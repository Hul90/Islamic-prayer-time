package com.example.receiver

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.calculation.PrayerTimeEngine
import com.example.model.*
import com.example.notification.PrayerNotificationManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class PrayerAlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarmsForTodayAndTomorrow(
        location: LocationData,
        settings: PrayerSettings
    ) {
        cancelAllAlarms()

        val zoneId = try {
            ZoneId.of(location.timeZoneId)
        } catch (_: Exception) {
            ZoneId.systemDefault()
        }
        val today = LocalDate.now(zoneId)
        val tomorrow = today.plusDays(1)

        // 1. Schedule Prayer Azan Alarms
        if (settings.isAzanGloballyEnabled) {
            scheduleDayPrayers(today, location, settings, isToday = true)
            scheduleDayPrayers(tomorrow, location, settings, isToday = false)
        }

        // 2. Schedule Sehri Wake-up Alarms
        if (settings.isSehriAlarmEnabled) {
            scheduleDaySehriAlarm(today, location, settings, isToday = true)
            scheduleDaySehriAlarm(tomorrow, location, settings, isToday = false)
        }
    }

    private fun scheduleDayPrayers(
        date: LocalDate,
        location: LocationData,
        settings: PrayerSettings,
        isToday: Boolean
    ) {
        val prayerTimes = PrayerTimeEngine.calculatePrayerTimes(date, location, settings)
        val zoneId = try {
            ZoneId.of(location.timeZoneId)
        } catch (_: Exception) {
            ZoneId.systemDefault()
        }
        val now = LocalDateTime.now(zoneId)

        val prayers = listOf(
            Pair(PrayerType.FAJR, prayerTimes.fajr),
            Pair(PrayerType.DHUHR, prayerTimes.dhuhr),
            Pair(PrayerType.ASR, prayerTimes.asr),
            Pair(PrayerType.MAGHRIB, prayerTimes.maghrib),
            Pair(PrayerType.ISHA, prayerTimes.isha)
        )

        for ((type, time) in prayers) {
            // Check if user has enabled alarm for this specific prayer
            val isEnabled = when (type) {
                PrayerType.FAJR -> settings.fajrAzan
                PrayerType.DHUHR -> settings.dhuhrAzan
                PrayerType.ASR -> settings.asrAzan
                PrayerType.MAGHRIB -> settings.maghribAzan
                PrayerType.ISHA -> settings.ishaAzan
                else -> false
            }

            if (!isEnabled) continue

            val prayerDateTime = LocalDateTime.of(date, time)
            if (prayerDateTime.isBefore(now)) continue // Already passed

            val epochMillis = prayerDateTime.atZone(zoneId).toInstant().toEpochMilli()
            val requestCode = getRequestCode(type, isToday)

            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = PrayerNotificationManager.ACTION_PRAYER_ALARM
                putExtra(PrayerNotificationManager.EXTRA_PRAYER_TYPE, type.id)
                putExtra(PrayerNotificationManager.EXTRA_PRAYER_TIME, time.toString())
                putExtra(PrayerNotificationManager.EXTRA_LOCATION_NAME, location.displayLocation(settings.language == AppLanguage.BANGLA))
                putExtra(PrayerNotificationManager.EXTRA_ASR_METHOD, if (settings.language == AppLanguage.BANGLA) settings.asrMethod.nameBn else "${settings.asrMethod.nameEn} calculation")
            }

            setAlarm(epochMillis, requestCode, intent)
        }
    }

    private fun scheduleDaySehriAlarm(
        date: LocalDate,
        location: LocationData,
        settings: PrayerSettings,
        isToday: Boolean
    ) {
        val prayerTimes = PrayerTimeEngine.calculatePrayerTimes(date, location, settings)
        val zoneId = try {
            ZoneId.of(location.timeZoneId)
        } catch (_: Exception) {
            ZoneId.systemDefault()
        }
        val now = LocalDateTime.now(zoneId)

        val alarmTime: LocalTime = when (settings.sehriAlarmMode) {
            SehriAlarmMode.BEFORE_SEHRI_END -> {
                prayerTimes.sehriEnd.minusMinutes(settings.sehriAlarmOffsetMinutes.toLong())
            }
            SehriAlarmMode.CUSTOM_TIME -> {
                LocalTime.of(
                    settings.sehriAlarmCustomHour.coerceIn(0, 23),
                    settings.sehriAlarmCustomMinute.coerceIn(0, 59)
                )
            }
        }

        val alarmDateTime = LocalDateTime.of(date, alarmTime)
        if (alarmDateTime.isBefore(now)) return // Already passed today

        val epochMillis = alarmDateTime.atZone(zoneId).toInstant().toEpochMilli()
        val requestCode = if (isToday) REQUEST_CODE_SEHRI_TODAY else REQUEST_CODE_SEHRI_TOMORROW

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerNotificationManager.ACTION_SEHRI_ALARM
            putExtra(PrayerNotificationManager.EXTRA_SEHRI_TIME, prayerTimes.sehriEnd.toString())
            putExtra(PrayerNotificationManager.EXTRA_SEHRI_OFFSET, settings.sehriAlarmOffsetMinutes)
        }

        setAlarm(epochMillis, requestCode, intent)
    }

    private fun setAlarm(epochMillis: Long, requestCode: Int, intent: Intent) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val canUseExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }
            if (canUseExact) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        epochMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
                }
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
        }
    }

    fun cancelAllAlarms() {
        for (isToday in listOf(true, false)) {
            for (type in PrayerType.entries) {
                val requestCode = getRequestCode(type, isToday)
                val intent = Intent(context, PrayerAlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                }
            }
        }

        // Cancel Sehri alarms
        for (requestCode in listOf(REQUEST_CODE_SEHRI_TODAY, REQUEST_CODE_SEHRI_TOMORROW, REQUEST_CODE_SEHRI_SNOOZE)) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    private fun getRequestCode(prayerType: PrayerType, isToday: Boolean): Int {
        val base = prayerType.ordinal * 10
        return if (isToday) base else base + 100
    }

    companion object {
        const val REQUEST_CODE_SEHRI_TODAY = 500
        const val REQUEST_CODE_SEHRI_TOMORROW = 501
        const val REQUEST_CODE_SEHRI_SNOOZE = 502
    }
}

