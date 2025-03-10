package spontaniius.domain.models

data class Card(
    val id: Int,
    val userId: String,
    val name: String,
    val phone: String,
    val background: Int?,
    val greeting: String?
)
