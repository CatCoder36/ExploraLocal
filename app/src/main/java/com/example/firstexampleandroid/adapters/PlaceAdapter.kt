package com.example.firstexampleandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firstexampleandroid.R
import com.example.firstexampleandroid.models.Place

class PlaceAdapter(
    private var places: List<Place>,
    private val onPlaceClick: (Place) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.place_name)
        val description: TextView = view.findViewById(R.id.place_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.name.text = place.name
        holder.description.text = place.description
        
        holder.itemView.setOnClickListener {
            onPlaceClick(place)
        }
    }

    override fun getItemCount() = places.size

    fun updatePlaces(newPlaces: List<Place>) {
        places = newPlaces
        notifyDataSetChanged()
    }
}