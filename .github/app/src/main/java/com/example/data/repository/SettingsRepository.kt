package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "islamic_prayer_settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val CALCULATION_METHOD = stringPreferencesKey("calculation_method")
        val ASR_METHOD = stringPreferencesKey("asr_method")
        val HIGH_LATITUDE_ADJUSTMENT = stringPreferencesKey("high_latitude_adj")
        val IS_24_HOUR = booleanPreferencesKey("is_24_hour")

        val FAJR_OFFSET = intPreferencesKey("fajr_offset")
        val DHUHR_OFFSET = intPreferencesKey("dhuhr_offset")
        val ASR_OFFSET = intPreferencesKey("asr_offset")
        val MAGHRIB_OFFSET = intPreferencesKey("maghrib_offset")
        val ISHA_OFFSET = intPreferencesKey("isha_offset")

        val IS_AZAN_GLOBALLY_ENABLED = booleanPreferencesKey("is_azan_globally_enabled")
        val FAJR_AZAN = booleanPreferencesKey("fajr_azan")
        val DHUHR_AZAN = booleanPreferencesKey("dhuhr_azan")
        val ASR_AZAN = booleanPreferencesKey("asr_azan")
        val MAGHRIB_AZAN = booleanPreferencesKey("maghrib_azan")
        val ISHA_AZAN = booleanPreferencesKey("isha_azan")
        val AZAN_SOUND_TYPE = stringPreferencesKey("azan_sound_type")
        val AZAN_VOLUME = floatPreferencesKey("azan_volume")
        val IS_VIBRATION = booleanPreferencesKey("is_vibration")

        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val IS_RAMADAN_MODE = booleanPreferencesKey("is_ramadan_mode")
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")

        // Saved Location
        val LATITUDE = doublePreferencesKey("saved_latitude")
        val LONGITUDE = doublePreferencesKey("saved_longitude")
        val CITY_NAME = stringPreferencesKey("saved_city_name")
        val COUNTRY_NAME = stringPreferencesKey("saved_country_name")
        val TIMEZONE_ID = stringPreferencesKey("saved_timezone_id")
        val IS_AUTO_LOCATION = booleanPreferencesKey("is_auto_location")
    }

    val settingsFlow: Flow<PrayerSettings> = context.dataStore.data.map { prefs ->
        PrayerSettings(
            calculationMethod = PrayerCalculationMethod.fromId(prefs[PreferencesKeys.CALCULATION_METHOD] ?: "karachi"),
            asrMethod = AsrJuristicMethod.fromId(prefs[PreferencesKeys.ASR_METHOD] ?: "shafi"),
            highLatitudeAdjustment = HighLatitudeAdjustment.fromId(prefs[PreferencesKeys.HIGH_LATITUDE_ADJUSTMENT] ?: "none"),
            is24HourFormat = prefs[PreferencesKeys.IS_24_HOUR] ?: false,
            fajrOffsetMinutes = prefs[PreferencesKeys.FAJR_OFFSET] ?: 0,
            dhuhrOffsetMinutes = prefs[PreferencesKeys.DHUHR_OFFSET] ?: 0,
            asrOffsetMinutes = prefs[PreferencesKeys.ASR_OFFSET] ?: 0,
            maghribOffsetMinutes = prefs[PreferencesKeys.MAGHRIB_OFFSET] ?: 0,
            ishaOffsetMinutes = prefs[PreferencesKeys.ISHA_OFFSET] ?: 0,
            isAzanGloballyEnabled = prefs[PreferencesKeys.IS_AZAN_GLOBALLY_ENABLED] ?: true,
            fajrAzan = prefs[PreferencesKeys.FAJR_AZAN] ?: true,
            dhuhrAzan = prefs[PreferencesKeys.DHUHR_AZAN] ?: true,
            asrAzan = prefs[PreferencesKeys.ASR_AZAN] ?: true,
            maghribAzan = prefs[PreferencesKeys.MAGHRIB_AZAN] ?: true,
            ishaAzan = prefs[PreferencesKeys.ISHA_AZAN] ?: true,
            azanSoundType = AzanSoundType.fromId(prefs[PreferencesKeys.AZAN_SOUND_TYPE] ?: "full_azan"),
            azanVolume = prefs[PreferencesKeys.AZAN_VOLUME] ?: 0.9f,
            isVibrationEnabled = prefs[PreferencesKeys.IS_VIBRATION] ?: true,
            themeMode = AppThemeMode.fromId(prefs[PreferencesKeys.THEME_MODE] ?: "system"),
            language = AppLanguage.fromId(prefs[PreferencesKeys.LANGUAGE] ?: "bn"),
            isRamadanModeActive = prefs[PreferencesKeys.IS_RAMADAN_MODE] ?: false,
            isOnboardingCompleted = prefs[PreferencesKeys.IS_ONBOARDING_COMPLETED] ?: false
        )
    }

    val savedLocationFlow: Flow<LocationData> = context.dataStore.data.map { prefs ->
        LocationData(
            latitude = prefs[PreferencesKeys.LATITUDE] ?: LocationData.DEFAULT_DHAKA.latitude,
            longitude = prefs[PreferencesKeys.LONGITUDE] ?: LocationData.DEFAULT_DHAKA.longitude,
            cityName = prefs[PreferencesKeys.CITY_NAME] ?: LocationData.DEFAULT_DHAKA.cityName,
            countryName = prefs[PreferencesKeys.COUNTRY_NAME] ?: LocationData.DEFAULT_DHAKA.countryName,
            timeZoneId = prefs[PreferencesKeys.TIMEZONE_ID] ?: LocationData.DEFAULT_DHAKA.timeZoneId,
            isAutoDetected = prefs[PreferencesKeys.IS_AUTO_LOCATION] ?: true
        )
    }

    suspend fun updateCalculationMethod(method: PrayerCalculationMethod) {
        context.dataStore.edit { it[PreferencesKeys.CALCULATION_METHOD] = method.id }
    }

    suspend fun updateAsrMethod(method: AsrJuristicMethod) {
        context.dataStore.edit { it[PreferencesKeys.ASR_METHOD] = method.id }
    }

    suspend fun updateHighLatitudeAdjustment(adjustment: HighLatitudeAdjustment) {
        context.dataStore.edit { it[PreferencesKeys.HIGH_LATITUDE_ADJUSTMENT] = adjustment.id }
    }

    suspend fun update24HourFormat(is24Hour: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_24_HOUR] = is24Hour }
    }

    suspend fun updateAzanGlobal(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_AZAN_GLOBALLY_ENABLED] = enabled }
    }

    suspend fun updatePrayerAzan(prayerType: PrayerType, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            when (prayerType) {
                PrayerType.FAJR -> prefs[PreferencesKeys.FAJR_AZAN] = enabled
                PrayerType.DHUHR -> prefs[PreferencesKeys.DHUHR_AZAN] = enabled
                PrayerType.ASR -> prefs[PreferencesKeys.ASR_AZAN] = enabled
                PrayerType.MAGHRIB -> prefs[PreferencesKeys.MAGHRIB_AZAN] = enabled
                PrayerType.ISHA -> prefs[PreferencesKeys.ISHA_AZAN] = enabled
                PrayerType.SUNRISE, PrayerType.SUNSET -> { /* No azan for sunrise/sunset */ }
            }
        }
    }

    suspend fun updateAzanSoundType(type: AzanSoundType) {
        context.dataStore.edit { it[PreferencesKeys.AZAN_SOUND_TYPE] = type.id }
    }

    suspend fun updateAzanVolume(volume: Float) {
        context.dataStore.edit { it[PreferencesKeys.AZAN_VOLUME] = volume }
    }

    suspend fun updateVibration(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_VIBRATION] = enabled }
    }

    suspend fun updateLanguage(language: AppLanguage) {
        context.dataStore.edit { it[PreferencesKeys.LANGUAGE] = language.id }
    }

    suspend fun updateThemeMode(themeMode: AppThemeMode) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = themeMode.id }
    }

    suspend fun updateRamadanMode(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_RAMADAN_MODE] = enabled }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_ONBOARDING_COMPLETED] = completed }
    }

    suspend fun updateOffsets(fajr: Int, dhuhr: Int, asr: Int, maghrib: Int, isha: Int) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.FAJR_OFFSET] = fajr
            prefs[PreferencesKeys.DHUHR_OFFSET] = dhuhr
            prefs[PreferencesKeys.ASR_OFFSET] = asr
            prefs[PreferencesKeys.MAGHRIB_OFFSET] = maghrib
            prefs[PreferencesKeys.ISHA_OFFSET] = isha
        }
    }

    suspend fun saveLocation(location: LocationData) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.LATITUDE] = location.latitude
            prefs[PreferencesKeys.LONGITUDE] = location.longitude
            prefs[PreferencesKeys.CITY_NAME] = location.cityName
            prefs[PreferencesKeys.COUNTRY_NAME] = location.countryName
            prefs[PreferencesKeys.TIMEZONE_ID] = location.timeZoneId
            prefs[PreferencesKeys.IS_AUTO_LOCATION] = location.isAutoDetected
        }
    }
}
