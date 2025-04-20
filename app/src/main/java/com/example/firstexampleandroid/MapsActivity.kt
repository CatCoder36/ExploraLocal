package com.example.firstexampleandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.firstexampleandroid.models.Place
import com.example.firstexampleandroid.ui.PlaceFormBottomSheet
import com.example.firstexampleandroid.utils.LocationHelper
import com.example.firstexampleandroid.utils.PhotoManager
import com.example.firstexampleandroid.viewmodel.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewMap: GoogleMap
    private lateinit var locationHelper: LocationHelper
    private lateinit var photoManager: PhotoManager
    private lateinit var placeFormBottomSheet: PlaceFormBottomSheet
    
    private val viewModel: PlacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Inicializar helpers
        locationHelper = LocationHelper(this)
        photoManager = PhotoManager(this)
        placeFormBottomSheet = PlaceFormBottomSheet(this, photoManager) { place ->
            viewModel.addPlace(place)
        }
        
        setupObservers()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupObservers() {
        viewModel.allPlaces.observe(this) { places ->
            updateMapWithPlaces(places)
        }
    }

    private fun updateMapWithPlaces(places: List<Place>) {
        if (::viewMap.isInitialized) {
            for (place in places) {
                val position = LatLng(place.latitude, place.longitude)
                viewMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(place.name)
                        .snippet(place.description)
                )
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        viewMap = googleMap

        viewMap.setOnMapClickListener { latLng ->
            placeFormBottomSheet.show(latLng)
        }
        
        locationHelper.checkLocationPermission {
            locationHelper.enableMyLocation(viewMap)
        }
    }
}