package spontaniius.data.remote.models

data class CreateUserRequest(
    val name: String,
    val phone: String,
    val gender: String? = null,  // Optional
    val card_id: String? = null,  // Optional
    val external_id: String? = null,
    val auth_provider: String? = null
)
