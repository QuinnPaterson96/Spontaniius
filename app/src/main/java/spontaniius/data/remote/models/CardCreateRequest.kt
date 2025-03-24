package spontaniius.data.remote.models

data class CardCreateRequest(
    val user_id: String,
    val user_name: String,
    val card_text: String?,
    val background: String? = "",  // Optional
    val background_address: String? = ""  // Optional
)

