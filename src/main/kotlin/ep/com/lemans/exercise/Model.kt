package ep.com.lemans.exercise

data class Part(
    val punctuatedPartNumber: String,
    val partDescription: String,
    val productId: Int,
    val originalRetailPrice: Double,
    val branName: String,
    val imageUrl: String
) {}

data class Product(val productId: Int, val productName: String, val categoryName: String) {
    lateinit var parts: List<Part>
}