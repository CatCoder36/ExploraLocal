package com.example.firstexampleandroid.db


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.firstexampleandroid.models.Place
import com.example.firstexampleandroid.repositories.PlaceDao

@Database(entities = [Place::class], version = 1, exportSchema = false)
abstract class PlaceDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}