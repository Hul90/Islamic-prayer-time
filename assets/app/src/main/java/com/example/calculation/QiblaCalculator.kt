package com.example.calculation

import kotlin.math.*

object QiblaCalculator {
    const val MAKKAH_LATITUDE = 21.4225
    const val MAKKAH_LONGITUDE = 39.8262

    /**
     * Calculates the Qibla bearing in degrees (0..360) clockwise from true North.
     */
    fun calculateQiblaBearing(latitude: Double, longitude: Double): Double {
        val latRad = Math.toRadians(latitude)
        val lngRad = Math.toRadians(longitude)
        val makkahLatRad = Math.toRadians(MAKKAH_LATITUDE)
        val makkahLngRad = Math.toRadians(MAKKAH_LONGITUDE)

        val deltaLng = makkahLngRad - lngRad

        val y = sin(deltaLng)
        val x = cos(latRad) * tan(makkahLatRad) - sin(latRad) * cos(deltaLng)

        var qiblaRad = atan2(y, x)
        var qiblaDeg = Math.toDegrees(qiblaRad)

        // Normalize to 0..360
        qiblaDeg = (qiblaDeg + 360.0) % 360.0

        return qiblaDeg
    }

    /**
     * Calculates distance to Makkah in kilometers using Haversine formula.
     */
    fun calculateDistanceToMakkahKm(latitude: Double, longitude: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(MAKKAH_LATITUDE - latitude)
        val dLng = Math.toRadians(MAKKAH_LONGITUDE - longitude)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(MAKKAH_LATITUDE)) *
                sin(dLng / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }
}
