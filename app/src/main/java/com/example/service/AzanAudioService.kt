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
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.model.PrayerType
import com.example.notification.PrayerNotificationManager
import com.example.receiver.PrayerAlarmReceiver
import kotlin.math.ln

class AzanAudioService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentVolume: Float = 0.9f
    private val handler = Handler(Looper.getMainLooper())
    private var previewStopRunnable: Runnable? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if (action == PrayerNotificationManager.ACTION_STOP_AZAN ||
            action == PrayerNotificationManager.ACTION_DISMISS_SEHRI
        ) {
            stopPlaybackAndService()
            return START_NOT_STICKY
        }

        if (action == ACTION_UPDATE_VOLUME) {
            val newVolume = intent.getFloatExtra("extra_volume", currentVolume)
            currentVolume = newVolume
            updateActivePlayerVolume(newVolume)
            return START_NOT_STICKY
        }

        val volume = intent?.getFloatExtra("extra_volume", 0.9f) ?: 0.9f
        currentVolume = volume
        val isPreview = intent?.getBooleanExtra("extra_is_preview", false) ?: false
        val isSehri = intent?.getBooleanExtra("extra_is_sehri", false) ?: false

        if (isSehri) {
            val sehriTime = intent.getStringExtra(PrayerNotificationManager.EXTRA_SEHRI_TIME) ?: ""
            startSehriForegroundNotification(sehriTime)
            playBundledAzan(volume, PrayerType.FAJR, isPreview = false)
            return START_NOT_STICKY
        }

        val prayerTypeId = intent?.getStringExtra(PrayerNotificationManager.EXTRA_PRAYER_TYPE) ?: PrayerType.FAJR.id
        val prayerType = PrayerType.fromId(prayerTypeId)
        val prayerTime = intent?.getStringExtra(PrayerNotificationManager.EXTRA_PRAYER_TIME) ?: ""
        val isFullAzan = intent?.getBooleanExtra("extra_is_full_azan", true) ?: true

        startForegroundServiceNotification(prayerType, prayerTime, isPreview)

        playBundledAzan(volume, prayerType, isPreview = isPreview)

        if (isPreview) {
            previewStopRunnable?.let { handler.removeCallbacks(it) }
            val runnable = Runnable { stopPlaybackAndService() }
            previewStopRunnable = runnable
            handler.postDelayed(runnable, 6000) // 6 seconds preview
        }

        return START_NOT_STICKY
    }

    private fun startForegroundServiceNotification(prayerType: PrayerType, prayerTime: String, isPreview: Boolean) {
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

        val title = if (isPreview) {
            "Azan Volume Preview (আজান সাউন্ড টেস্ট)"
        } else {
            "Azan: ${prayerType.nameEn} / ${prayerType.nameBn}"
        }

        val content = if (isPreview) {
            "Playing audio sample to test volume settings."
        } else {
            "Azan audio is playing ($prayerTime)."
        }

        val notification: Notification = NotificationCompat.Builder(this, PrayerNotificationManager.CHANNEL_AZAN_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(!isPreview)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Stop (বন্ধ করুন)", stopAzanPendingIntent)
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

    private fun startSehriForegroundNotification(sehriTime: String) {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, PrayerAlarmReceiver::class.java).apply {
            action = PrayerNotificationManager.ACTION_DISMISS_SEHRI
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            1003,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, PrayerNotificationManager.CHANNEL_SEHRI_ALARM_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("🌙 Sehri Alarm (সেহরি অ্যালার্ম)")
            .setContentText("Wake up for Sehri ($sehriTime)")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Stop Alarm (অ্যালার্ম বন্ধ করুন)", stopPendingIntent)
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

    private fun playBundledAzan(volume: Float, prayerType: PrayerType, isPreview: Boolean) {
        stopPlayerOnly()

        // Fajr -> 002 azan, Others -> 001 azan
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
            val gain = calculateAcousticGain(volume)
            player.setVolume(gain, gain)
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

    private fun updateActivePlayerVolume(volume: Float) {
        try {
            mediaPlayer?.let { player ->
                val gain = calculateAcousticGain(volume)
                player.setVolume(gain, gain)
            }
        } catch (_: Exception) { }
    }

    private fun calculateAcousticGain(volume: Float): Float {
        val clamped = volume.coerceIn(0.01f, 1.0f)
        // Logarithmic volume mapping: human ears perceive loudness logarithmically.
        val gain = 1f - (ln(101f - clamped * 100f) / ln(101f))
        return gain.coerceIn(0.01f, 1.0f)
    }

    private fun stopPlayerOnly() {
        previewStopRunnable?.let {
            handler.removeCallbacks(it)
            previewStopRunnable = null
        }
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
        const val ACTION_UPDATE_VOLUME = "com.aistudio.islamicprayer.ACTION_UPDATE_VOLUME"

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

        fun startSehriAlarm(
            context: Context,
            sehriTime: String,
            volume: Float
        ) {
            val intent = Intent(context, AzanAudioService::class.java).apply {
                putExtra("extra_is_sehri", true)
                putExtra(PrayerNotificationManager.EXTRA_SEHRI_TIME, sehriTime)
                putExtra("extra_volume", volume)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun startPreview(
            context: Context,
            volume: Float
        ) {
            val intent = Intent(context, AzanAudioService::class.java).apply {
                putExtra("extra_is_preview", true)
                putExtra(PrayerNotificationManager.EXTRA_PRAYER_TYPE, PrayerType.FAJR.id)
                putExtra("extra_volume", volume)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun updateVolume(context: Context, volume: Float) {
            val intent = Intent(context, AzanAudioService::class.java).apply {
                action = ACTION_UPDATE_VOLUME
                putExtra("extra_volume", volume)
            }
            context.startService(intent)
        }

        fun stopAzan(context: Context) {
            context.stopService(Intent(context, AzanAudioService::class.java))
        }
    }
}

