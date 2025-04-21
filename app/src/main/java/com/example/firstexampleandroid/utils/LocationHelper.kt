package com.example.firstexampleandroid.utils

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

/**
 * Helper class for managing location permissions and Google Maps location features.
 * This class handles permission requests, location access, and map camera positioning
 * based on the user's current location.
 */
class LocationHelper(private val context: AppCompatActivity) {
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationPermissionCallback: (() -> Unit)? = null
    private var locationCallback: LocationCallback? = null
    
    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 10000L // 10 seconds
    }
    
    
    /**
     * Permission launcher for requesting location access.
     * Handles the result of the permission request and invokes the callback
     * if permission is granted or displays a toast message if denied.
     */
    val locationPermissionLauncher: ActivityResultLauncher<String> = context.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            locationPermissionCallback?.invoke()
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    
    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }
    
    /**
     * Checks if the location permission is granted and requests it if necessary.
     * 
     * @param onPermissionGranted Callback function to execute when the location permission is granted
     */
    fun checkLocationPermission(onPermissionGranted: () -> Unit) {
        locationPermissionCallback = onPermissionGranted
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onPermissionGranted()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    
    /**
     * Enables the "My Location" layer on the provided Google Map and centers the camera
     * on the user's current location with an appropriate zoom level.
     * 
     * @param googleMap The Google Map instance on which to enable the location features
     */
    fun enableMyLocation(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }
    
    /**
     * Checks if the application has been granted location permission.
     * 
     * @return Boolean indicating whether the location permission has been granted (true) or not (false)
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

     /**
     * Requests periodic location updates and passes them to the provided callback.
     *
     * @param onLocationUpdate Callback function that will be called when a new location is available
     */
    fun requestLocationUpdates(onLocationUpdate: (Location) -> Unit) {
        if (!hasLocationPermission()) {
            return
        }
        
        // Get last known location first
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                onLocationUpdate(it)
            }
        }
        
        // Then set up continuous updates
        val locationRequest = LocationRequest.create().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = LOCATION_UPDATE_INTERVAL / 2
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onLocationUpdate(location)
                }
            }
        }
        
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationCallback?.let {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    it,
                    Looper.getMainLooper()
                )
            }
        }
    }
    
    /**
     * Stops location updates to conserve battery.
     * Should be called in onDestroy() of the activity or fragment.
     */
    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }
}