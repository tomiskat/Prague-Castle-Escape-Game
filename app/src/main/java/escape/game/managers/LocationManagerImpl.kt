package escape.game.managers

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import escape.game.utils.Constants.LOCATION_INTERVAL_UPDATE

class LocationManagerImpl(private val fusedLocationClient: FusedLocationProviderClient, private val locationUpdateCallback: (Location) -> Unit) :
    LocationManager {

    private var locationRequest: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_INTERVAL_UPDATE).apply {
        setMinUpdateIntervalMillis(LOCATION_INTERVAL_UPDATE)
        setWaitForAccurateLocation(true)
    }.build()

    private val locationCallbackImpl = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { location -> locationUpdateCallback(location) }
        }
    }

    // Permission checked in the GameActivity
    @SuppressLint("MissingPermission")
    override fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallbackImpl, null)
    }

    override fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallbackImpl)
    }
}