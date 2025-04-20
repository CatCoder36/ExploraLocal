package com.example.firstexampleandroid.repositories

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.firstexampleandroid.models.Place

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getAllPlaces(): LiveData<List<Place>>

    @Insert
    suspend fun insert(place: Place)

    @Delete
    suspend fun delete(place: Place)

    @Update
    suspend fun update(place: Place)
}
