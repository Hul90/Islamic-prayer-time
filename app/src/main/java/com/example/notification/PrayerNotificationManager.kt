package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.model.PrayerType
import com.example.receiver.PrayerAlarmReceiver

class PrayerNotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 1. Prayer Notification Channel
            val prayerChannel = NotificationChannel(
                CHANNEL_PRAYER_ID,
                "Prayer Time Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily Islamic prayer notifications"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }

            // 2. Azan Foreground Channel
            val azanChannel = NotificationChannel(
                CHANNEL_AZAN_ID,
                "Azan Audio Playback",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Active Azan playback controls"
                enableVibration(true)
            }

            // 3. General Channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL_ID,
                "General Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General Islamic reminders and updates"
            }

            notificationManager.createNotificationChannel(prayerChannel)
            notificationManager.createNotificationChannel(azanChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }

    fun showPrayerNotification(
        prayerType: PrayerType,
        prayerTimeStr: String,
        isAzanPlaying: Boolean,
        isBangla: Boolean,
        locationName: String = "",
        asrMethodName: String = ""
    ) {
        val prayerName = if (isBangla) prayerType.nameBn else prayerType.nameEn
        val title = if (isBangla) {
            "নামাজের ওয়াক্ত হয়েছে: $prayerName"
        } else {
            "Time for $prayerName Prayer"
        }

        val locationSuffix = if (locationName.isNotBlank()) " • $locationName" else ""
        val asrSuffix = if (prayerType == PrayerType.ASR && asrMethodName.isNotBlank()) " ($asrMethodName)" else ""

        val content = if (isBangla) {
            "এখন $prayerName নামাজের ওয়াক্ত ($prayerTimeStr)$asrSuffix$locationSuffix। নামাজে মশগুল হোন।"
        } else {
            "It is now time for $prayerName prayer ($prayerTimeStr)$asrSuffix$locationSuffix. Take a moment to pray."
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            prayerType.ordinal,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopAzanIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = ACTION_STOP_AZAN
        }
        val stopAzanPendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            stopAzanIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_PRAYER_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)

        if (isAzanPlaying) {
            val stopText = if (isBangla) "আজান বন্ধ করুন" else "Stop Azan"
            builder.addAction(
                android.R.drawable.ic_media_pause,
                stopText,
                stopAzanPendingIntent
            )
        }

        notificationManager.notify(NOTIFICATION_PRAYER_BASE_ID + prayerType.ordinal, builder.build())
    }

    fun cancelAllPrayerNotifications() {
        for (type in PrayerType.entries) {
            notificationManager.cancel(NOTIFICATION_PRAYER_BASE_ID + type.ordinal)
        }
    }

    companion object {
        const val CHANNEL_PRAYER_ID = "islamic_prayer_alert_channel"
        const val CHANNEL_AZAN_ID = "islamic_azan_playback_channel"
        const val CHANNEL_GENERAL_ID = "islamic_general_channel"

        const val NOTIFICATION_PRAYER_BASE_ID = 2000
        const val NOTIFICATION_AZAN_SERVICE_ID = 3000

        const val ACTION_STOP_AZAN = "com.aistudio.islamicprayer.ACTION_STOP_AZAN"
        const val ACTION_PRAYER_ALARM = "com.aistudio.islamicprayer.ACTION_PRAYER_ALARM"
        const val EXTRA_PRAYER_TYPE = "extra_prayer_type"
        const val EXTRA_PRAYER_TIME = "extra_prayer_time"
        const val EXTRA_LOCATION_NAME = "extra_location_name"
        const val EXTRA_ASR_METHOD = "extra_asr_method"
    }
}
