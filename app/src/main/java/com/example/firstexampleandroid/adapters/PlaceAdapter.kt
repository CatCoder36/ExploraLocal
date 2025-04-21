package com.example.firstexampleandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firstexampleandroid.R
import com.example.firstexampleandroid.models.Place

class PlaceAdapter(
    private var places: List<Place>,
    private val onPlaceClick: (Place) -> Unit,
    private val onEditClick: (Place) -> Unit = {},
    private val onShareClick: (Place) -> Unit = {},
    private val onDeleteClick: (Place) -> Unit = {}
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.place_name)
        val description: TextView = view.findViewById(R.id.place_description)
        val rating: RatingBar = view.findViewById(R.id.place_rating)
        val image: ImageView = view.findViewById(R.id.place_image)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
        val btnShare: ImageButton = view.findViewById(R.id.btn_share)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        
        // Set basic info
        holder.name.text = place.name
        holder.description.text = place.description
        holder.rating.rating = place.rating
        
        // Handle image display
        if (place.photoUrl != null) {
            holder.image.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(place.photoUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.image)
        } else {
            holder.image.visibility = View.GONE
        }
        
        // Set click listeners
        holder.itemView.setOnClickListener { onPlaceClick(place) }
        holder.btnEdit.setOnClickListener { onEditClick(place) }
        holder.btnShare.setOnClickListener { onShareClick(place) }
        holder.btnDelete.setOnClickListener { onDeleteClick(place) }
    }

    override fun getItemCount() = places.size

    fun updatePlaces(newPlaces: List<Place>) {
        places = newPlaces
        notifyDataSetChanged()
    }
    
    fun sortByName() {
        places = places.sortedBy { it.name }
        notifyDataSetChanged()
    }
    
    fun sortByRating() {
        places = places.sortedByDescending { it.rating }
        notifyDataSetChanged()
    }
}