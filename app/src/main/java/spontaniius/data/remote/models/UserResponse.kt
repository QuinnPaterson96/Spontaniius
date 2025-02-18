package spontaniius.data.remote.models

data class UserResponse(
    val id: String,
    val name: String,
    val phone: String,
    val gender: String?,
    val external_id: String?,
    val auth_provider: String?
)
