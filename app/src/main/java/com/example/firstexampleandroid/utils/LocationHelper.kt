package com.example.firstexampleandroid.utils

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
}