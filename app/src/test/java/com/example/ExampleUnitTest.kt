package com.example

import com.example.calculation.CalendarEngine
import com.example.calculation.PrayerTimeEngine
import com.example.model.LocationData
import com.example.model.PrayerSettings
import com.example.model.PrayerType
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {

    @Test
    fun testDhakaPrayerTimesCalculation() {
        val date = LocalDate.of(2026, 8, 27)
        val dhaka = LocationData.DEFAULT_DHAKA
        val settings = PrayerSettings()

        val times = PrayerTimeEngine.calculatePrayerTimes(date, dhaka, settings)

        assertNotNull(times)
        assertNotNull(times.fajr)
        assertNotNull(times.dhuhr)
        assertNotNull(times.asr)
        assertNotNull(times.maghrib)
        assertNotNull(times.isha)

        // Verify logical chronological order
        assertTrue(times.fajr.isBefore(times.sunrise))
        assertTrue(times.sunrise.isBefore(times.dhuhr))
        assertTrue(times.dhuhr.isBefore(times.asr))
        assertTrue(times.asr.isBefore(times.maghrib))
        assertTrue(times.maghrib.isBefore(times.isha))

        // Verify prayers in list
        val list = times.getPrayerList()
        assertEquals(6, list.size)
        assertEquals(PrayerType.FAJR, list[0].type)
        assertEquals(PrayerType.ISHA, list[5].type)
    }

    @Test
    fun testQiblaDirectionDhaka() {
        val dhaka = LocationData.DEFAULT_DHAKA
        val qiblaBearing = com.example.calculation.QiblaCalculator.calculateQiblaBearing(dhaka.latitude, dhaka.longitude)
        val distanceKm = com.example.calculation.QiblaCalculator.calculateDistanceToMakkahKm(dhaka.latitude, dhaka.longitude)

        // Dhaka is approx 276-279 degrees azimuth to Kaaba (West-Northwest)
        assertTrue(qiblaBearing in 270.0..285.0)
        // Distance is approx 5100-5200 km
        assertTrue(distanceKm in 4800.0..5500.0)
    }

    @Test
    fun testBanglaCalendarConversion() {
        val date = LocalDate.of(2026, 4, 14) // Pohela Boishakh
        val banglaDate = CalendarEngine.gregorianToBangla(date)

        assertEquals(1, banglaDate.day)
        assertEquals("বৈশাখ", banglaDate.monthNameBn)
        assertEquals(1433, banglaDate.year)
    }

    @Test
    fun testBangladeshHolidays() {
        val holidays = CalendarEngine.getBangladeshHolidaysForYear(2026)
        assertTrue(holidays.isNotEmpty())
        assertTrue(holidays.any { it.nameEn.contains("Victory", ignoreCase = true) })
        assertTrue(holidays.any { it.nameEn.contains("Language", ignoreCase = true) })
    }
}
