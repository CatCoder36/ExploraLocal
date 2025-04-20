package com.example.firstexampleandroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        
        adapter = PlaceAdapter(emptyList()) { place ->
            // Handle click on a place item
        }
        
        placesRecyclerView.adapter = adapter
        
        setupObservers()
        
        return view
    }
    
    private fun setupObservers() {
        viewModel.allPlaces.observe(viewLifecycleOwner) { places ->
            adapter.updatePlaces(places)
        }
    }
}