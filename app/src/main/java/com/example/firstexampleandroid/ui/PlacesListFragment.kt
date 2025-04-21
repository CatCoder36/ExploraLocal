package com.example.firstexampleandroid.ui

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
import com.example.firstexampleandroid.R
import com.example.firstexampleandroid.adapters.PlaceAdapter
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
                Toast.makeText(context, "Lugar seleccionado: ${place.name}", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { place ->
                // Handle edit action (placeholder for now)
                Toast.makeText(context, "Editar: ${place.name}", Toast.LENGTH_SHORT).show()
            },
            onShareClick = { place ->
                // Handle share action (placeholder for now)
                Toast.makeText(context, "Compartir: ${place.name}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { place ->
                // Handle delete action (placeholder for now)
                Toast.makeText(context, "Eliminar: ${place.name}", Toast.LENGTH_SHORT).show()
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
    
    private fun setupObservers() {
        viewModel.allPlaces.observe(viewLifecycleOwner) { places ->
            adapter.updatePlaces(places)
        }
    }
}