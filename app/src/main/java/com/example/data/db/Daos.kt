package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerRecordDao {
    @Query("SELECT * FROM prayer_records WHERE date = :date")
    fun getRecordsForDate(date: String): Flow<List<PrayerRecordEntity>>

    @Query("SELECT * FROM prayer_records WHERE date = :date AND prayerName = :prayerName LIMIT 1")
    suspend fun getRecord(date: String, prayerName: String): PrayerRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(record: PrayerRecordEntity)

    @Query("SELECT * FROM prayer_records WHERE date BETWEEN :startDate AND :endDate")
    fun getRecordsForRange(startDate: String, endDate: String): Flow<List<PrayerRecordEntity>>

    @Query("SELECT COUNT(*) FROM prayer_records WHERE status = 'PRAYED'")
    fun getTotalPrayedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM prayer_records WHERE status = 'PRAYED' AND date = :date")
    fun getPrayedCountForDate(date: String): Flow<Int>
}

@Dao
interface TasbihRecordDao {
    @Query("SELECT * FROM tasbih_records WHERE presetId = :presetId")
    fun getRecordForPreset(presetId: String): Flow<TasbihRecordEntity?>

    @Query("SELECT * FROM tasbih_records")
    fun getAllRecords(): Flow<List<TasbihRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecord(record: TasbihRecordEntity)

    @Query("DELETE FROM tasbih_records WHERE presetId = :presetId")
    suspend fun deleteRecord(presetId: String)
}
