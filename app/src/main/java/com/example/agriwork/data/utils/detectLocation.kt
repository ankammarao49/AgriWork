import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import com.google.android.gms.location.*
import java.util.Locale

@SuppressLint("MissingPermission")
fun detectLocation(
    context: Context,
    onLocationDetected: (String) -> Unit,
    onError: (String) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Request fresh high-accuracy location
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        2000 // update interval (2 sec)
    ).setMaxUpdates(1).build()

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    reverseGeocode(context, location, onLocationDetected, onError)
                } else {
                    onError("Fresh location not found")
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        },
        Looper.getMainLooper()
    )
}

private fun reverseGeocode(
    context: Context,
    location: Location,
    onLocationDetected: (String) -> Unit,
    onError: (String) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // ✅ API 33+ async version
        geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<android.location.Address>) {
                    if (addresses.isNotEmpty()) {
                        val addr = addresses[0]
                        onLocationDetected(formatAddress(addr))
                    } else {
                        onError("Unable to fetch address")
                    }
                }

                override fun onError(errorMessage: String?) {
                    onError(errorMessage ?: "Failed to detect address")
                }
            }
        )
    } else {
        // 🕰 Older devices — blocking call on background thread
        Thread {
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    onLocationDetected(formatAddress(addr))
                } else {
                    onError("Unable to fetch address")
                }
            } catch (e: Exception) {
                onError("Geocoder failed: ${e.localizedMessage}")
            }
        }.start()
    }
}

private fun formatAddress(addr: android.location.Address): String {
    return listOfNotNull(
        addr.locality,   // City/town
        addr.adminArea   // State
    ).joinToString(", ")
}
