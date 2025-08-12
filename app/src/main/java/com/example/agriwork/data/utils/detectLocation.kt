import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

@SuppressLint("MissingPermission")
fun detectLocation(
    context: Context,
    onLocationDetected: (String) -> Unit,
    onError: (String) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                val geocoder = Geocoder(context, Locale.getDefault())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // ✅ API 33+
                    geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<android.location.Address>) {
                                if (addresses.isNotEmpty()) {
                                    val addr = addresses[0]
                                    val locationText = formatAddress(addr)
                                    onLocationDetected(locationText)
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
                    // 🕰 Older devices — run blocking call on background thread
                    Thread {
                        try {
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val addr = addresses[0]
                                val locationText = formatAddress(addr)
                                onLocationDetected(locationText)
                            } else {
                                onError("Unable to fetch address")
                            }
                        } catch (e: Exception) {
                            onError("Geocoder failed: ${e.localizedMessage}")
                        }
                    }.start()
                }
            } else {
                onError("Location not found")
            }
        }
        .addOnFailureListener {
            onError("Failed to detect location: ${it.localizedMessage}")
        }
}

// Helper to format the address
private fun formatAddress(addr: android.location.Address): String {
    return listOfNotNull(
        addr.featureName,   // House/landmark
        addr.subLocality,   // Colony/inner area
        addr.locality,      // City
        addr.subAdminArea,  // District
        addr.adminArea,     // State
        addr.postalCode     // PIN
    ).joinToString(", ")
}
