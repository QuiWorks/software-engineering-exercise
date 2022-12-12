package ep.com.lemans.exercise

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.io.BufferedReader

/**
 * File constants.
 */
private const val PART_FILE_PATH = "/data/parts.csv"
private const val PRODUCT_FILE_PATH = "/data/products.csv"
private const val PART_STREAM = "partsStream"
private const val PRODUCT_STREAM = "productsStream"

/**
 * Interface for interacting with data stored in files.
 */
interface FileService {
    /**
     * The stream containing the part data.
     */
    val partsStream: BufferedReader

    /**
     * The stream containing the product data.
     */
    val productsStream: BufferedReader

    /**
     * Instantiates a list of Products from a data stream.
     */
    fun readProducts(): List<Product>

    /**
     * Instantiates a list of Parts from a data stream.
     */
    fun readParts(): List<Part>
}

/**
 * Implementation of [[FileService]] for comma seperated value(CSV) files.
 */
@Component
class CsvService(
    @Qualifier(PART_STREAM) override val partsStream: BufferedReader,
    @Qualifier(PRODUCT_STREAM) override val productsStream: BufferedReader
) : FileService {
    override fun readProducts(): List<Product> {
        return getLineSequence(productsStream)
            .map { readProduct(it) }.toList()
    }

    override fun readParts(): List<Part> {
        return getLineSequence(partsStream)
            .map { readPart(it) }.toList()
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
            punctuatedPartNumber.replace(oldValue = "\"", newValue = ""),
            partDescription,
            productId.toInt(),
            originalRetailPrice.toDouble(),
            brandName,
            imageUrl
        )
    }

    private fun getLineSequence(reader: BufferedReader): Sequence<String> {
        reader.readLine()
        return reader.lineSequence().filter { it.isNotBlank() };
    }

}

@Configuration
class Config {
    @Bean
    @Qualifier(PART_STREAM)
    fun partsStream(): BufferedReader {
        return getBufferedReader(PART_FILE_PATH)!!
    }

    @Bean
    @Qualifier(PRODUCT_STREAM)
    fun productsStream(): BufferedReader {
        return getBufferedReader(PRODUCT_FILE_PATH)!!
    }

}

internal fun getBufferedReader(path: String) = Config::class.java.getResourceAsStream(path)?.bufferedReader()
operator fun <T> List<T>.component6(): T = get(5)


