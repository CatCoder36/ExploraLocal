package com.example.firstexampleandroid.repositories

import androidx.lifecycle.LiveData
import com.example.firstexampleandroid.models.Place
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceRepository @Inject constructor(private val placeDao: PlaceDao) {

    val allPlaces: LiveData<List<Place>> = placeDao.getAllPlaces()

    suspend fun insert(place: Place) {
        placeDao.insert(place)
    }

    suspend fun delete(place: Place) {
        placeDao.delete(place)
    }

    suspend fun update(place: Place) {
        placeDao.update(place)
    }
}
