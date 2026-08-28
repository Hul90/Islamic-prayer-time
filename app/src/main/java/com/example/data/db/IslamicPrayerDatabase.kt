package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PrayerRecordEntity::class, TasbihRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class IslamicPrayerDatabase : RoomDatabase() {
    abstract fun prayerRecordDao(): PrayerRecordDao
    abstract fun tasbihRecordDao(): TasbihRecordDao

    companion object {
        @Volatile
        private var INSTANCE: IslamicPrayerDatabase? = null

        fun getDatabase(context: Context): IslamicPrayerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IslamicPrayerDatabase::class.java,
                    "islamic_prayer_sultan.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
