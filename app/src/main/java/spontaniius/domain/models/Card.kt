package spontaniius.domain.models

data class Card(
    val id: Int,
    val userId: String,
    val name: String,
    val background: String,
    val greeting: String?
)
