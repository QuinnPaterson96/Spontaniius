package spontaniius.domain.models

data class User(
    val id: String,
    val name: String,
    val phone: String?,
    val gender: String?,
    val cardId: Int?,
    val externalId: String?,
    val authProvider: String?
)
