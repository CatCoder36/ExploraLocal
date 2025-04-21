package com.example.firstexampleandroid.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstexampleandroid.MapsActivity
import com.example.firstexampleandroid.R
import com.example.firstexampleandroid.adapters.PlaceAdapter
import com.example.firstexampleandroid.models.Place
import com.example.firstexampleandroid.viewmodel.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlacesListFragment : Fragment() {

    private val viewModel: PlacesViewModel by activityViewModels()
    private lateinit var placesRecyclerView: RecyclerView
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_places_list, container, false)
        
        placesRecyclerView = view.findViewById(R.id.places_recycler_view)
        placesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = PlaceAdapter(
            emptyList(),
            onPlaceClick = { place ->
                // Handle click on a place item
                Toast.makeText(context, "Selected place: ${place.name}", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { place ->
                // Open the form in edit mode
                editPlace(place)
            },
            onShareClick = { place ->
                // Share the place
                sharePlace(place)
            },
            onDeleteClick = { place ->
                // Show delete confirmation dialog
                showDeleteConfirmationDialog(place)
            }
        )
        
        placesRecyclerView.adapter = adapter
        
        // Set up sort button
        val btnSort = view.findViewById<Button>(R.id.btnSort)
        btnSort.setOnClickListener { view -> 
            showSortPopupMenu(view)
        }
        
        setupObservers()
        
        return view
    }

    /**
     * Opens the place form bottom sheet in edit mode for the given place.
     *
     * @param place The place object to be edited.
     */
    private fun editPlace(place: Place) {
        val activity = requireActivity() as MapsActivity
        val placeFormBottomSheet = PlaceFormBottomSheet(
            activity,
            activity.photoManager
        ) { updatedPlace ->
            viewModel.addPlace(updatedPlace)
        }
        placeFormBottomSheet.showEditMode(place)
    }

    /**
     * Shares the place information using the device's sharing functionality.
     * Creates a Google Maps URL for the place and includes name and description.
     *
     * @param place The place object to be shared.
     */
    private fun sharePlace(place: Place) {
        val googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=${place.latitude},${place.longitude}"
        
        val shareMessage = "Hey! Check out this amazing place: ${place.name}\n" +
                "${place.description}\n\n" +
                "Location: $googleMapsUrl"
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        
        startActivity(Intent.createChooser(shareIntent, "Share place"))
    }

    /**
     * Shows a confirmation dialog before deleting a place.
     * If confirmed, the place will be removed from the database.
     *
     * @param place The place object to be deleted.
     */
    private fun showDeleteConfirmationDialog(place: Place) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete place")
            .setMessage("Are you sure you want to delete ${place.name}?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deletePlace(place)
                Toast.makeText(context, "${place.name} successfully deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .setIcon(R.drawable.ic_delete)
            .show()
    }
    
    /**
     * Displays a popup menu with sorting options for the places list.
     *
     * @param view The view that was clicked to show the popup menu.
     */
    private fun showSortPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_sort_places, popupMenu.menu)
        
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.sort_by_name -> {
                    adapter.sortByName()
                    true
                }
                R.id.sort_by_rating -> {
                    adapter.sortByRating()
                    true
                }
                else -> false
            }
        }
        
        popupMenu.show()
    }
    
    /**
     * Sets up observers for LiveData objects from the ViewModel.
     * Updates the adapter when place data changes.
     */
    private fun setupObservers() {
        viewModel.allPlaces.observe(viewLifecycleOwner) { places ->
            adapter.updatePlaces(places)
        }
    }
}