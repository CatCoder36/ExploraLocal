package com.example.firstexampleandroid

import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.firstexampleandroid.models.Place
import com.example.firstexampleandroid.ui.NearbyPlacesFragment
import com.example.firstexampleandroid.ui.PlaceFormBottomSheet
import com.example.firstexampleandroid.ui.PlacesListFragment
import com.example.firstexampleandroid.utils.LocationHelper
import com.example.firstexampleandroid.utils.PhotoManager
import com.example.firstexampleandroid.viewmodel.PlacesViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewMap: GoogleMap
    lateinit var locationHelper: LocationHelper
    private lateinit var placeFormBottomSheet: PlaceFormBottomSheet

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var placesListFragment: PlacesListFragment
    private lateinit var nearbyPlacesFragment: NearbyPlacesFragment

    lateinit var photoManager: PhotoManager
    
    var currentLocation: Location? = null
    
    val viewModel: PlacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Initialize helpers
        locationHelper = LocationHelper(this)
        photoManager = PhotoManager(this)

        // Initialize bottom sheets
        placeFormBottomSheet = PlaceFormBottomSheet(this, photoManager) { place ->
            viewModel.addPlace(place)
        }
        
        // Initialize fragments
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        placesListFragment = PlacesListFragment()
        nearbyPlacesFragment = NearbyPlacesFragment()
        
        setupObservers()
        setupBottomNavigation()
        setupLocationUpdates()

        mapFragment.getMapAsync(this)
    }

    private fun setupLocationUpdates() {
        locationHelper.checkLocationPermission {
            locationHelper.requestLocationUpdates { location ->
                currentLocation = location
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_map -> {
                    showFragment(mapFragment)
                    true
                }
                R.id.navigation_nearby -> {
                    showFragment(nearbyPlacesFragment)
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
                hideAllExceptMap()
                show(mapFragment)
            } else {
                hideAllExceptMap()
                if (!fragment.isAdded) {
                    add(R.id.fragment_container, fragment)
                }
                show(fragment)
            }
            commit()
        }
    }
    
    private fun hideAllExceptMap() {
        supportFragmentManager.fragments.forEach { frag ->
            if (frag != mapFragment && frag.isAdded) {
                supportFragmentManager.beginTransaction().hide(frag).commit()
            }
        }
    }

    private fun setupObservers() {
        viewModel.allPlaces.observe(this) { places ->
            updateMapWithPlaces(places)
        }
    }

    private fun updateMapWithPlaces(places: List<Place>) {
        if (::viewMap.isInitialized) {
            viewMap.clear()
            
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
    
    override fun onDestroy() {
        super.onDestroy()
        locationHelper.stopLocationUpdates()
    }
}