package com.example.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayer_records",
    indices = [Index(value = ["date", "prayerName"], unique = true)]
)
data class PrayerRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // ISO "YYYY-MM-DD"
    val prayerName: String, // "fajr", "dhuhr", "asr", "maghrib", "isha"
    val status: String, // "PRAYED", "MISSED", "NOT_MARKED"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasbih_records")
data class TasbihRecordEntity(
    @PrimaryKey
    val presetId: String,
    val count: Int,
    val target: Int,
    val totalCount: Long,
    val completedRounds: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
