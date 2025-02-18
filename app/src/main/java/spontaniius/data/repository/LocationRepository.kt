package spontaniius.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.LatLngResponse
import javax.inject.Inject
import javax.inject.Singleton

class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val remoteDataSource: RemoteDataSource
) {
    private val locationAPIKey="AIzaSyDftsoTlkMRu33vd6FLeWh-rzc0p0Ttt6k"// TODO Maybe have this stored in secrets



    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun fetchLastKnownLocation(onSuccess: (LatLng) -> Unit, onFailure: (Exception) -> Unit) {
        if (!hasLocationPermission()) {
            onFailure(SecurityException("Location permissions not granted"))
            return
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                onSuccess(latLng)
            } ?: onFailure(Exception("Location not found"))
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    suspend fun getLocationFromAddress(address: String): Result<LatLng> {
        return try {
            val apiKey = "YOUR_GOOGLE_MAPS_API_KEY"
            val response = remoteDataSource.getLocationFromAddress(address, apiKey)

            response.map { location ->
                LatLng(location.latitude, location.longitude)
            }
        } catch (e: Exception) {
            Log.e("LocationRepository", "Error fetching location: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

}


