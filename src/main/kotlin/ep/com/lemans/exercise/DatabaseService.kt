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
class JdbcDatabaseService(
    @Autowired val fileService: FileService,
    @Autowired override val connectionService: ConnectionService,
) : DatabaseService {

    override fun refresh() {
        clearData()
        insertProducts(fileService.readProducts())
        insertParts(fileService.readParts())
    }

    override fun fetch(): Set<Product> {
        connectionService.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(FETCH_QUERY).use { resultSet ->
                    return mapResultSet(resultSet)
                }
            }
        }
    }

    internal fun mapResultSet(resultSet: ResultSet): Set<Product> {
        return resultSet.fold(setOf()) { products, row ->
            if (!products.containsKey(row.getKey())) products + row.toProduct().add(row.toPart())
            else {
                with(products) {
                    findByKey(row.getKey())?.add(row.toPart())
                    this
                }
            }
        }
    }

    /**
     * Creates and executes bulk insert query for products table.
     */
    private fun insertProducts(products: List<Product>) {
        connectionService.execute(buildString {
            append("INSERT INTO $PRODUCT VALUES ")
            val bulk = products.asSequence().map {
                val (productId, productName, categoryName) = it
                "('$productId', '$productName', '$categoryName')"
            }.toList().joinToString { it }
            append(bulk)
        })
    }

    /**
     * Creates and executes bulk insert query for parts table.
     */
    private fun insertParts(parts: List<Part>) {
        connectionService.execute(buildString {
            append("INSERT INTO $PART VALUES ")
            val bulk = parts.asSequence().map {
                val (punctuatedPartNumber, partDescription, productId, originalRetailPrice, branName, imageUrl) = it
                "('$punctuatedPartNumber', '$partDescription', '$productId', '$originalRetailPrice','$branName','$imageUrl')"
            }.toList().joinToString { it }
            append(bulk)
        })
    }

    /**
     * Clears all data from the database.
     * part.product_id is defined as cascade on delete.
     */
    private fun clearData() {
        connectionService.execute("DELETE FROM product")
    }

}

interface ConnectionService {
    fun getConnection(): Connection {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD)!!
    }

    fun execute(query: String) {
        getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.execute(query)
            }
        }
    }
}

@Component
class ConnectionServiceImpl : ConnectionService

/**
 * Extension functions for [[Set]].
 */
fun <T : Model> Set<T>.containsKey(key: Int): Boolean = any { it.productId == key }
fun <T : Model> Set<T>.findByKey(key: Int): T? = find { it.productId == key }


/**
 * Extension functions for [[ResultSet]]
 */
inline fun <R> ResultSet.fold(initial: R, operation: (acc: R, ResultSet) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

operator fun ResultSet.iterator(): Iterator<ResultSet> {
    val rs = this
    return object : Iterator<ResultSet> {
        override fun hasNext(): Boolean = rs.next()

        override fun next(): ResultSet = rs
    }
}

fun ResultSet.toPart(): Part {
    return Part(
        getString(PUNCTUATED_PART_NUMBER),
        getString(PART_DESCRIPTION),
        getInt(PRODUCT_ID),
        getDouble(ORIGINAL_RETAIL_PRICE),
        getString(BRAND_NAME),
        getString(IMAGE_URL)
    )
}

fun ResultSet.getKey(): Int {
    return getInt(PRODUCT_ID);
}

fun ResultSet.toProduct(): Product {
    return Product(getInt(PRODUCT_ID), getString(PRODUCT_NAME), getString(CATEGORY_NAME))
}