package ep.com.lemans.exercise

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

/**
 * Database constants.
 */
private const val JDBC_URL = "jdbc:postgresql://localhost:5432/exercise"
private const val USER = "exercise"
private const val PRODUCT = "product"
private const val PART = "part"
private const val PASSWORD = "password"
internal const val PRODUCT_ID = "product_id"
internal const val PRODUCT_NAME = "product_name"
internal const val CATEGORY_NAME = "category_name"
internal const val PUNCTUATED_PART_NUMBER = "punctuated_part_number"
internal const val PART_DESCRIPTION = "part_description"
internal const val ORIGINAL_RETAIL_PRICE = "original_retail_price"
internal const val BRAND_NAME = "brand_name"
internal const val IMAGE_URL = "image_url"
private const val FETCH_QUERY =
    "select $PRODUCT.$PRODUCT_ID, $PRODUCT_NAME, $CATEGORY_NAME, $PUNCTUATED_PART_NUMBER, $PART_DESCRIPTION, $ORIGINAL_RETAIL_PRICE, $BRAND_NAME, $IMAGE_URL " +
            "from $PRODUCT " +
            "         inner join $PART on $PRODUCT.$PRODUCT_ID = $PART.$PRODUCT_ID " +
            "order by $PRODUCT.$PRODUCT_ID;"

/**
 * Interface for interacting with the database.
 */
interface DatabaseService {
    val connectionService: ConnectionService

    /**
     * Clears and reloads all data in the database.
     */
    fun refresh()

    /**
     * Retrieves all data in the database.
     */
    fun fetch(): Set<Product>
}

/**
 * Implementation of [[DatabaseService]] using JDBC.
 */
@Component
class JdbcDatabaseService(@Autowired val fileService: FileService, @Autowired override val connectionService: ConnectionService) : DatabaseService {

    override fun refresh() {
        clearData()
        insertProducts(fileService.readProducts())
        insertParts(fileService.readParts())
    }

    override fun fetch(): Set<Product> {
        val products: MutableSet<Product> = mutableSetOf()
        connectionService.getConnection().createStatement().executeQuery(FETCH_QUERY).use { resultSet ->
            resultSet.iterator().forEach { row ->
                val key = row.getInt(PRODUCT_ID)
                val part = toPart(row)
                if (products.containsKey(key)) products.findByKey(key)?.addPart(part)
                else products.add(toProduct(row).addPart(part))
            }
        }
        return products
    }

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
        with(connectionService.getConnection()) {
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
        with(connectionService.getConnection()) {
            createStatement().execute(sql)
        }
    }

    /**
     * Clears all data from the database.
     * part.product_id is defined as cascade on delete.
     */
    private fun clearData() {
        val sql = "DELETE FROM product"
        with(connectionService.getConnection()) {
            createStatement().execute(sql)
        }
    }

    private fun toProduct(row: ResultSet): Product {
        return Product(row.getInt(PRODUCT_ID), row.getString(PRODUCT_NAME), row.getString(CATEGORY_NAME))
    }

    private fun toPart(row: ResultSet): Part {
        return Part(
            row.getString(PUNCTUATED_PART_NUMBER),
            row.getString(PART_DESCRIPTION),
            row.getInt(PRODUCT_ID),
            row.getDouble(ORIGINAL_RETAIL_PRICE),
            row.getString(BRAND_NAME),
            row.getString(IMAGE_URL)
        )

    }
}

interface ConnectionService {
    fun getConnection(): Connection
}

@Component
class RealConnectionService: ConnectionService {
    override fun getConnection(): Connection {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD)!!
    }

}

fun <T : Model> MutableSet<T>.containsKey(key: Int): Boolean = any { it.productId == key }
fun <T : Model> MutableSet<T>.findByKey(key: Int): T? = find { it.productId == key }

/**
 * Creates an iterator through a [[ResultSet]]
 */
operator fun ResultSet.iterator(): Iterator<ResultSet> {
    val rs = this
    return object : Iterator<ResultSet> {
        override fun hasNext(): Boolean = rs.next()

        override fun next(): ResultSet = rs
    }
}