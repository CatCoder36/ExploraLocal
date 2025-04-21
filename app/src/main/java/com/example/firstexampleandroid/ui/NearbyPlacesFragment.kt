package com.example.firstexampleandroid.ui

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstexampleandroid.MapsActivity
import com.example.firstexampleandroid.R
import com.example.firstexampleandroid.adapters.NearbyPlaceAdapter
import com.example.firstexampleandroid.models.Place
import com.example.firstexampleandroid.models.PlaceWithDistance
import com.example.firstexampleandroid.viewmodel.PlacesViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class NearbyPlacesFragment : Fragment() {

    private val viewModel: PlacesViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var adapter: NearbyPlaceAdapter
    private var currentLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nearby_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.nearby_places_recycler_view)
        emptyStateText = view.findViewById(R.id.text_empty_state)
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        setupRecyclerView()
        observeViewModel()
        getCurrentLocation()
    }

    /**
     * Sets up the RecyclerView with a LinearLayoutManager and an adapter.
     */
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NearbyPlaceAdapter(emptyList()) { placeWithDistance ->
            Toast.makeText(
                requireContext(),
                "Seleccionaste: ${placeWithDistance.place.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
        recyclerView.adapter = adapter
    }

    /**
     * Observes changes in the ViewModel's allPlaces LiveData and updates the nearby places.
     */
    private fun observeViewModel() {
        viewModel.allPlaces.observe(viewLifecycleOwner) { places ->
            updateNearbyPlaces(places)
        }
    }

    /**
     * Retrieves the current location using the MapsActivity's location helper.
     * Updates the nearby places based on the current location.
     */
    private fun getCurrentLocation() {
        loadingIndicator.visibility = View.VISIBLE
        
        (activity as? MapsActivity)?.let { mapsActivity ->
            mapsActivity.locationHelper.checkLocationPermission {
                mapsActivity.locationHelper.requestLocationUpdates { location ->
                    currentLocation = location
                    updateNearbyPlaces(viewModel.allPlaces.value ?: emptyList())
                }
            }
        }
    }

    /**
     * Updates the list of nearby places based on the current location and sorts them by distance.
     *
     * @param places List of places to be updated
     */
    private fun updateNearbyPlaces(places: List<Place>) {
        loadingIndicator.visibility = View.GONE
        
        if (places.isEmpty()) {
            showEmptyState()
            return
        }

        currentLocation?.let { location ->
            val placesWithDistance = places.map { place ->
                val distance = calculateDistance(
                    location.latitude, location.longitude,
                    place.latitude, place.longitude
                )
                PlaceWithDistance(place, distance)
            }

            val sortedPlaces = placesWithDistance.sortedBy { it.distanceInMeters }
            
            if (sortedPlaces.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                adapter.updatePlaces(sortedPlaces)
            }
        } ?: showEmptyState()
    }

    /**
     * Displays the empty state view when no places are available.
     * Hides the recycler view and shows the empty state text.
     */
    private fun showEmptyState() {
        recyclerView.visibility = View.GONE
        emptyStateText.visibility = View.VISIBLE
    }

    /**
     * Hides the empty state view when places are available.
     * Shows the recycler view and hides the empty state text.
     */
    private fun hideEmptyState() {
        recyclerView.visibility = View.VISIBLE
        emptyStateText.visibility = View.GONE
    }

    /**
     * Calculates the distance between two geographic coordinates using the Haversine formula.
     *
     * @param lat1 Latitude of the first point in degrees
     * @param lon1 Longitude of the first point in degrees
     * @param lat2 Latitude of the second point in degrees
     * @param lon2 Longitude of the second point in degrees
     * @return Distance between the points in meters
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadiusKm * c * 1000
    }
}