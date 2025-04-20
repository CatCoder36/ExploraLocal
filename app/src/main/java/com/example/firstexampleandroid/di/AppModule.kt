package com.example.firstexampleandroid.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.firstexampleandroid.db.PlaceDatabase
import com.example.firstexampleandroid.repositories.PlaceDao
import com.example.firstexampleandroid.repositories.PlaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePlaceDatabase(@ApplicationContext context: Context): PlaceDatabase {
        val db = Room.databaseBuilder(
            context,
            PlaceDatabase::class.java,
            "places_database"
        ).build()
        Log.d("Database", "Database instance created at: ${context.getDatabasePath("places_database")}")
        return db
    }

    @Provides
    @Singleton
    fun providePlaceDao(database: PlaceDatabase): PlaceDao {
        return database.placeDao()
    }

    @Provides
    @Singleton
    fun providePlaceRepository(placeDao: PlaceDao): PlaceRepository {
        return PlaceRepository(placeDao)
    }
}