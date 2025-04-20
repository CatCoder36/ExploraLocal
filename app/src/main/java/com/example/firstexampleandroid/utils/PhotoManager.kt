package com.example.firstexampleandroid.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Class for managing photo capture and selection operations.
 * Handles camera permissions, photo taking, gallery selection, and image display.
 */
class PhotoManager(private val context: AppCompatActivity) {
    
    private var currentPhotoPath: String = ""
    private var currentPhotoUri: Uri? = null
    private var photoReadyCallback: ((Uri?) -> Unit)? = null
    
    // Launcher for taking photos
    val takePictureLauncher: ActivityResultLauncher<Uri> = context.registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            photoReadyCallback?.invoke(currentPhotoUri)
        }
    }
    
    // Launcher for selecting photos
    val selectPictureLauncher: ActivityResultLauncher<String> = context.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentPhotoUri = it
            photoReadyCallback?.invoke(currentPhotoUri)
        }
    }
    
    // Launcher for requesting camera permissions
    val requestCameraPermissionLauncher: ActivityResultLauncher<String> = context.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(context, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Opens the camera to take a photo.
     *
     * @param onPhotoReady Callback function that will be invoked when the photo is ready,
     *                    receives the photo's Uri as parameter.
     */
    fun openCamera(onPhotoReady: (Uri?) -> Unit = {}) {
        photoReadyCallback = onPhotoReady
        val photoFile = createImageFile()
        currentPhotoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        takePictureLauncher.launch(currentPhotoUri)
    }
    
    /**
     * Opens the gallery to select a photo.
     *
     * @param onPhotoReady Callback function that will be invoked when a photo is selected,
     *                    receives the selected photo's Uri as parameter.
     */
    fun openGallery(onPhotoReady: (Uri?) -> Unit = {}) {
        photoReadyCallback = onPhotoReady
        selectPictureLauncher.launch("image/*")
    }
    
    /**
     * Creates a temporary file to store the captured photo.
     *
     * @return File object pointing to the newly created image file.
     */
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }
    
    /**
     * Displays a photo in the provided ImageView.
     *
     * @param photoUri The Uri of the photo to display.
     * @param imageView The ImageView where the photo will be displayed.
     */
    fun displayPhotoInImageView(photoUri: Uri, imageView: ImageView) {
        try {
            imageView.setImageURI(photoUri)
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Gets the Uri of the current photo.
     *
     * @return The Uri of the current photo or null if no photo is available.
     */
    fun getCurrentPhotoUri(): Uri? {
        return currentPhotoUri
    }
    
    /**
     * Clears the current photo reference.
     * This should be called when the photo is no longer needed.
     */
    fun clearCurrentPhoto() {
        currentPhotoUri = null
    }
    
    /**
     * Checks if camera permissions have been granted.
     *
     * @return true if camera permission is granted, false otherwise.
     */
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Requests camera permission from the user.
     * If granted, the camera will be opened automatically.
     */
    fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}