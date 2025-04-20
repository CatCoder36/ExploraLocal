package com.example.firstexampleandroid

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import com.example.firstexampleandroid.models.Place
import com.example.firstexampleandroid.viewmodel.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewMap: GoogleMap
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Añade estas variables para manejar la foto
    private lateinit var photoImageView: ImageView
    private lateinit var photoPreviewContainer: ConstraintLayout
    private var currentPhotoPath: String = ""
    private var currentPhotoUri: Uri? = null


    private val viewModel: PlacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setupObservers()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.allPlaces.observe(this) { places ->
            // Aquí puedes actualizar el mapa con los lugares guardados
            updateMapWithPlaces(places)
        }
    }


    private fun updateMapWithPlaces(places: List<Place>) {
        // Solo actualizar si el mapa está inicializado
        if (::viewMap.isInitialized) {
            // Opcional: limpiar marcadores existentes
            // viewMap.clear()

            // Añadir marcadores para cada lugar
            for (place in places) {
                val position = LatLng(place.latitude, place.longitude)
                viewMap.addMarker(
                    com.google.android.gms.maps.model.MarkerOptions()
                        .position(position)
                        .title(place.name)
                        .snippet(place.description)
                )
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        viewMap = googleMap

        viewMap.setOnMapClickListener { latLng ->
            showAddPlaceForm(latLng)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Enable the My Location layer if the fine location permission has been granted.
     */
    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    viewMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }

    private fun showAddPlaceForm(latLng: LatLng) {
        val bottomSheetView = layoutInflater.inflate(R.layout.form_place, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        // Configurar el comportamiento del BottomSheetDialog para adaptarse mejor a la orientación
        val behavior = bottomSheetDialog.behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true

        // Obtener referencias a las vistas
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

        photoPreviewContainer.visibility = View.GONE

        // Configurar botón para quitar foto
        btnRemovePhoto.setOnClickListener {
            currentPhotoUri = null
            photoPreviewContainer.visibility = View.GONE
        }
        
        // Botones existentes...
        btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


         // Configurar botón para tomar foto
         btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, 
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

         // Configurar botón para subir foto
         btnUploadPhoto.setOnClickListener {
            openGallery()
        }

        btnSavePlace.setOnClickListener {
            val nombre = etNombre.text.toString()
            val descripcion = etDescripcion.text.toString()
            val rating = ratingBar.rating
            
            if (nombre.isEmpty()) {
                etNombre.error = "Por favor ingresa un nombre"
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

            viewModel.addPlace(nuevoLugar)
            Toast.makeText(this, "Lugar guardado", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }

    

        // Mostrar el BottomSheet
        bottomSheetDialog.show()
    }

    // Fotografia:
     // Registros para los resultados de actividades
     private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            // Foto tomada correctamente
            showPhotoInPreview(currentPhotoUri!!)
        }
    }

    private val selectPictureLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Foto seleccionada correctamente
            currentPhotoUri = it
            showPhotoInPreview(it)
        }
    }
    
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Se necesita permiso de cámara para tomar fotos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        currentPhotoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        takePictureLauncher.launch(currentPhotoUri)
    }
    
    private fun openGallery() {
        selectPictureLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }
    
    private fun showPhotoInPreview(photoUri: Uri) {
        try {
            // Mostrar la imagen en la vista previa
            photoImageView.setImageURI(photoUri)
            photoPreviewContainer.visibility = View.VISIBLE
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}