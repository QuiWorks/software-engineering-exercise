package ep.com.lemans.exercise

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.BufferedReader

/**
 * File constants.
 */
private const val PART_FILE_PATH = "/data/parts.csv"
private const val PRODUCT_FILE_PATH = "/data/products.csv"

/**
 * Interface for interacting with data stored in files.
 */
interface FileService {
    val bufferedReaderService: BufferedReaderService
    /**
     * Instantiates a list of Products from a data stream.
     */
    fun readProducts(path: String = PRODUCT_FILE_PATH): List<Product>

    /**
     * Instantiates a list of Parts from a data stream.
     */
    fun readParts(path: String = PART_FILE_PATH): List<Part>
}

interface BufferedReaderService {
    fun get(path: String = PRODUCT_FILE_PATH): BufferedReader {
        return BufferedReaderService::class.java.getResourceAsStream(path)?.bufferedReader()!!
    }
}

@Component
class BufferedReaderServiceImpl: BufferedReaderService

/**
 * Implementation of [[FileService]] for comma seperated value(CSV) files.
 */
@Component
class CsvService(@Autowired override val bufferedReaderService: BufferedReaderService): FileService {
    override fun readProducts(path: String): List<Product> {
        return parseModel(path, this::readProduct)
    }

    override fun readParts(path: String): List<Part> {
        return parseModel(path, this::readPart)
    }

    private fun readProduct(it: String): Product {
        val (productId, productName, categoryName) = it.split(',', ignoreCase = false, limit = 3)
        return Product(productId.toInt(), productName, categoryName)
    }

    private fun readPart(it: String): Part {
        val (punctuatedPartNumber, partDescription, productId, originalRetailPrice, brandName, imageUrl) = it.split(
            ',',
            ignoreCase = false,
            limit = 6
        )
        return Part(
            punctuatedPartNumber.clean(),
            partDescription,
            productId.toInt(),
            originalRetailPrice.toDouble(),
            brandName,
            imageUrl
        )
    }

    private fun <M: Model> parseModel(path: String, mapping: (input: String) -> M): List<M> {
        bufferedReaderService.get(path).use {
            it.readLine()
            return it.lineSequence()
                .filter { line -> line.isNotBlank() }
                .map { line -> mapping(line) }
                .toList()
        }
    }

}

fun String.clean(): String {
    return replace(oldValue = "\"", newValue = "")
}

operator fun <T> List<T>.component6(): T = get(5)


