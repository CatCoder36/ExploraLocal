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

/**
 * Main activity for displaying maps and handling place-related operations.
 * This activity manages map interactions, location updates, and navigation between fragments.
 */
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

    /**
     * Initializes the activity, sets up the UI components, and prepares the map.
     *
     * @param savedInstanceState State information saved from a previous instance
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        locationHelper = LocationHelper(this)
        photoManager = PhotoManager(this)

        placeFormBottomSheet = PlaceFormBottomSheet(this, photoManager) { place ->
            viewModel.addPlace(place)
        }
        
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        placesListFragment = PlacesListFragment()
        nearbyPlacesFragment = NearbyPlacesFragment()
        
        setupObservers()
        setupBottomNavigation()
        setupLocationUpdates()

        mapFragment.getMapAsync(this)
    }

    /**
     * Sets up location updates by checking permission and requesting continuous updates.
     * Updates the currentLocation variable when a new location is received.
     */
    private fun setupLocationUpdates() {
        locationHelper.checkLocationPermission {
            locationHelper.requestLocationUpdates { location ->
                currentLocation = location
            }
        }
    }

    /**
     * Configures the bottom navigation view with appropriate listeners
     * to handle fragment transitions based on user selection.
     */
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
        
        bottomNavigation.selectedItemId = R.id.navigation_map
    }

    /**
     * Displays the selected fragment while hiding others.
     * Handles special logic for the map fragment to ensure proper visibility.
     *
     * @param fragment The fragment to be displayed
     */
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
    
    /**
     * Hides all fragments except the map fragment to maintain map visibility
     * when other fragments are not active.
     */
    private fun hideAllExceptMap() {
        supportFragmentManager.fragments.forEach { frag ->
            if (frag != mapFragment && frag.isAdded) {
                supportFragmentManager.beginTransaction().hide(frag).commit()
            }
        }
    }

    /**
     * Sets up observers for LiveData objects from the ViewModel
     * to update the UI when data changes.
     */
    private fun setupObservers() {
        viewModel.allPlaces.observe(this) { places ->
            updateMapWithPlaces(places)
        }
    }

    /**
     * Updates the map with markers for each place in the provided list.
     *
     * @param places List of places to display on the map
     */
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

    /**
     * Callback that is triggered when the map is ready to be used.
     * Sets up map click listeners and displays existing places.
     *
     * @param googleMap The GoogleMap instance that is ready
     */
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
    
    /**
     * Cleans up resources when the activity is destroyed,
     * particularly stopping location updates.
     */
    override fun onDestroy() {
        super.onDestroy()
        locationHelper.stopLocationUpdates()
    }
}