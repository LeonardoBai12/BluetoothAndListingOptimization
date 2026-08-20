package io.lb.bleandlistingopt.core.common

/**
 * Shared fake-listing model for both feature:listing:compose and
 * feature:listing:xml. Every property is a `val` of a primitive/String type,
 * which is what makes this class Compose-stable without needing an
 * `@Immutable` annotation (this module has no Compose dependency on purpose).
 */
data class ListingItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val category: String,
    val price: Double,
    val rating: Float,
    val isFavorite: Boolean,
    val colorSeed: Int,
)

/** Deterministic fake data so both list features render the same content. */
object FakeListingDataGenerator {
    private val categories = listOf("Books", "Toys", "Home", "Garden", "Tech", "Sports")

    fun generate(count: Int = 3000): List<ListingItem> {
        val random = kotlin.random.Random(seed = 42)
        return (1..count).map { id ->
            ListingItem(
                id = id.toLong(),
                title = "Item #$id",
                subtitle = "Fake listing entry $id",
                category = categories[id % categories.size],
                price = 5.0 + random.nextInt(500),
                rating = 1f + random.nextInt(4),
                isFavorite = id % 7 == 0,
                colorSeed = id,
            )
        }
    }
}
