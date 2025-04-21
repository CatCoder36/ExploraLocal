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

    private fun observeViewModel() {
        viewModel.allPlaces.observe(viewLifecycleOwner) { places ->
            updateNearbyPlaces(places)
        }
    }

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

    private fun showEmptyState() {
        recyclerView.visibility = View.GONE
        emptyStateText.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        recyclerView.visibility = View.VISIBLE
        emptyStateText.visibility = View.GONE
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        
        // Convert to radians
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        // Haversine formula
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        // Distance in meters
        return earthRadiusKm * c * 1000
    }
}