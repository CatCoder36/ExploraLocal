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
        
        // Configure buttons
        setupButtons(btnClose, btnSavePlace, btnTakePhoto, btnUploadPhoto, btnRemovePhoto, etNombre, etDescripcion, ratingBar, latLng)
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
                etNombre.error = "Please enter a name"
                return@setOnClickListener
            }

            val nuevoLugar = Place(
                name = nombre,
                description = descripcion,
                latitude = latLng.latitude,
                longitude = latLng.longitude,
                rating = rating,
                photoUrl = currentPhotoUri?.toString()
            )

            onSavePlace(nuevoLugar)
            Toast.makeText(context, "Place saved", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }
    }
}