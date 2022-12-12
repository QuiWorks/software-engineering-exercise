package ep.com.lemans.exercise

sealed interface Model {
    val productId: Int
}

data class Part(
    val punctuatedPartNumber: String,
    val partDescription: String,
    override val productId: Int,
    val originalRetailPrice: Double,
    val brandName: String,
    val imageUrl: String
) : Model

data class Product(override val productId: Int, val productName: String, val categoryName: String) : Model {
    lateinit var parts: MutableSet<Part>
    fun addPart(part: Part): Product {
        if (this::parts.isInitialized) parts.add(part)
        else parts = mutableSetOf(part)
        return this;
    }
}