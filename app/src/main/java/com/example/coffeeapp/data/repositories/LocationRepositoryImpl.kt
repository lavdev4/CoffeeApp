package com.example.coffeeapp.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.coffeeapp.data.location.LocationProvider
import com.example.coffeeapp.data.mappers.LocationMapper
import com.example.coffeeapp.di.annotations.ApplicationScope
import com.example.coffeeapp.domain.entities.LocationEntity
import com.example.coffeeapp.domain.entities.LocationResultEntity
import com.example.coffeeapp.domain.repositories.LocationRepository
import javax.inject.Inject

@ApplicationScope
class LocationRepositoryImpl @Inject constructor(
    private val provider: LocationProvider,
    private val mapper: LocationMapper
) : LocationRepository {

    override fun requestLocationUpdates(): LocationResultEntity {
        return mapper.mapLocationResultToResultEntity(provider.requestUpdates())
    }

    override fun getCurrentLocation(): LiveData<LocationEntity> {
        return provider.currentLocation.map { mapper.mapLocationToLocationEntity(it) }
    }

    override fun getDistanceBetweenLocations(
        firstLatitude: Double,
        secondLatitude: Double,
        firstLongitude: Double,
        secondLongitude: Double
    ): Float {
        return LocationProvider.getDistanceBetween(
            firstLatitude,
            secondLatitude,
            firstLongitude,
            secondLongitude,
        )
    }
}