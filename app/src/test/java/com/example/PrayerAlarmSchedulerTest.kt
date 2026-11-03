package com.example

import android.app.AlarmManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.LocationData
import com.example.model.PrayerSettings
import com.example.model.PrayerType
import com.example.receiver.PrayerAlarmScheduler
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PrayerAlarmSchedulerTest {

    private lateinit var context: Context
    private lateinit var scheduler: PrayerAlarmScheduler
    private lateinit var shadowAlarmManager: ShadowAlarmManager

    private val testLocation = LocationData(
        latitude = 23.8103,
        longitude = 90.4125,
        cityName = "Dhaka",
        countryName = "Bangladesh",
        timeZoneId = "Asia/Dhaka",
        isAutoDetected = false
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        shadowAlarmManager = shadowOf(alarmManager)
        scheduler = PrayerAlarmScheduler(context)
    }

    @Test
    fun `test request codes are unique across prayer types and days`() {
        val todayCodes = mutableSetOf<Int>()
        val tomorrowCodes = mutableSetOf<Int>()

        for (type in PrayerType.entries) {
            val codeToday = scheduler.getRequestCode(type, isToday = true)
            val codeTomorrow = scheduler.getRequestCode(type, isToday = false)

            assertFalse("Duplicate today request code: $codeToday", todayCodes.contains(codeToday))
            assertFalse("Duplicate tomorrow request code: $codeTomorrow", tomorrowCodes.contains(codeTomorrow))
            assertNotEquals("Today and tomorrow codes must differ for $type", codeToday, codeTomorrow)

            todayCodes.add(codeToday)
            tomorrowCodes.add(codeTomorrow)
        }

        // Verify Sehri codes don't overlap with prayer codes
        assertFalse(todayCodes.contains(PrayerAlarmScheduler.REQUEST_CODE_SEHRI_TODAY))
        assertFalse(tomorrowCodes.contains(PrayerAlarmScheduler.REQUEST_CODE_SEHRI_TOMORROW))
        assertFalse(todayCodes.contains(PrayerAlarmScheduler.REQUEST_CODE_SEHRI_SNOOZE))
    }

    @Test
    fun `test cancelling all alarms clears scheduled alarms`() {
        val settings = PrayerSettings(
            isAzanGloballyEnabled = true,
            isSehriAlarmEnabled = true
        )

        scheduler.scheduleAlarmsForTodayAndTomorrow(testLocation, settings)
        // Verify alarms were scheduled
        val scheduledCount = shadowAlarmManager.scheduledAlarms.size
        assertTrue("Expected scheduled alarms, found $scheduledCount", scheduledCount > 0)

        // Cancel all
        scheduler.cancelAllAlarms()
        // Check that scheduled alarms are now cancelled
        assertEquals(0, shadowAlarmManager.scheduledAlarms.size)
    }

    @Test
    fun `test disabling global azan schedules no prayer alarms`() {
        val settings = PrayerSettings(
            isAzanGloballyEnabled = false,
            isSehriAlarmEnabled = false
        )

        scheduler.scheduleAlarmsForTodayAndTomorrow(testLocation, settings)
        assertEquals(0, shadowAlarmManager.scheduledAlarms.size)
    }

    @Test
    fun `test disabling individual prayers does not schedule that prayer`() {
        val settingsAllDisabledExceptFajr = PrayerSettings(
            isAzanGloballyEnabled = true,
            isSehriAlarmEnabled = false,
            fajrAzan = true,
            dhuhrAzan = false,
            asrAzan = false,
            maghribAzan = false,
            ishaAzan = false
        )

        scheduler.scheduleAlarmsForTodayAndTomorrow(testLocation, settingsAllDisabledExceptFajr)

        // Only Fajr (today/tomorrow if in future) should be scheduled
        for (scheduled in shadowAlarmManager.scheduledAlarms) {
            val intent = shadowOf(scheduled.operation).savedIntent
            val prayerTypeExtra = intent.getStringExtra(com.example.notification.PrayerNotificationManager.EXTRA_PRAYER_TYPE)
            assertEquals(PrayerType.FAJR.id, prayerTypeExtra)
        }
    }
}
