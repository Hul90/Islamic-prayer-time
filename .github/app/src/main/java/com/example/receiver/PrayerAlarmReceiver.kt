package com.example.receiver

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

        if (action == PrayerNotificationManager.ACTION_STOP_AZAN) {
            AzanAudioService.stopAzan(context)
            return
        }

        if (action != PrayerNotificationManager.ACTION_PRAYER_ALARM) return

        val pendingResult = goAsync()
        val app = context.applicationContext as? IslamicPrayerApplication
        if (app == null) {
            pendingResult.finish()
            return
        }

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
}
