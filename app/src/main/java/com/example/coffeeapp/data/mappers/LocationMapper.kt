package com.example.coffeeapp.data.mappers

import android.location.Location
import com.example.coffeeapp.data.location.LocationProvider
import com.example.coffeeapp.domain.entities.LocationEntity
import com.example.coffeeapp.domain.entities.LocationResultEntity
import javax.inject.Inject

class LocationMapper @Inject constructor() {

    fun mapLocationResultToResultEntity(
        locationProviderResult: Int
    ) : LocationResultEntity {
        return when (locationProviderResult) {
            LocationProvider.PROVIDERS_CONNECTED -> LocationResultEntity.Success
            LocationProvider.NO_PERMISSION_GRANTED -> LocationResultEntity.NoPermission
            LocationProvider.NO_PROVIDER_CONNECTED -> LocationResultEntity.NoProvider
            else -> { throw RuntimeException("Unknown provider result case.") }
        }
    }

    fun mapLocationToLocationEntity(
        location: Location
    ) : LocationEntity {
        return LocationEntity(
            latitude = location.latitude,
            longitude = location.longitude
        )
    }
}