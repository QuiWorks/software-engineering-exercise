package ep.com.lemans.exercise

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.FileReader
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

private const val PUNCTUATED_PART_NUMBER = "0101-3337"
private const val PART_DESC = "HELMET FX90 BLACK XS"
private const val PRODUCT_ID_VALUE = 1417
private const val PRICE = 89.95
private const val BRAND = "AFX"
private const val IMAGE = "https://asset.lemansplatform.com/media/edge/4/7/2/472D5DEF-8374-45C6-A2B6-7BBADB9A7C77.png"
private const val PRODUCT_NAME = "AX-8 Carbon Helmets"
private const val CATEGORY = "Adult MX Helmets"
private const val PART_STRING = "\n\"$PUNCTUATED_PART_NUMBER\",$PART_DESC,$PRODUCT_ID_VALUE,$PRICE,$BRAND,$IMAGE"
private const val PRODUCT_STRING = "\n$PRODUCT_ID_VALUE,$PRODUCT_NAME,$CATEGORY"
private const val PART_SOURCE_FILE = "src/main/resources/data/parts.csv"
private const val PRODUCT_SOURCE_FILE = "src/main/resources/data/products.csv"

internal class CsvServiceTest {

    private val service = CsvService(BufferedReader(StringReader(PART_STRING)), BufferedReader(StringReader(PRODUCT_STRING)))

    @Test
    fun `CSV service correctly parses products`() {
        val products = service.readProducts()
        Assertions.assertEquals(1, products.size, "Number of products is not correct.")
        val (id, name, category) = products[0]
        Assertions.assertEquals(PRODUCT_ID_VALUE, id, "Product id is incorrect")
        Assertions.assertEquals(PRODUCT_NAME, name, "Product name is incorrect")
        Assertions.assertEquals(CATEGORY, category, "Category name is incorrect")
    }

    @Test
    fun `CSV service correctly parses parts`() {
        val parts = service.readParts()
        Assertions.assertEquals(1, parts.size, "Number of parts is not correct.")
        val (number, desc, id, price, brand, image) = parts[0]
        Assertions.assertEquals(PUNCTUATED_PART_NUMBER, number, "Part number is incorrect")
        Assertions.assertEquals(PART_DESC, desc, "Part description is incorrect")
        Assertions.assertEquals(PRODUCT_ID_VALUE, id, "Part id is incorrect")
        Assertions.assertEquals(PRICE, price, "Part price is incorrect")
        Assertions.assertEquals(BRAND, brand, "Part brand is incorrect")
        Assertions.assertEquals(IMAGE, image, "Part image is incorrect")
    }

    @Test
    fun `CSV service parses all products`() {
        val count = Files.lines(Path.of(PRODUCT_SOURCE_FILE), StandardCharsets.UTF_8).count() -1 // Exclude header line from count.
        val service = CsvService(BufferedReader(StringReader(PART_STRING)), BufferedReader(FileReader(PRODUCT_SOURCE_FILE)))
        Assertions.assertEquals(count.toInt(), service.readProducts().size, "Number of products is not correct.")
    }


    @Test
    fun `CSV service parses all parts`() {
        val count = Files.lines(Path.of(PART_SOURCE_FILE), StandardCharsets.UTF_8).count() -1 // Exclude header line from count.
        val service = CsvService(BufferedReader(FileReader(PART_SOURCE_FILE)), BufferedReader(StringReader(PRODUCT_STRING)))
        Assertions.assertEquals(count.toInt(), service.readParts().size, "Number of parts is not correct.")
    }

}