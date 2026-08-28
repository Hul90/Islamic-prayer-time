package com.example

import com.example.calculation.PrayerTimeEngine
import com.example.model.AsrJuristicMethod
import com.example.model.LocationData
import com.example.model.PrayerCalculationMethod
import com.example.model.PrayerSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AsrCalculationTest {

    @Test
    fun `test asr calculation for Tongi Bangladesh with Shafi then Hanafi then Shafi`() {
        // Tongi, Bangladesh Coordinates: 23.8950° N, 90.4043° E, Timezone: Asia/Dhaka
        val tongiLocation = LocationData(
            latitude = 23.8950,
            longitude = 90.4043,
            cityName = "Tongi (টঙ্গী)",
            countryName = "Bangladesh",
            timeZoneId = "Asia/Dhaka",
            isAutoDetected = false
        )

        val date = LocalDate.of(2026, 8, 27)

        // 1. Initial Shafi calculation (Shadow Factor = 1)
        val shafiSettings = PrayerSettings(
            calculationMethod = PrayerCalculationMethod.KARACHI,
            asrMethod = AsrJuristicMethod.SHAFI
        )
        val shafiTimes = PrayerTimeEngine.calculatePrayerTimes(date, tongiLocation, shafiSettings)

        // 2. Switch Shafi -> Hanafi (Shadow Factor = 2)
        val hanafiSettings = PrayerSettings(
            calculationMethod = PrayerCalculationMethod.KARACHI,
            asrMethod = AsrJuristicMethod.HANAFI
        )
        val hanafiTimes = PrayerTimeEngine.calculatePrayerTimes(date, tongiLocation, hanafiSettings)

        // Confirm Asr changes and Hanafi is later than Shafi
        assertNotEquals(shafiTimes.asr, hanafiTimes.asr)
        assertTrue("Hanafi Asr must be later than Shafi Asr", hanafiTimes.asr.isAfter(shafiTimes.asr))

        // 3. Switch Hanafi -> Shafi
        val returnShafiTimes = PrayerTimeEngine.calculatePrayerTimes(date, tongiLocation, shafiSettings)

        // Confirm Asr returns to the exact Shafi calculation
        assertEquals(shafiTimes.asr, returnShafiTimes.asr)

        // 4. Also check Maliki and Hanbali are equal to Shafi (1x Shadow)
        val malikiSettings = PrayerSettings(calculationMethod = PrayerCalculationMethod.KARACHI, asrMethod = AsrJuristicMethod.MALIKI)
        val hanbaliSettings = PrayerSettings(calculationMethod = PrayerCalculationMethod.KARACHI, asrMethod = AsrJuristicMethod.HANBALI)
        val malikiTimes = PrayerTimeEngine.calculatePrayerTimes(date, tongiLocation, malikiSettings)
        val hanbaliTimes = PrayerTimeEngine.calculatePrayerTimes(date, tongiLocation, hanbaliSettings)

        assertEquals(shafiTimes.asr, malikiTimes.asr)
        assertEquals(shafiTimes.asr, hanbaliTimes.asr)
    }
}
