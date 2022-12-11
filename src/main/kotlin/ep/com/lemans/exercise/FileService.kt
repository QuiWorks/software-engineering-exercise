package ep.com.lemans.exercise

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

interface FileService {
    val partFileLocation: String
    val productFileLocation: String
    fun readProductFile(): List<Product>
    fun readPartFile(): List<Part>
}

class CsvService(override val partFileLocation: String, override val productFileLocation: String) : FileService {
    override fun readProductFile(): List<Product> {
        return getLineSequence(productFileLocation)
            .map {
                val (productId, productName, categoryName) = it.split(',', ignoreCase = false, limit = 3)
                Product(productId.toInt(), productName, categoryName)
            }.toList()
    }

    override fun readPartFile(): List<Part> {
        return getLineSequence(partFileLocation)
            .map {
                val (punctuatedPartNumber, partDescription, productId, originalRetailPrice, brandName, imageUrl ) = it.split(',', ignoreCase = false, limit = 6)
                Part(punctuatedPartNumber.replace(oldValue = "\"", newValue = ""), partDescription, productId.toInt(), originalRetailPrice.toDouble(), brandName, imageUrl)
            }.toList()
    }

    private fun getLineSequence(fileLocation: String): Sequence<String>
    {
        val reader = FileService::class.java.getResourceAsStream(fileLocation)?.bufferedReader()!!
        reader.readLine()
        return reader.lineSequence().filter { it.isNotBlank() };
    }

}

@Configuration
class FileServiceConfig {
    @Bean
    fun fileService(): FileService
    {
        return CsvService("/data/parts.csv","/data/products.csv")
    }
}

// Needed for destructing parts csv file.
operator fun <T> List<T>.component6(): T = get(5)


