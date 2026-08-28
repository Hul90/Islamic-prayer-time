package com.example.data.repository

import com.example.calculation.PrayerTimeEngine
import com.example.data.db.PrayerRecordDao
import com.example.data.db.PrayerRecordEntity
import com.example.data.db.TasbihRecordDao
import com.example.data.db.TasbihRecordEntity
import com.example.model.LocationData
import com.example.model.PrayerSettings
import com.example.model.PrayerTimesDay
import com.example.model.PrayerType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PrayerRepository(
    private val prayerRecordDao: PrayerRecordDao,
    private val tasbihRecordDao: TasbihRecordDao
) {
    fun calculatePrayerTimes(
        date: LocalDate,
        location: LocationData,
        settings: PrayerSettings
    ): PrayerTimesDay {
        return PrayerTimeEngine.calculatePrayerTimes(date, location, settings)
    }

    fun getPrayerRecordsForDate(date: LocalDate): Flow<List<PrayerRecordEntity>> {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return prayerRecordDao.getRecordsForDate(dateString)
    }

    suspend fun setPrayerStatus(date: LocalDate, prayerType: PrayerType, status: String) {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val existing = prayerRecordDao.getRecord(dateString, prayerType.id)
        val record = PrayerRecordEntity(
            id = existing?.id ?: 0,
            date = dateString,
            prayerName = prayerType.id,
            status = status,
            timestamp = System.currentTimeMillis()
        )
        prayerRecordDao.insertOrUpdateRecord(record)
    }

    fun getRecordsForRange(startDate: LocalDate, endDate: LocalDate): Flow<List<PrayerRecordEntity>> {
        val startStr = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endStr = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return prayerRecordDao.getRecordsForRange(startStr, endStr)
    }

    fun getTotalPrayedCount(): Flow<Int> = prayerRecordDao.getTotalPrayedCount()

    // Tasbih
    fun getTasbihRecord(presetId: String): Flow<TasbihRecordEntity?> {
        return tasbihRecordDao.getRecordForPreset(presetId)
    }

    fun getAllTasbihRecords(): Flow<List<TasbihRecordEntity>> {
        return tasbihRecordDao.getAllRecords()
    }

    suspend fun saveTasbihRecord(record: TasbihRecordEntity) {
        tasbihRecordDao.saveRecord(record)
    }
}
