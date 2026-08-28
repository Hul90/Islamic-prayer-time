package com.example.calculation

import com.example.model.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.TimeZone
import kotlin.math.*

object PrayerTimeEngine {

    /**
     * Calculates all daily prayer times for a given date, location, and settings.
     */
    fun calculatePrayerTimes(
        date: LocalDate,
        location: LocationData,
        settings: PrayerSettings
    ): PrayerTimesDay {
        val lat = location.latitude
        val lng = location.longitude
        val timeZone = try {
            val tz = TimeZone.getTimeZone(location.timeZoneId)
            val zoneOffset = ZoneId.of(location.timeZoneId).rules.getOffset(date.atStartOfDay()).totalSeconds / 3600.0
            zoneOffset
        } catch (e: Exception) {
            // Fallback: estimate from longitude
            (lng / 15.0).roundToInt().toDouble()
        }

        val julianDay = getJulianDay(date.year, date.monthValue, date.dayOfMonth)
        val d = julianDay - 2451545.0
        val t = d / 36525.0

        // Solar calculations
        val geomMeanLongSun = (280.46646 + 36000.76983 * t + 0.0003032 * t * t) % 360.0
        val geomMeanAnomSun = (357.52911 + 35999.05029 * t - 0.0001536 * t * t) % 360.0
        val meanAnomRad = Math.toRadians(geomMeanAnomSun)

        val sunEqOfCtr = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(meanAnomRad) +
                (0.019993 - 0.000101 * t) * sin(2.0 * meanAnomRad) +
                0.000289 * sin(3.0 * meanAnomRad)

        val sunTrueLong = geomMeanLongSun + sunEqOfCtr
        val sunAppLong = sunTrueLong - 0.00569 - 0.00478 * sin(Math.toRadians(125.04 - 1934.136 * t))
        val meanObliqEcliptic = 23.439291 - 0.0130042 * t - 0.00000016 * t * t + 0.000000504 * t * t * t
        val obliqCorr = meanObliqEcliptic + 0.00256 * cos(Math.toRadians(125.04 - 1934.136 * t))

        val declinationRad = asin(sin(Math.toRadians(obliqCorr)) * sin(Math.toRadians(sunAppLong)))
        val declinationDeg = Math.toDegrees(declinationRad)

        val y = tan(Math.toRadians(obliqCorr) / 2.0).pow(2.0)
        val eotMinutes = 4.0 * Math.toDegrees(
            y * sin(2.0 * Math.toRadians(geomMeanLongSun)) -
                    2.0 * 0.016708634 * sin(meanAnomRad) +
                    4.0 * 0.016708634 * y * sin(meanAnomRad) * cos(2.0 * Math.toRadians(geomMeanLongSun)) -
                    0.5 * y * y * sin(4.0 * Math.toRadians(geomMeanLongSun)) -
                    1.25 * 0.016708634 * 0.016708634 * sin(2.0 * meanAnomRad)
        )

        // Solar noon (Dhuhr base)
        val solarNoonHours = 12.0 + timeZone - (lng / 15.0) - (eotMinutes / 60.0)
        // Add standard 1-2 minute margin for sun to cross zenith
        val dhuhrHours = solarNoonHours + (1.5 / 60.0)

        // Sunrise & Sunset (approx 0.833° atmospheric refraction)
        val sunriseSunsetAngle = 0.8333
        val sunriseHours = calculateSunAngleTime(solarNoonHours, sunriseSunsetAngle, lat, declinationDeg, isBeforeNoon = true)
        val sunsetHours = calculateSunAngleTime(solarNoonHours, sunriseSunsetAngle, lat, declinationDeg, isBeforeNoon = false)

        val nightDuration = if (sunriseHours != null && sunsetHours != null) {
            (24.0 - sunsetHours) + sunriseHours
        } else {
            12.0
        }

        // Fajr
        val fajrAngle = settings.calculationMethod.fajrAngle
        var fajrHours = calculateSunAngleTime(solarNoonHours, fajrAngle, lat, declinationDeg, isBeforeNoon = true)

        // Isha
        var ishaHours = if (settings.calculationMethod.isIshaFixedMinutes) {
            (sunsetHours ?: (solarNoonHours + 6.0)) + (settings.calculationMethod.ishaMinutes / 60.0)
        } else {
            calculateSunAngleTime(solarNoonHours, settings.calculationMethod.ishaAngle, lat, declinationDeg, isBeforeNoon = false)
        }

        // High latitude adjustment if needed
        if (fajrHours == null || ishaHours == null || settings.highLatitudeAdjustment != HighLatitudeAdjustment.NONE) {
            val sunset = sunsetHours ?: (solarNoonHours + 6.0)
            val sunrise = sunriseHours ?: (solarNoonHours - 6.0)
            when (settings.highLatitudeAdjustment) {
                HighLatitudeAdjustment.MIDNIGHT -> {
                    val halfNight = nightDuration / 2.0
                    if (fajrHours == null || fajrHours < (sunrise - halfNight)) {
                        fajrHours = sunrise - halfNight
                    }
                    if (ishaHours == null || ishaHours > (sunset + halfNight)) {
                        ishaHours = sunset + halfNight
                    }
                }
                HighLatitudeAdjustment.ONE_SEVENTH -> {
                    val seventhNight = nightDuration / 7.0
                    if (fajrHours == null || fajrHours < (sunrise - seventhNight)) {
                        fajrHours = sunrise - seventhNight
                    }
                    if (ishaHours == null || ishaHours > (sunset + seventhNight)) {
                        ishaHours = sunset + seventhNight
                    }
                }
                HighLatitudeAdjustment.ANGLE_BASED -> {
                    val fajrPortion = (fajrAngle / 60.0) * nightDuration
                    val ishaPortion = (settings.calculationMethod.ishaAngle / 60.0) * nightDuration
                    if (fajrHours == null || fajrHours < (sunrise - fajrPortion)) {
                        fajrHours = sunrise - fajrPortion
                    }
                    if (ishaHours == null || ishaHours > (sunset + ishaPortion)) {
                        ishaHours = sunset + ishaPortion
                    }
                }
                HighLatitudeAdjustment.NONE -> {
                    if (fajrHours == null) fajrHours = (sunriseHours ?: (solarNoonHours - 6.0)) - (1.5)
                    if (ishaHours == null) ishaHours = (sunsetHours ?: (solarNoonHours + 6.0)) + (1.5)
                }
            }
        }

        // Asr Calculation based on Juristic method shadow length
        val shadowFactor = settings.asrMethod.shadowFactor
        val asrHours = calculateAsrTime(solarNoonHours, shadowFactor, lat, declinationDeg)

        // Convert double hours to LocalTime and apply user custom offsets
        val fajrTime = hoursToLocalTime(fajrHours!!, settings.fajrOffsetMinutes)
        val sunriseTime = hoursToLocalTime(sunriseHours ?: (solarNoonHours - 6.0), 0)
        val dhuhrTime = hoursToLocalTime(dhuhrHours, settings.dhuhrOffsetMinutes)
        val asrTime = hoursToLocalTime(asrHours, settings.asrOffsetMinutes)
        val sunsetTime = hoursToLocalTime(sunsetHours ?: (solarNoonHours + 6.0), 0)
        val maghribTime = hoursToLocalTime(sunsetHours ?: (solarNoonHours + 6.0), settings.maghribOffsetMinutes)
        val ishaTime = hoursToLocalTime(ishaHours!!, settings.ishaOffsetMinutes)

        // Midnight and Sehri/Iftar
        val midnightHours = ((sunsetHours ?: 18.0) + nightDuration / 2.0) % 24.0
        val midnightTime = hoursToLocalTime(midnightHours, 0)
        // Sehri ends right at Fajr or 1-2 min before for safe imsak
        val sehriEndTime = fajrTime.minusMinutes(1)
        val iftarTime = maghribTime

        return PrayerTimesDay(
            date = date,
            fajr = fajrTime,
            sunrise = sunriseTime,
            dhuhr = dhuhrTime,
            asr = asrTime,
            sunset = sunsetTime,
            maghrib = maghribTime,
            isha = ishaTime,
            midnight = midnightTime,
            sehriEnd = sehriEndTime,
            iftar = iftarTime
        )
    }

    private fun calculateSunAngleTime(
        solarNoon: Double,
        angleDeg: Double,
        latDeg: Double,
        declinationDeg: Double,
        isBeforeNoon: Boolean
    ): Double? {
        val latRad = Math.toRadians(latDeg)
        val declRad = Math.toRadians(declinationDeg)
        val angleRad = Math.toRadians(angleDeg)

        val cosHourAngle = (-sin(angleRad) - sin(latRad) * sin(declRad)) / (cos(latRad) * cos(declRad))
        if (cosHourAngle < -1.0 || cosHourAngle > 1.0) {
            return null // Sun does not reach this angle (polar day/night)
        }

        val hourAngleDeg = Math.toDegrees(acos(cosHourAngle))
        val hourAngleHours = hourAngleDeg / 15.0

        return if (isBeforeNoon) {
            solarNoon - hourAngleHours
        } else {
            solarNoon + hourAngleHours
        }
    }

    private fun calculateAsrTime(
        solarNoon: Double,
        shadowFactor: Double,
        latDeg: Double,
        declinationDeg: Double
    ): Double {
        val latRad = Math.toRadians(latDeg)
        val declRad = Math.toRadians(declinationDeg)
        val latMinusDecl = abs(latRad - declRad)

        // Sun altitude angle at Asr: arccot(shadowFactor + tan|lat - decl|)
        val cotSunAlt = shadowFactor + tan(latMinusDecl)
        val sunAltRad = atan(1.0 / cotSunAlt)

        val cosHourAngle = (sin(sunAltRad) - sin(latRad) * sin(declRad)) / (cos(latRad) * cos(declRad))
        val clampedCos = cosHourAngle.coerceIn(-1.0, 1.0)
        val hourAngleHours = Math.toDegrees(acos(clampedCos)) / 15.0

        return solarNoon + hourAngleHours
    }

    private fun hoursToLocalTime(hours: Double, offsetMinutes: Int): LocalTime {
        var normalized = (hours + (offsetMinutes / 60.0)) % 24.0
        if (normalized < 0) normalized += 24.0

        val totalSeconds = (normalized * 3600.0).roundToInt()
        val h = (totalSeconds / 3600) % 24
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60

        return LocalTime.of(h, m, s)
    }

    private fun getJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }
}
