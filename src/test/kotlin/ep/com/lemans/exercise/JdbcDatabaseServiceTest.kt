package ep.com.lemans.exercise

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import java.sql.ResultSet

private const val PUNCTUATED_PART_NUMBER_VALUE_1 = "0101-3337"
private const val PUNCTUATED_PART_NUMBER_VALUE_2 = "0101-3338"
private const val PART_DESC_VALUE = "HELMET FX90 BLACK XS"
private const val PRODUCT_ID_VALUE = 1417
private const val PRICE_VALUE = 89.95
private const val BRAND_VALUE = "AFX"
private const val IMAGE_VALUE = "https://asset.lemansplatform.com/media/edge/4/7/2/472D5DEF-8374-45C6-A2B6-7BBADB9A7C77.png"
private const val PRODUCT_NAME_VALUE = "AX-8 Carbon Helmets"
private const val CATEGORY_VALUE = "Adult MX Helmets"

internal class JdbcDatabaseServiceTest {

    private val mockConnectionService: ConnectionService = mock()
    private val mockResultSet: ResultSet = mock()
    private val mockFileService: FileService = mock()
    private val service = JdbcDatabaseService(mockFileService, mockConnectionService)

    @BeforeEach
    fun setUp() {
        Mockito.`when`(mockConnectionService.getResultSet(Mockito.anyString())).thenReturn(mockResultSet)
        Mockito.`when`(mockResultSet.getInt(PRODUCT_ID)).thenReturn(PRODUCT_ID_VALUE)
        Mockito.`when`(mockResultSet.getString(PUNCTUATED_PART_NUMBER)).thenReturn(PUNCTUATED_PART_NUMBER_VALUE_1,PUNCTUATED_PART_NUMBER_VALUE_2)
        Mockito.`when`(mockResultSet.getString(PART_DESCRIPTION)).thenReturn(PART_DESC_VALUE)
        Mockito.`when`(mockResultSet.getDouble(ORIGINAL_RETAIL_PRICE)).thenReturn(PRICE_VALUE)
        Mockito.`when`(mockResultSet.getString(BRAND_NAME)).thenReturn(BRAND_VALUE)
        Mockito.`when`(mockResultSet.getString(IMAGE_URL)).thenReturn(IMAGE_VALUE)
        Mockito.`when`(mockResultSet.getString(PRODUCT_NAME)).thenReturn(PRODUCT_NAME_VALUE)
        Mockito.`when`(mockResultSet.getString(CATEGORY_NAME)).thenReturn(CATEGORY_VALUE)
        Mockito.`when`(mockResultSet.next()).thenReturn(true, true, false)

    }

    @Test
    fun `Jdbc service adds parts to product list`() {
        val products = service.fetch()
        Assertions.assertEquals(1, products.size)
        val product = products.first()
        Assertions.assertEquals(PRODUCT_ID_VALUE, product.productId, "Product id is incorrect")
        Assertions.assertEquals(PRODUCT_NAME_VALUE, product.productName, "Product name is incorrect")
        Assertions.assertEquals(CATEGORY_VALUE, product.categoryName, "Category name is incorrect")
        Assertions.assertEquals(2, product.parts.size, "Number of parts is not correct.")
        val part1 = product.parts.first()
        Assertions.assertEquals(PUNCTUATED_PART_NUMBER_VALUE_1, part1.punctuatedPartNumber, "Part number is incorrect")
        Assertions.assertEquals(PART_DESC_VALUE, part1.partDescription, "Part description is incorrect")
        Assertions.assertEquals(PRODUCT_ID_VALUE, part1.productId, "Part id is incorrect")
        Assertions.assertEquals(PRICE_VALUE, part1.originalRetailPrice, "Part price is incorrect")
        Assertions.assertEquals(BRAND_VALUE, part1.brandName, "Part brand is incorrect")
        Assertions.assertEquals(IMAGE_VALUE, part1.imageUrl, "Part image is incorrect")
        val part2 = product.parts.elementAt(1)
        Assertions.assertEquals(PUNCTUATED_PART_NUMBER_VALUE_2, part2.punctuatedPartNumber, "Part number is incorrect")
        Assertions.assertEquals(PART_DESC_VALUE, part2.partDescription, "Part description is incorrect")
        Assertions.assertEquals(PRODUCT_ID_VALUE, part2.productId, "Part id is incorrect")
        Assertions.assertEquals(PRICE_VALUE, part2.originalRetailPrice, "Part price is incorrect")
        Assertions.assertEquals(BRAND_VALUE, part2.brandName, "Part brand is incorrect")
        Assertions.assertEquals(IMAGE_VALUE, part2.imageUrl, "Part image is incorrect")
    }
}
