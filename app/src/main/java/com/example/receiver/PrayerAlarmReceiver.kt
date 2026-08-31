package com.example.receiver

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.example.IslamicPrayerApplication
import com.example.model.AppLanguage
import com.example.model.AzanSoundType
import com.example.model.PrayerType
import com.example.notification.PrayerNotificationManager
import com.example.service.AzanAudioService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val app = context.applicationContext as? IslamicPrayerApplication

        if (action == PrayerNotificationManager.ACTION_STOP_AZAN) {
            AzanAudioService.stopAzan(context)
            return
        }

        if (action == PrayerNotificationManager.ACTION_DISMISS_SEHRI) {
            AzanAudioService.stopAzan(context)
            app?.notificationManager?.cancelSehriNotification()
            return
        }

        if (action == PrayerNotificationManager.ACTION_SNOOZE_SEHRI) {
            AzanAudioService.stopAzan(context)
            app?.notificationManager?.cancelSehriNotification()

            val sehriTime = intent.getStringExtra(PrayerNotificationManager.EXTRA_SEHRI_TIME) ?: ""
            scheduleSnooze(context, sehriTime)
            return
        }

        if (action == PrayerNotificationManager.ACTION_SEHRI_ALARM) {
            handleSehriAlarm(context, app, intent)
            return
        }

        if (action != PrayerNotificationManager.ACTION_PRAYER_ALARM) return

        handlePrayerAlarm(context, app, intent)
    }

    private fun handleSehriAlarm(context: Context, app: IslamicPrayerApplication?, intent: Intent) {
        if (app == null) return
        val pendingResult = goAsync()

        val sehriTime = intent.getStringExtra(PrayerNotificationManager.EXTRA_SEHRI_TIME) ?: ""
        val offsetMinutes = intent.getIntExtra(PrayerNotificationManager.EXTRA_SEHRI_OFFSET, 0)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = app.settingsRepository.settingsFlow.first()
                val isBangla = settings.language == AppLanguage.BANGLA

                if (settings.sehriAlarmVibration) {
                    try {
                        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 800, 300, 800, 300, 800), -1))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator?.vibrate(longArrayOf(0, 800, 300, 800, 300, 800), -1)
                        }
                    } catch (_: Exception) { }
                }

                // Show high priority full screen alarm notification
                app.notificationManager.showSehriAlarmNotification(
                    sehriTimeStr = sehriTime,
                    isBangla = isBangla,
                    offsetMinutes = offsetMinutes
                )

                // Start alarm sound
                if (settings.sehriAlarmSoundType != AzanSoundType.SILENT) {
                    AzanAudioService.startSehriAlarm(
                        context = context,
                        sehriTime = sehriTime,
                        volume = settings.azanVolume
                    )
                }

                // Reschedule for next days
                val location = app.settingsRepository.savedLocationFlow.first()
                app.alarmScheduler.scheduleAlarmsForTodayAndTomorrow(location, settings)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun handlePrayerAlarm(context: Context, app: IslamicPrayerApplication?, intent: Intent) {
        if (app == null) return
        val pendingResult = goAsync()

        val prayerTypeId = intent.getStringExtra(PrayerNotificationManager.EXTRA_PRAYER_TYPE) ?: PrayerType.FAJR.id
        val prayerType = PrayerType.fromId(prayerTypeId)
        val prayerTime = intent.getStringExtra(PrayerNotificationManager.EXTRA_PRAYER_TIME) ?: ""
        val locationName = intent.getStringExtra(PrayerNotificationManager.EXTRA_LOCATION_NAME) ?: ""
        val asrMethodName = intent.getStringExtra(PrayerNotificationManager.EXTRA_ASR_METHOD) ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = app.settingsRepository.settingsFlow.first()
                val isBangla = settings.language == AppLanguage.BANGLA

                if (settings.isVibrationEnabled) {
                    try {
                        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator?.vibrate(longArrayOf(0, 500, 200, 500), -1)
                        }
                    } catch (_: Exception) { }
                }

                val isAzanPlaying = settings.azanSoundType != AzanSoundType.SILENT
                app.notificationManager.showPrayerNotification(
                    prayerType = prayerType,
                    prayerTimeStr = prayerTime,
                    isAzanPlaying = isAzanPlaying,
                    isBangla = isBangla,
                    locationName = locationName,
                    asrMethodName = asrMethodName
                )

                if (isAzanPlaying) {
                    AzanAudioService.startAzan(
                        context = context,
                        prayerType = prayerType,
                        prayerTime = prayerTime,
                        volume = settings.azanVolume,
                        isFullAzan = settings.azanSoundType == AzanSoundType.FULL_AZAN
                    )
                }

                val location = app.settingsRepository.savedLocationFlow.first()
                app.alarmScheduler.scheduleAlarmsForTodayAndTomorrow(location, settings)
            } finally {
                pendingResult.finish()
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleSnooze(context: Context, sehriTime: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val snoozeEpochMillis = System.currentTimeMillis() + 5 * 60 * 1000 // 5 minutes snooze

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerNotificationManager.ACTION_SEHRI_ALARM
            putExtra(PrayerNotificationManager.EXTRA_SEHRI_TIME, sehriTime)
            putExtra(PrayerNotificationManager.EXTRA_SEHRI_OFFSET, 0)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            PrayerAlarmScheduler.REQUEST_CODE_SEHRI_SNOOZE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeEpochMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeEpochMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeEpochMillis, pendingIntent)
        }
    }
}

