package spontaniius.data.remote.models

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("auth_provider") val authProvider: String? = null,
    @SerializedName("card_id") val cardId: String? = null
)
