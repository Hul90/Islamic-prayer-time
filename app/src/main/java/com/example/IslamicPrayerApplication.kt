package com.example

import android.app.Application
import android.content.Context
import com.example.data.db.IslamicPrayerDatabase
import com.example.data.repository.LocationRepository
import com.example.data.repository.PrayerRepository
import com.example.data.repository.SettingsRepository
import com.example.notification.PrayerNotificationManager
import com.example.receiver.PrayerAlarmScheduler

class IslamicPrayerApplication : Application() {

    lateinit var database: IslamicPrayerDatabase
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var locationRepository: LocationRepository
        private set

    lateinit var prayerRepository: PrayerRepository
        private set

    lateinit var notificationManager: PrayerNotificationManager
        private set

    lateinit var alarmScheduler: PrayerAlarmScheduler
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = IslamicPrayerDatabase.getDatabase(this)
        settingsRepository = SettingsRepository(this)
        locationRepository = LocationRepository(this)
        prayerRepository = PrayerRepository(database.prayerRecordDao(), database.tasbihRecordDao())
        notificationManager = PrayerNotificationManager(this)
        alarmScheduler = PrayerAlarmScheduler(this)

        notificationManager.createNotificationChannels()
    }

    companion object {
        lateinit var instance: IslamicPrayerApplication
            private set

        fun getAppContext(): Context = instance.applicationContext
    }
}
