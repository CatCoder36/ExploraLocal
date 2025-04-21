package com.example.firstexampleandroid.ui

import android.Manifest
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.firstexampleandroid.R
import com.example.firstexampleandroid.models.Place
import com.example.firstexampleandroid.utils.PhotoManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Class to manage the Place form BottomSheet
 */
class PlaceFormBottomSheet(
    private val context: AppCompatActivity, 
    private val photoManager: PhotoManager,
    private val onSavePlace: (Place) -> Unit
) {
    
    private var currentPhotoUri: Uri? = null
    private lateinit var bottomSheetView: View
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var photoImageView: ImageView
    private lateinit var photoPreviewContainer: ConstraintLayout

    private var isEditMode = false
    private var placeToEdit: Place? = null
    
    /**
     * Shows the form to add a place
     */
    fun show(latLng: LatLng) {
        // Configure the BottomSheet
        bottomSheetView = LayoutInflater.from(context).inflate(R.layout.form_place, null)
        bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(bottomSheetView)
        
        // Configure behavior
        val behavior = bottomSheetDialog.behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true

        // Update title
        val tvTitle = bottomSheetView.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = "Nuevo Lugar"
        
        // Update button text
        val btnSavePlace = bottomSheetView.findViewById<Button>(R.id.btnSavePlace)
        btnSavePlace.text = "Guardar ubicación"
        
        // Initialize views
        initViews(latLng)
        
        // Show the dialog
        bottomSheetDialog.show()
    }
    
    /**
     * Initializes and configures all form views
     */
    private fun initViews(latLng: LatLng) {
        // Get references to views
        val etNombre = bottomSheetView.findViewById<EditText>(R.id.etNombre)
        val etDescripcion = bottomSheetView.findViewById<EditText>(R.id.etDescripcion)
        val ratingBar = bottomSheetView.findViewById<RatingBar>(R.id.ratingBar)
        val btnClose = bottomSheetView.findViewById<ImageButton>(R.id.btnClose)
        val btnSavePlace = bottomSheetView.findViewById<Button>(R.id.btnSavePlace)
        val btnTakePhoto = bottomSheetView.findViewById<Button>(R.id.btnTakePhoto)
        val btnUploadPhoto = bottomSheetView.findViewById<Button>(R.id.btnUploadPhoto)
        
        photoImageView = bottomSheetView.findViewById(R.id.photoPreview)
        photoPreviewContainer = bottomSheetView.findViewById(R.id.photoPreviewContainer)
        val btnRemovePhoto = bottomSheetView.findViewById<ImageButton>(R.id.btnRemovePhoto)
        
        // Hide the preview container initially
        photoPreviewContainer.visibility = View.GONE

         // Hide the preview container initially if not in edit mode
         if (!isEditMode || currentPhotoUri == null) {
            photoPreviewContainer.visibility = View.GONE
        }
        
        // Configure buttons
        setupButtons(btnClose, btnSavePlace, btnTakePhoto, btnUploadPhoto, btnRemovePhoto, etNombre, etDescripcion, ratingBar, latLng)
    }

    /**
     * Shows the form to edit an existing place
     */
    fun showEditMode(place: Place) {
        isEditMode = true
        placeToEdit = place
        currentPhotoUri = if (place.photoUrl != null) Uri.parse(place.photoUrl) else null
        
        // Configure the BottomSheet
        bottomSheetView = LayoutInflater.from(context).inflate(R.layout.form_place, null)
        bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(bottomSheetView)
        
        // Configure behavior
        val behavior = bottomSheetDialog.behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        
        // Update title
        val tvTitle = bottomSheetView.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = "Editar Lugar"
        
        // Update button text
        val btnSavePlace = bottomSheetView.findViewById<Button>(R.id.btnSavePlace)
        btnSavePlace.text = "Guardar cambios"
        
        // Initialize views with the place's data
        initViews(LatLng(place.latitude, place.longitude))
        
        // Populate form with place data
        populateFormWithPlaceData(place)
        
        // Show the dialog
        bottomSheetDialog.show()
    }
    
    /**
     * Populates the form with data from an existing place
     */
    private fun populateFormWithPlaceData(place: Place) {
        val etNombre = bottomSheetView.findViewById<EditText>(R.id.etNombre)
        val etDescripcion = bottomSheetView.findViewById<EditText>(R.id.etDescripcion)
        val ratingBar = bottomSheetView.findViewById<RatingBar>(R.id.ratingBar)
        
        etNombre.setText(place.name)
        etDescripcion.setText(place.description)
        ratingBar.rating = place.rating
        
        // Load image if exists
        if (place.photoUrl != null) {
            photoPreviewContainer.visibility = View.VISIBLE
            photoManager.displayPhotoInImageView(Uri.parse(place.photoUrl), photoImageView)
        } else {
            photoPreviewContainer.visibility = View.GONE
        }
    }
    
      /**
     * Configures button events
     */
    private fun setupButtons(
        btnClose: ImageButton,
        btnSavePlace: Button,
        btnTakePhoto: Button,
        btnUploadPhoto: Button,
        btnRemovePhoto: ImageButton,
        etNombre: EditText,
        etDescripcion: EditText,
        ratingBar: RatingBar,
        latLng: LatLng
    ) {
        // Close button
        btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        
        // Button to remove photo
        btnRemovePhoto.setOnClickListener {
            currentPhotoUri = null
            photoPreviewContainer.visibility = View.GONE
        }
        
        // Button to take photo
        btnTakePhoto.setOnClickListener {
            if (photoManager.hasCameraPermission()) {
                photoManager.openCamera { uri ->
                    uri?.let {
                        currentPhotoUri = it
                        photoManager.displayPhotoInImageView(it, photoImageView)
                        photoPreviewContainer.visibility = View.VISIBLE
                    }
                }
            } else {
                photoManager.requestCameraPermission()
            }
        }
        
        // Button to upload photo
        btnUploadPhoto.setOnClickListener {
            photoManager.openGallery { uri ->
                uri?.let {
                    currentPhotoUri = it
                    photoManager.displayPhotoInImageView(it, photoImageView)
                    photoPreviewContainer.visibility = View.VISIBLE
                }
            }
        }
        
        // Save button
        btnSavePlace.setOnClickListener {
            val nombre = etNombre.text.toString()
            val descripcion = etDescripcion.text.toString()
            val rating = ratingBar.rating
            
            if (nombre.isEmpty()) {
                etNombre.error = "Por favor ingresa un nombre"
                return@setOnClickListener
            }

            val lugar = if (isEditMode && placeToEdit != null) {
                // Crear una copia del lugar existente con los nuevos valores
                placeToEdit!!.copy(
                    name = nombre,
                    description = descripcion,
                    rating = rating,
                    photoUrl = currentPhotoUri?.toString()
                )
            } else {
                // Crear un nuevo lugar
                Place(
                    name = nombre,
                    description = descripcion,
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    rating = rating,
                    photoUrl = currentPhotoUri?.toString()
                )
            }

            onSavePlace(lugar)
            
            val message = if (isEditMode) "Lugar actualizado" else "Lugar guardado"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            
            bottomSheetDialog.dismiss()
        }
    }

}