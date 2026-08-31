package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
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
                setSound(null, null)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }

            // 2. Azan Foreground Channel
            val azanChannel = NotificationChannel(
                CHANNEL_AZAN_ID,
                "Azan Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Active Azan playback controls"
                setSound(null, null)
                enableVibration(false)
            }

            // 3. General Channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL_ID,
                "General Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General Islamic reminders and updates"
            }

            // 4. Sehri Wake-up Alarm Channel
            val sehriChannel = NotificationChannel(
                CHANNEL_SEHRI_ALARM_ID,
                "Sehri Wake-up Alarm (সেহরি অ্যালার্ম)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm alerts for Sehri wake-up"
                setSound(null, null)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 800, 300, 800, 300, 800)
            }

            notificationManager.createNotificationChannel(prayerChannel)
            notificationManager.createNotificationChannel(azanChannel)
            notificationManager.createNotificationChannel(generalChannel)
            notificationManager.createNotificationChannel(sehriChannel)
        }
    }

    fun showSehriAlarmNotification(
        sehriTimeStr: String,
        isBangla: Boolean,
        offsetMinutes: Int = 0
    ) {
        val title = if (isBangla) "🌙 সেহরির সময় হয়েছে! ঘুম থেকে উঠুন" else "🌙 Time for Sehri! Wake Up"
        val content = if (isBangla) {
            if (offsetMinutes > 0) {
                "সেহরি শেষ হওয়ার $offsetMinutes মিনিট পূর্বে অ্যালার্ম। সেহরি শেষ: $sehriTimeStr"
            } else {
                "সেহরির অ্যালার্ম ($sehriTimeStr)। সময়মতো সেহরি গ্রহণ করুন।"
            }
        } else {
            if (offsetMinutes > 0) {
                "Alarm set $offsetMinutes min before Sehri ends. Sehri ends at: $sehriTimeStr"
            } else {
                "Sehri wake-up alarm ($sehriTimeStr). Have your blessed meal on time."
            }
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_SEHRI_ALARM_ID,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss action
        val dismissIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = ACTION_DISMISS_SEHRI
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            2001,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze action (5 min)
        val snoozeIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = ACTION_SNOOZE_SEHRI
            putExtra(EXTRA_SEHRI_TIME, sehriTimeStr)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            2002,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_SEHRI_ALARM_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                if (isBangla) "বন্ধ করুন" else "Dismiss",
                dismissPendingIntent
            )
            .addAction(
                android.R.drawable.ic_lock_idle_alarm,
                if (isBangla) "৫ মি. স্নুজ" else "5m Snooze",
                snoozePendingIntent
            )

        notificationManager.notify(NOTIFICATION_SEHRI_ALARM_ID, builder.build())
    }

    fun cancelSehriNotification() {
        notificationManager.cancel(NOTIFICATION_SEHRI_ALARM_ID)
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
        const val CHANNEL_PRAYER_ID = "islamic_prayer_alert_channel_v2"
        const val CHANNEL_AZAN_ID = "islamic_azan_playback_channel_v2"
        const val CHANNEL_GENERAL_ID = "islamic_general_channel"
        const val CHANNEL_SEHRI_ALARM_ID = "islamic_sehri_alarm_channel_v2"

        const val NOTIFICATION_PRAYER_BASE_ID = 2000
        const val NOTIFICATION_AZAN_SERVICE_ID = 3000
        const val NOTIFICATION_SEHRI_ALARM_ID = 4000

        const val ACTION_STOP_AZAN = "com.aistudio.islamicprayer.ACTION_STOP_AZAN"
        const val ACTION_PRAYER_ALARM = "com.aistudio.islamicprayer.ACTION_PRAYER_ALARM"
        const val ACTION_SEHRI_ALARM = "com.aistudio.islamicprayer.ACTION_SEHRI_ALARM"
        const val ACTION_DISMISS_SEHRI = "com.aistudio.islamicprayer.ACTION_DISMISS_SEHRI"
        const val ACTION_SNOOZE_SEHRI = "com.aistudio.islamicprayer.ACTION_SNOOZE_SEHRI"

        const val EXTRA_PRAYER_TYPE = "extra_prayer_type"
        const val EXTRA_PRAYER_TIME = "extra_prayer_time"
        const val EXTRA_LOCATION_NAME = "extra_location_name"
        const val EXTRA_ASR_METHOD = "extra_asr_method"
        const val EXTRA_SEHRI_TIME = "extra_sehri_time"
        const val EXTRA_SEHRI_OFFSET = "extra_sehri_offset"
    }
}
