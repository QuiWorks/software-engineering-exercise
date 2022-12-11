package ep.com.lemans.exercise

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager

interface DataService {
    fun refresh()
    fun fetch(): List<Product>
}

@Component
class JdbcDataService(@Autowired val fileService: FileService) : DataService {

    override fun refresh() {
        clearData()
        insertProducts(fileService.readProductFile())
        insertParts(fileService.readPartFile())
    }

    override fun fetch(): List<Product> {
        val products = fetchProducts()
        val parts = fetchParts()
        products.forEach {
            it.parts = parts.asSequence().filter { part -> part.productId == it.productId }.toList()
        }
        return products
    }

    private fun fetchProducts(): List<Product> {
        return getConnection().createStatement().executeQuery("SELECT * FROM product").use {
            generateSequence {
                if(it.next()) Product(it.getInt(PRODUCT_ID),it.getString(PRODUCT_NAME),it.getString(CATEGORY_NAME)) else null
            }.toList()
        }
    }

    private fun fetchParts(): List<Part> {
        return getConnection().createStatement().executeQuery("SELECT * FROM part").use {
            generateSequence {
                if(it.next()) Part(it.getString(PUNCTUATED_PART_NUMBER),it.getString(PART_DESCRIPTION),it.getInt(PRODUCT_ID), it.getDouble(ORIGINAL_RETAIL_PRICE), it.getString(BRAND_NAME), it.getString(IMAGE_URL)) else null
            }.toList()
        }
    }

    private fun getConnection(): Connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)!!

    /**
     * Creates and executes bulk insert query for products table.
     */
    private fun insertProducts(products: List<Product>) {
        val sql = buildString {
            append("INSERT INTO $PRODUCT VALUES ")
            val bulk = products.asSequence().map {
                val (productId, productName, categoryName) = it
                "('$productId', '$productName', '$categoryName')"
            }.toList().joinToString { it }
            append(bulk)
        }
        with(getConnection()) {
            createStatement().execute(sql)
        }
    }

    /**
     * Creates and executes bulk insert query for parts table.
     */
    private fun insertParts(parts: List<Part>) {
        val sql = buildString {
            append("INSERT INTO $PART VALUES ")
            val bulk = parts.asSequence().map {
                val (punctuatedPartNumber, partDescription, productId, originalRetailPrice, branName, imageUrl) = it
                "('$punctuatedPartNumber', '$partDescription', '$productId', '$originalRetailPrice','$branName','$imageUrl')"
            }.toList().joinToString { it }
            append(bulk)
        }
        with(getConnection()) {
            createStatement().execute(sql)
        }
    }

    /**
     * Clears all data from the database.
     * part.product_id is defined as cascade on delete.
     */
    private fun clearData() {
        val sql = "DELETE FROM product"
        with(getConnection()) {
            createStatement().execute(sql)
        }
    }
}

private const val JDBC_URL = "jdbc:postgresql://localhost:5432/exercise"
private const val USER = "exercise"
private const val PRODUCT = "product"
private const val PART = "part"
private const val PASSWORD = "password"
private const val PRODUCT_ID = "product_id"
private const val PRODUCT_NAME = "product_name"
private const val CATEGORY_NAME = "category_name"
private const val PUNCTUATED_PART_NUMBER = "punctuated_part_number"
private const val PART_DESCRIPTION = "part_description"
private const val ORIGINAL_RETAIL_PRICE = "original_retail_price"
private const val BRAND_NAME = "brand_name"
private const val IMAGE_URL = "image_url"
