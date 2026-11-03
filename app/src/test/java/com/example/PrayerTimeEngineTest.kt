package com.example

import com.example.calculation.PrayerTimeEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class PrayerTimeEngineTest {

    private val dhakaLocation = LocationData(
        latitude = 23.8103,
        longitude = 90.4125,
        cityName = "Dhaka",
        countryName = "Bangladesh",
        timeZoneId = "Asia/Dhaka",
        isAutoDetected = false
    )

    @Test
    fun `test prayer timeline chronological ordering`() {
        val date = LocalDate.of(2026, 6, 15)
        val settings = PrayerSettings(
            calculationMethod = PrayerCalculationMethod.KARACHI,
            asrMethod = AsrJuristicMethod.HANAFI
        )

        val times = PrayerTimeEngine.calculatePrayerTimes(date, dhakaLocation, settings)

        // Fajr < Sunrise < Dhuhr < Asr < Sunset == Maghrib < Isha
        assertTrue("Fajr must be before Sunrise", times.fajr.isBefore(times.sunrise))
        assertTrue("Sunrise must be before Dhuhr", times.sunrise.isBefore(times.dhuhr))
        assertTrue("Dhuhr must be before Asr", times.dhuhr.isBefore(times.asr))
        assertTrue("Asr must be before Maghrib", times.asr.isBefore(times.maghrib))
        assertTrue("Maghrib must be before Isha", times.maghrib.isBefore(times.isha))

        // Sehri end is 1 min before Fajr
        assertEquals(times.fajr.minusMinutes(1), times.sehriEnd)

        // Iftar is Maghrib
        assertEquals(times.maghrib, times.iftar)
    }

    @Test
    fun `test custom offsets adjust prayer times correctly`() {
        val date = LocalDate.of(2026, 6, 15)
        val baseSettings = PrayerSettings(
            calculationMethod = PrayerCalculationMethod.KARACHI,
            fajrOffsetMinutes = 0,
            maghribOffsetMinutes = 0
        )
        val offsetSettings = PrayerSettings(
            calculationMethod = PrayerCalculationMethod.KARACHI,
            fajrOffsetMinutes = 5,
            maghribOffsetMinutes = -3
        )

        val baseTimes = PrayerTimeEngine.calculatePrayerTimes(date, dhakaLocation, baseSettings)
        val offsetTimes = PrayerTimeEngine.calculatePrayerTimes(date, dhakaLocation, offsetSettings)

        assertEquals(baseTimes.fajr.plusMinutes(5), offsetTimes.fajr)
        assertEquals(baseTimes.maghrib.minusMinutes(3), offsetTimes.maghrib)
    }

    @Test
    fun `test high latitude adjustments in northern location`() {
        // Oslo, Norway coordinates (High latitude: ~60° N)
        val osloLocation = LocationData(
            latitude = 59.9139,
            longitude = 10.7522,
            cityName = "Oslo",
            countryName = "Norway",
            timeZoneId = "Europe/Oslo",
            isAutoDetected = false
        )

        val summerDate = LocalDate.of(2026, 6, 21) // Summer solstice
        val midnightSettings = PrayerSettings(
            calculationMethod = PrayerCalculationMethod.MUSLIM_WORLD_LEAGUE,
            highLatitudeAdjustment = HighLatitudeAdjustment.MIDNIGHT
        )
        val oneSeventhSettings = PrayerSettings(
            calculationMethod = PrayerCalculationMethod.MUSLIM_WORLD_LEAGUE,
            highLatitudeAdjustment = HighLatitudeAdjustment.ONE_SEVENTH
        )

        val midnightTimes = PrayerTimeEngine.calculatePrayerTimes(summerDate, osloLocation, midnightSettings)
        val oneSeventhTimes = PrayerTimeEngine.calculatePrayerTimes(summerDate, osloLocation, oneSeventhSettings)

        assertNotNull(midnightTimes.fajr)
        assertNotNull(midnightTimes.isha)
        assertNotNull(oneSeventhTimes.fajr)
        assertNotNull(oneSeventhTimes.isha)
    }
}
