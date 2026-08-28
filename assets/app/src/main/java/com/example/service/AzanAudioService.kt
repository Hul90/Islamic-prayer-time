package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.model.PrayerType
import com.example.notification.PrayerNotificationManager
import com.example.receiver.PrayerAlarmReceiver

class AzanAudioService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == PrayerNotificationManager.ACTION_STOP_AZAN) {
            stopPlaybackAndService()
            return START_NOT_STICKY
        }

        val prayerTypeId = intent?.getStringExtra(PrayerNotificationManager.EXTRA_PRAYER_TYPE) ?: PrayerType.FAJR.id
        val prayerType = PrayerType.fromId(prayerTypeId)
        val prayerTime = intent?.getStringExtra(PrayerNotificationManager.EXTRA_PRAYER_TIME) ?: ""
        val volume = intent?.getFloatExtra("extra_volume", 0.9f) ?: 0.9f
        val isFullAzan = intent?.getBooleanExtra("extra_is_full_azan", true) ?: true

        startForegroundServiceNotification(prayerType, prayerTime)

        if (isFullAzan) {
            playBundledAzan(volume, prayerType)
        } else {
            // Keep the optional short-sound mode, but never synthesize a fake Azan.
            playBundledAzan(volume, prayerType)
        }

        return START_NOT_STICKY
    }

    private fun startForegroundServiceNotification(prayerType: PrayerType, prayerTime: String) {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopAzanIntent = Intent(this, PrayerAlarmReceiver::class.java).apply {
            action = PrayerNotificationManager.ACTION_STOP_AZAN
        }
        val stopAzanPendingIntent = PendingIntent.getBroadcast(
            this,
            1002,
            stopAzanIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, PrayerNotificationManager.CHANNEL_AZAN_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Azan: ${prayerType.nameEn} / ${prayerType.nameBn}")
            .setContentText("Azan audio is playing ($prayerTime).")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Stop Azan (আজান বন্ধ করুন)", stopAzanPendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                PrayerNotificationManager.NOTIFICATION_AZAN_SERVICE_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(PrayerNotificationManager.NOTIFICATION_AZAN_SERVICE_ID, notification)
        }
    }

    private fun playBundledAzan(volume: Float, prayerType: PrayerType) {
        stopPlayerOnly()

        // User-supplied Azan mapping:
        // Fajr -> 002 azan
        // Dhuhr, Asr, Maghrib, Isha -> 001 azan
        val resourceName = if (prayerType == PrayerType.FAJR) "azan_002" else "azan_001"
        val resourceId = resources.getIdentifier(resourceName, "raw", packageName)
        if (resourceId == 0) {
            stopPlaybackAndService()
            return
        }

        try {
            val player = MediaPlayer()
            mediaPlayer = player
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            resources.openRawResourceFd(resourceId).use { afd ->
                player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
            player.prepare()
            val safeVolume = volume.coerceIn(0.0f, 1.0f)
            player.setVolume(safeVolume, safeVolume)
            player.setOnCompletionListener { stopPlaybackAndService() }
            player.setOnErrorListener { _, _, _ ->
                stopPlaybackAndService()
                true
            }
            player.start()
        } catch (_: Exception) {
            stopPlaybackAndService()
        }
    }

    private fun stopPlayerOnly() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.reset()
                it.release()
            }
        } catch (_: Exception) { }
        mediaPlayer = null
    }

    private fun stopPlaybackAndService() {
        stopPlayerOnly()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    override fun onDestroy() {
        stopPlayerOnly()
        super.onDestroy()
    }

    companion object {
        fun startAzan(
            context: Context,
            prayerType: PrayerType,
            prayerTime: String,
            volume: Float,
            isFullAzan: Boolean
        ) {
            val intent = Intent(context, AzanAudioService::class.java).apply {
                putExtra(PrayerNotificationManager.EXTRA_PRAYER_TYPE, prayerType.id)
                putExtra(PrayerNotificationManager.EXTRA_PRAYER_TIME, prayerTime)
                putExtra("extra_volume", volume)
                putExtra("extra_is_full_azan", isFullAzan)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopAzan(context: Context) {
            // Stop the service directly; starting a background service just to stop it
            // can be rejected on newer Android versions.
            context.stopService(Intent(context, AzanAudioService::class.java))
        }
    }
}
