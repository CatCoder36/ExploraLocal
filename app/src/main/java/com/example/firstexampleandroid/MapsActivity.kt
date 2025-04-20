package com.example.firstexampleandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.firstexampleandroid.models.Place
import com.example.firstexampleandroid.ui.PlaceFormBottomSheet
import com.example.firstexampleandroid.ui.PlacesListFragment
import com.example.firstexampleandroid.utils.LocationHelper
import com.example.firstexampleandroid.utils.PhotoManager
import com.example.firstexampleandroid.viewmodel.PlacesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewMap: GoogleMap
    private lateinit var locationHelper: LocationHelper
    private lateinit var photoManager: PhotoManager
    private lateinit var placeFormBottomSheet: PlaceFormBottomSheet

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var placesListFragment: PlacesListFragment
    
    
    private val viewModel: PlacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Initialize helpers
        locationHelper = LocationHelper(this)
        photoManager = PhotoManager(this)
        placeFormBottomSheet = PlaceFormBottomSheet(this, photoManager) { place ->
            viewModel.addPlace(place)
        }
        
        // Initialize fragments
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        placesListFragment = PlacesListFragment()
        
        setupObservers()
        setupBottomNavigation()

        mapFragment.getMapAsync(this)
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_map -> {
                    showFragment(mapFragment)
                    true
                }
                R.id.navigation_places -> {
                    showFragment(placesListFragment)
                    true
                }
                else -> false
            }
        }
        
        // Default to map view
        bottomNavigation.selectedItemId = R.id.navigation_map
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            if (fragment is SupportMapFragment) {
                hide(placesListFragment)
                show(mapFragment)
            } else {
                if (!fragment.isAdded) {
                    add(R.id.fragment_container, fragment)
                }
                hide(mapFragment)
                show(fragment)
            }
            commit()
        }
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

        viewModel.allPlaces.value?.let { updateMapWithPlaces(it) }
    }
}