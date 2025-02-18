package spontaniius.data.remote.models

data class PromotionCreateRequest(
    val promotionAddress: List<Double>,
    val promotionTitle: String,
    val promotionText: String,
    val icon: String = "",  // Optional
    val background: String = "",  // Optional
    val background_address: String = ""  // Optional
)
