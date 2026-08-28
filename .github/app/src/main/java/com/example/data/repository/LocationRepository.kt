package com.example.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.model.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

class LocationRepository(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData? {
        if (!hasLocationPermission()) {
            return null
        }

        return withContext(Dispatchers.IO) {
            try {
                val cancellationTokenSource = CancellationTokenSource()
                val location = suspendCancellableCoroutine<Location?> { cont ->
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        cancellationTokenSource.token
                    ).addOnSuccessListener { loc ->
                        cont.resume(loc)
                    }.addOnFailureListener {
                        cont.resume(null)
                    }
                }

                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude
                    val geoInfo = getCityAndCountry(lat, lng)
                    val tz = TimeZone.getDefault().id

                    LocationData(
                        latitude = lat,
                        longitude = lng,
                        cityName = geoInfo.first,
                        countryName = geoInfo.second,
                        timeZoneId = tz,
                        isAutoDetected = true
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getCityAndCountry(lat: Double, lng: Double): Pair<String, String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Current Location"
                val country = address.countryName ?: "Bangladesh"
                Pair(city, country)
            } else {
                Pair("Detected Location", "Bangladesh")
            }
        } catch (e: Exception) {
            Pair("Detected Location", "Bangladesh")
        }
    }
}
