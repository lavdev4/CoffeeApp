package com.example.coffeeapp.domain.repositories

import androidx.lifecycle.LiveData
import com.example.coffeeapp.domain.entities.LocationEntity
import com.example.coffeeapp.domain.entities.LocationResultEntity

interface LocationRepository {
    fun requestLocationUpdates(): LocationResultEntity
    fun getCurrentLocation(): LiveData<LocationEntity>
    fun getDistanceBetweenLocations(
        firstLatitude: Double,
        secondLatitude: Double,
        firstLongitude: Double,
        secondLongitude: Double
    ): Float
}