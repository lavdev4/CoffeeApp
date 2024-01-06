package com.example.coffeeapp.data.location

import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.location.LocationListenerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coffeeapp.di.annotations.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class LocationProvider @Inject constructor(
    private val locationManager: LocationManager
) {
    private val networkProvider = LocationManager.NETWORK_PROVIDER
    private val gpsProvider = LocationManager.GPS_PROVIDER
    private var providersConnected = false

    private val _currentLocation = LocationLiveData()
    val currentLocation: LiveData<Location>
        get() = _currentLocation

    private val locationListener = LocationListenerCompat { location ->
        _currentLocation.value = location
    }

    fun requestUpdates(): Int {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            requestForOldDevices()
        } else {
            requestForNewDevices()
        }
    }

    private fun requestForOldDevices(): Int {
        val networkResult = requestNetworkUpdates()
        val gpsResult = requestGpsUpdates()

        val networkActive = networkResult != NO_NETWORK_PROVIDER
        val gpsActive = gpsResult != NO_GPS_PROVIDER
        val bothActive = networkActive && gpsActive
        val bothNotActive = !networkActive && !gpsActive

        if (bothNotActive) return NO_PROVIDER_CONNECTED
        if (bothActive) {
            if (networkResult == NO_NETWORK_PERMISSION) return NO_PERMISSION_GRANTED
        }
        if (networkActive) {
            if (networkResult == NO_NETWORK_PERMISSION) return NO_PERMISSION_GRANTED
        }
        if (gpsActive) {
            if (gpsResult == NO_GPS_PERMISSION) return NO_PERMISSION_GRANTED
        }
        providersConnected = true
        return PROVIDERS_CONNECTED
    }

    private fun requestForNewDevices(): Int {
        val networkResult = requestNetworkUpdates()
        if (networkResult == NO_NETWORK_PROVIDER) return NO_PROVIDER_CONNECTED
        if (networkResult == NO_NETWORK_PERMISSION) return NO_PERMISSION_GRANTED
        providersConnected = true
        return PROVIDERS_CONNECTED
    }

    private fun cancelUpdates() {
        locationManager.removeUpdates(locationListener)
    }

    private fun requestNetworkUpdates(): Int {
        val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (networkEnabled) {
            try {
                val lastLocation = locationManager.getLastKnownLocation(networkProvider)
                lastLocation?.let { _currentLocation.value = it }
                locationManager.requestLocationUpdates(
                    networkProvider,
                    REQUEST_DELAY,
                    UPDATE_DISTANCE,
                    locationListener
                )
            } catch (exception: SecurityException) {
                return NO_NETWORK_PERMISSION
            }
        } else return NO_NETWORK_PROVIDER
        return NETWORK_PROVIDER_CONNECTED
    }

    private fun requestGpsUpdates(): Int {
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsEnabled) {
            try {
                val lastLocation = locationManager.getLastKnownLocation(gpsProvider)
                lastLocation?.let { _currentLocation.value = it }
                locationManager.requestLocationUpdates(
                    gpsProvider,
                    REQUEST_DELAY,
                    UPDATE_DISTANCE,
                    locationListener
                )
            } catch (exception: SecurityException) {
                return NO_GPS_PERMISSION
            }
        } else return NO_GPS_PROVIDER
        return GPS_PROVIDER_CONNECTED
    }

    companion object {
        private const val REQUEST_DELAY = 100L
        private const val UPDATE_DISTANCE = 100.0f

        private const val NO_NETWORK_PERMISSION = 0
        private const val NO_GPS_PERMISSION = 1
        private const val NO_NETWORK_PROVIDER = 2
        private const val NO_GPS_PROVIDER = 3
        private const val NETWORK_PROVIDER_CONNECTED = 4
        private const val GPS_PROVIDER_CONNECTED = 5
        const val NO_PERMISSION_GRANTED = 6
        const val NO_PROVIDER_CONNECTED = 7
        const val PROVIDERS_CONNECTED = 8

        fun getDistanceBetween(
            firstLatitude: Double,
            secondLatitude: Double,
            firstLongitude: Double,
            secondLongitude: Double): Float {
            val firstLocation = Location(null).apply {
                latitude = firstLatitude
                longitude = firstLongitude
            }
            val secondLocation = Location(null).apply {
                latitude = secondLatitude
                longitude = secondLongitude
            }
            return firstLocation.distanceTo(secondLocation)
        }
    }

    private inner class LocationLiveData : MutableLiveData<Location>() {
        /** New value should not exceed old more than [accuracyPermissibleVariance] times */
        private val accuracyPermissibleVariance = 25

        override fun postValue(value: Location?) {
            value?.let {
                if (checkUpdateAccuracy(it)) super.postValue(value)
            } ?: super.postValue(null)
        }

        override fun setValue(value: Location?) {
            value?.let {
                if (checkUpdateAccuracy(it)) super.setValue(value)
            } ?: super.setValue(null)
        }

        override fun onActive() {
            if (providersConnected) { requestUpdates() }
        }

        override fun onInactive() {
            cancelUpdates()
        }

        private fun checkUpdateAccuracy(newValue: Location): Boolean {
            return this.value?.let { currentValue ->
                newValue.accuracy / currentValue.accuracy < accuracyPermissibleVariance
            } ?: true
        }
    }
}