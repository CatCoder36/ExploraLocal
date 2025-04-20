package com.example.firstexampleandroid.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.firstexampleandroid.models.Place
import com.example.firstexampleandroid.repositories.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class PlacesViewModel 
    @Inject constructor(private val repository: PlaceRepository ): ViewModel() {

    val allPlaces: LiveData<List<Place>> = repository.allPlaces

    fun addPlace(place: Place) {
        viewModelScope.launch {
            repository.insert(place)
        }
    }
}