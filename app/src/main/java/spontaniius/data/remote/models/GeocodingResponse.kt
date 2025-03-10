package spontaniius.data.remote.models

data class GeocodingResponse(
    val results: List<GeocodingResult>?,
    val status: String
)

data class GeocodingResult(
    val formatted_address: String,
    val address_components: List<AddressComponent> // ✅ Include more fields if needed
)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)
