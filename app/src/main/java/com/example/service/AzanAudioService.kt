package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.model.PrayerType
import com.example.notification.PrayerNotificationManager
import com.example.receiver.PrayerAlarmReceiver
import kotlinx.coroutines.*
import kotlin.math.sin

class AzanAudioService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var isPlaying = false
    private var audioTrack: AudioTrack? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == PrayerNotificationManager.ACTION_STOP_AZAN) {
            stopPlaybackAndService()
            return START_NOT_STICKY
        }

        val prayerTypeId = intent?.getStringExtra(PrayerNotificationManager.EXTRA_PRAYER_TYPE) ?: PrayerType.FAJR.id
        val prayerType = PrayerType.fromId(prayerTypeId)
        val prayerTime = intent?.getStringExtra(PrayerNotificationManager.EXTRA_PRAYER_TIME) ?: ""
        val volume = intent?.getFloatExtra("extra_volume", 0.9f) ?: 0.9f
        val isFullAzan = intent?.getBooleanExtra("extra_is_full_azan", true) ?: true

        startForegroundServiceNotification(prayerType, prayerTime)
        playHarmonicAzan(prayerType, volume, isFullAzan)

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
            .setContentText("Azan audio is currently playing ($prayerTime).")
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

    private fun playHarmonicAzan(prayerType: PrayerType, volume: Float, isFullAzan: Boolean) {
        if (isPlaying) return
        isPlaying = true

        serviceScope.launch {
            try {
                // Synthesize melodic, peaceful resonant prayer tones (Adhan motif in Maqam Bayati/Rast tuning)
                val sampleRate = 44100
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .build()

                val track = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(minBufferSize * 4)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                track.setVolume(volume.coerceIn(0.1f, 1.0f))
                track.play()

                // Melodic notes sequence representing serene prayer chime / call
                // Notes in Hz: D4(293.66), F4(349.23), G4(392.00), A4(440.00), Bb4(466.16), C5(523.25), D5(587.33)
                val notes = if (isFullAzan) {
                    listOf(
                        // Allahu Akbar phrase
                        Pair(392.0, 1.2), Pair(440.0, 0.8), Pair(392.0, 1.5), Pair(349.2, 1.8),
                        Pair(0.0, 0.5),
                        Pair(392.0, 1.2), Pair(440.0, 0.8), Pair(392.0, 1.5), Pair(349.2, 1.8),
                        Pair(0.0, 0.7),
                        // Ash-hadu alla ilaha illallah
                        Pair(349.2, 1.0), Pair(392.0, 1.2), Pair(440.0, 1.5), Pair(392.0, 1.0), Pair(349.2, 2.0),
                        Pair(0.0, 0.6),
                        // Hayya alas-Salah
                        Pair(440.0, 1.2), Pair(523.2, 1.4), Pair(466.1, 1.2), Pair(440.0, 2.0),
                        Pair(0.0, 0.6),
                        // Allahu Akbar, La ilaha illallah
                        Pair(392.0, 1.2), Pair(349.2, 1.5), Pair(293.6, 2.5)
                    )
                } else {
                    // Short soft chime
                    listOf(
                        Pair(440.0, 0.6), Pair(523.2, 0.8), Pair(587.3, 1.2)
                    )
                }

                for ((freq, durationSec) in notes) {
                    if (!isPlaying) break

                    if (freq <= 0.0) {
                        delay((durationSec * 1000).toLong())
                        continue
                    }

                    val totalSamples = (sampleRate * durationSec).toInt()
                    val buffer = ShortArray(totalSamples)
                    val twoPiF = 2.0 * Math.PI * freq / sampleRate

                    for (i in 0 until totalSamples) {
                        // Gentle envelope to avoid click and add warm harmonic overtones
                        val envelope = when {
                            i < sampleRate * 0.05 -> i / (sampleRate * 0.05)
                            i > totalSamples - (sampleRate * 0.1) -> (totalSamples - i) / (sampleRate * 0.1)
                            else -> 1.0
                        }

                        // Fundamental + warm harmonic octaves
                        val sampleVal = (sin(twoPiF * i) + 0.3 * sin(2.0 * twoPiF * i) + 0.1 * sin(3.0 * twoPiF * i)) * envelope
                        buffer[i] = (sampleVal * 16000).toInt().coerceIn(-32767, 32767).toShort()
                    }

                    track.write(buffer, 0, buffer.size)
                }

                delay(1000)
            } catch (e: Exception) {
                // Fallback to default ringtone if synthesis encounters limitation
                try {
                    val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(applicationContext, defaultUri)
                    r.play()
                } catch (_: Exception) {}
            } finally {
                stopPlaybackAndService()
            }
        }
    }

    private fun stopPlaybackAndService() {
        isPlaying = false
        serviceScope.cancel()
        try {
            audioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
            audioTrack = null
        } catch (_: Exception) {}

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopPlaybackAndService()
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
            val intent = Intent(context, AzanAudioService::class.java).apply {
                action = PrayerNotificationManager.ACTION_STOP_AZAN
            }
            context.startService(intent)
        }
    }
}
