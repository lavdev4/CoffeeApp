package com.example.coffeeapp.domain.usecases

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.domain.entities.LocationEntity
import com.example.coffeeapp.domain.entities.LocationResultEntity
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.repositories.CafesRepository
import com.example.coffeeapp.domain.repositories.LocationRepository
import com.example.coffeeapp.domain.repositories.TokenRepository
import javax.inject.Inject

class GetCafesUseCase @Inject constructor(
    private val cafesRepository: CafesRepository,
    private val locationRepository: LocationRepository,
    private val tokenRepository: TokenRepository
) {
    private val _cafesData = MediatorLiveData<List<CafeEntity>>()
    val cafesData: LiveData<List<CafeEntity>>
        get() = _cafesData

    private val _networkErrors = MutableLiveData<NetworkError>()
    val networkErrors: LiveData<NetworkError>
        get() = _networkErrors

    suspend fun initialise() {
        val token = tokenRepository.getAuthToken()
        if (token != null) {
            requestCafes(token)
        } else {
            _networkErrors.value = NetworkError.NoTokenProvided
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    private suspend fun requestCafes(token: String) {
        when (val networkResult = cafesRepository.getCafes(token)) {
            is NetworkResultEntity.Success -> {
                _cafesData.value = networkResult.data
                mergeCafesWithLocations()
            }
            is NetworkResultEntity.Failure -> _networkErrors.value = networkResult.error
        }
    }

    fun requestLocations(): LocationResultEntity {
        return locationRepository.requestLocationUpdates()
    }

    private fun mergeCafesWithLocations() {
        val cafesWithDistance = locationRepository.getCurrentLocation()
            .map { location ->
                _cafesData.value?.let { cafesData ->
                    parametrizeCafesWithDistance(cafesData, location)
                }
            }
        _cafesData.addSource(cafesWithDistance) { _cafesData.value = it }
    }

    private fun parametrizeCafesWithDistance(
        nonParametrized: List<CafeEntity>,
        userLocation: LocationEntity
    ): List<CafeEntity> {
        return nonParametrized.map {
            val distance = locationRepository.getDistanceBetweenLocations(
                firstLatitude = it.latitude.toDouble(),
                firstLongitude = it.longitude.toDouble(),
                secondLatitude = userLocation.latitude,
                secondLongitude = userLocation.longitude
            )
            it.copy(distanceMeters = distance)
        }
    }
}