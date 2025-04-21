package com.example.firstexampleandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
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

    /**
     * Creates a new ViewHolder by inflating the item layout.
     *
     * @param parent The parent ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new PlaceViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    /**
     * Binds the data to the ViewHolder at the specified position.
     * Sets place details, loads image if available, and configures click listeners.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        
        holder.name.text = place.name
        holder.description.text = place.description
        holder.rating.rating = place.rating
        
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
        
        holder.itemView.setOnClickListener { onPlaceClick(place) }
        holder.btnEdit.setOnClickListener { onEditClick(place) }
        holder.btnShare.setOnClickListener { onShareClick(place) }
        holder.btnDelete.setOnClickListener { onDeleteClick(place) }
    }

    /**
     * Returns the total number of items in the data set.
     *
     * @return The total number of places.
     */
    override fun getItemCount() = places.size

    /**
     * Updates the list of places with a new list, using DiffUtil for efficient updates.
     *
     * @param newPlaces The new list of places to display.
     */
    fun updatePlaces(newPlaces: List<Place>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = places.size
            override fun getNewListSize() = newPlaces.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return places[oldItemPosition].id == newPlaces[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return places[oldItemPosition] == newPlaces[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        places = newPlaces
        diffResult.dispatchUpdatesTo(this)
    }
    
    /**
     * Sorts the places alphabetically by name and refreshes the view.
     */
    fun sortByName() {
        places = places.sortedBy { it.name }
        notifyDataSetChanged()
    }
    
    /**
     * Sorts the places by rating in descending order and refreshes the view.
     */
    fun sortByRating() {
        places = places.sortedByDescending { it.rating }
        notifyDataSetChanged()
    }
}