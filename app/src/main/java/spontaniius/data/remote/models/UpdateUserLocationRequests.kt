package spontaniius.data.remote.models
import com.google.gson.annotations.SerializedName

data class UpdateUserLocationRequest(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)
