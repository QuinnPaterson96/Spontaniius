package spontaniius.data.remote.models

import com.google.gson.annotations.SerializedName

data class FindEventRequest(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("gender") val gender: String = "Any", // Default to "Any"
    @SerializedName("user_id") val userId: String
)
