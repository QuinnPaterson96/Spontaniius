package spontaniius.data.remote.models

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

// 📌 Request Model
data class AutocompleteRequest(
    @SerializedName("input") val input: String,
    @SerializedName("sessionToken") val sessionToken: String,
    @SerializedName("locationBias") val locationBias: LocationBias? = null // ✅ Optional location bias
)

// 📌 Location Bias Model
data class LocationBias(
    @SerializedName("circle") val circle: Circle
)

// 📌 Circle Model (Defines Bias Area)
data class Circle(
    @SerializedName("center") val center: LatLngWrapper, // ✅ Uses wrapper for Google LatLng
    @SerializedName("radius") val radius: Double // ✅ Radius in meters (default 50000)
)

// 📌 LatLng Wrapper (to correctly serialize Google Maps LatLng)
data class LatLngWrapper(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
) {
    constructor(latLng: LatLng) : this(latLng.latitude, latLng.longitude) // ✅ Accepts Google Maps LatLng
}
// 📌 Response Model
data class AutocompleteResponse(
    @SerializedName("suggestions") val suggestions: List<PlaceSuggestion>
)

data class PlaceSuggestion(
    @SerializedName("placePrediction") val placePrediction: PlacePrediction
)

data class PlacePrediction(
    @SerializedName("placeId") val placeId: String, // ✅ Place ID
    @SerializedName("text") val text_field: PlaceText // ✅ Full location name
)

data class PlaceText(
    @SerializedName("text") val fullText: String // ✅ "Le Far Breton, Rue Bélanger, Montreal, QC, Canada"
)

data class PlaceDetailsRequest(
    @SerializedName("placeId") val placeId: String
)

data class PlaceDetailsResponse(
    @SerializedName("location") val location: LocationData,
    @SerializedName("formattedAddress") val formattedAddress: String
)

data class LocationData(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)