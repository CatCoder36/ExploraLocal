package com.example.firstexampleandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firstexampleandroid.R
import com.example.firstexampleandroid.models.PlaceWithDistance

/**
 * Adapter for displaying nearby places in a RecyclerView.
 *
 * @property places List of places with their distances to show
 * @property onPlaceClick Callback function to handle click events on places
 */
class NearbyPlaceAdapter(
    private var places: List<PlaceWithDistance>,
    private val onPlaceClick: (PlaceWithDistance) -> Unit
) : RecyclerView.Adapter<NearbyPlaceAdapter.ViewHolder>() {

    /**
     * ViewHolder for the nearby place items.
     *
     * @property name TextView displaying the place name
     * @property description TextView displaying the place description
     * @property rating RatingBar displaying the place rating
     * @property distance TextView displaying the distance to the place
     * @property thumbnail ImageView displaying the place photo
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.place_name)
        val description: TextView = view.findViewById(R.id.place_description)
        val rating: RatingBar = view.findViewById(R.id.place_rating)
        val distance: TextView = view.findViewById(R.id.place_distance)
        val thumbnail: ImageView = view.findViewById(R.id.place_thumbnail)
    }

    /**
     * Creates new ViewHolder instances for the RecyclerView.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new ViewHolder that holds a View of the given view type
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nearby_place, parent, false)
        return ViewHolder(view)
    }

    /**
     * Binds the data to the ViewHolder.
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position of the item in the data set
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        
        holder.name.text = place.place.name
        holder.description.text = place.place.description
        holder.rating.rating = place.place.rating
        
        val distanceText = when {
            place.distanceInMeters < 1000 -> "A ${place.distanceInMeters.toInt()} metros"
            else -> "A ${String.format("%.1f", place.distanceInMeters / 1000)} km"
        }
        holder.distance.text = distanceText
        
        if (place.place.photoUrl != null) {
            holder.thumbnail.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(place.place.photoUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.thumbnail)
        } else {
            holder.thumbnail.setImageResource(R.drawable.ic_image_placeholder)
        }
        
        holder.itemView.setOnClickListener {
            onPlaceClick(place)
        }
    }

    /**
     * Returns the total number of items in the data set.
     *
     * @return The total number of items
     */
    override fun getItemCount() = places.size

    /**
     * Updates the list of places and refreshes the UI.
     *
     * @param newPlaces New list of places to display
     */
    fun updatePlaces(newPlaces: List<PlaceWithDistance>) {
        places = newPlaces
        notifyDataSetChanged()
    }
}