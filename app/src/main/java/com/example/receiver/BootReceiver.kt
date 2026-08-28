package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.IslamicPrayerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            val app = context.applicationContext as? IslamicPrayerApplication ?: return
            CoroutineScope(Dispatchers.Default).launch {
                val location = app.settingsRepository.savedLocationFlow.first()
                val settings = app.settingsRepository.settingsFlow.first()
                app.alarmScheduler.scheduleAlarmsForTodayAndTomorrow(location, settings)
            }
        }
    }
}
