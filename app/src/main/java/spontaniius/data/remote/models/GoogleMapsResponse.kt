package spontaniius.data.remote.models


data class GoogleMapsResponse(
    val results: List<ResultData>
)

data class ResultData(
    val geometry: Geometry
)

data class Geometry(
    val location: LatLngResponse
)

data class LatLngResponse(
    val lat: Double,
    val lng: Double
)
