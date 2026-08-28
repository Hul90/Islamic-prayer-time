package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.IslamicPrayerApplication
import com.example.calculation.CalendarEngine
import com.example.calculation.PrayerTimeEngine
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class MainUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val location: LocationData = LocationData.DEFAULT_DHAKA,
    val settings: PrayerSettings = PrayerSettings(),
    val prayerTimes: PrayerTimesDay? = null,
    val currentPrayer: SinglePrayerTime? = null,
    val nextPrayer: SinglePrayerTime? = null,
    val timeRemainingToNextPrayer: String = "--:--:--",
    val countdownProgress: Float = 0f,
    val hijriDate: IslamicDate = CalendarEngine.gregorianToHijri(LocalDate.now()),
    val banglaDate: BanglaDate = CalendarEngine.gregorianToBangla(LocalDate.now()),
    val sehriRemainingStr: String = "--:--:--",
    val iftarRemainingStr: String = "--:--:--",
    val isLocationLoading: Boolean = false,
    val locationErrorMessage: String? = null,
    val dailyTrackerRecords: Map<String, String> = emptyMap()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as IslamicPrayerApplication
    private val settingsRepo = app.settingsRepository
    private val locationRepo = app.locationRepository
    private val prayerRepo = app.prayerRepository
    private val alarmScheduler = app.alarmScheduler

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _isLocationLoading = MutableStateFlow(false)
    private val _locationErrorMessage = MutableStateFlow<String?>(null)

    val settings: StateFlow<PrayerSettings> = settingsRepo.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PrayerSettings())

    val location: StateFlow<LocationData> = settingsRepo.savedLocationFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocationData.DEFAULT_DHAKA)

    private val _currentTimeTicker = flow {
        while (true) {
            emit(LocalDateTime.now())
            delay(1000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDateTime.now())

    // Combine primary core flows (date, location, settings)
    private val _coreFlow = combine(_selectedDate, location, settings) { date, loc, sett ->
        Triple(date, loc, sett)
    }

    // Combine status flows
    private val _statusFlow = combine(_isLocationLoading, _locationErrorMessage) { isLocLoading, locError ->
        Pair(isLocLoading, locError)
    }

    val uiState: StateFlow<MainUiState> = combine(
        _coreFlow,
        _currentTimeTicker,
        _statusFlow
    ) { (date, loc, sett), now, (isLocLoading, locError) ->

        val times = PrayerTimeEngine.calculatePrayerTimes(date, loc, sett)
        val hijri = CalendarEngine.gregorianToHijri(date)
        val bangla = CalendarEngine.gregorianToBangla(date)

        val isToday = date.isEqual(now.toLocalDate())
        val pair = if (isToday) {
            times.getCurrentAndNextPrayer(now)
        } else {
            Pair(null, times.getPrayerList().first { it.type == PrayerType.FAJR })
        }

        val currP = pair.first
        val nextP = pair.second

        // Calculate exact remaining time string
        val remainingStr: String
        val progress: Float
        if (isToday) {
            val nextDateTime = LocalDateTime.of(date, nextP.time)
            val effectiveNextDateTime = if (nextDateTime.isBefore(now)) {
                // Next day's Fajr
                LocalDateTime.of(date.plusDays(1), nextP.time)
            } else {
                nextDateTime
            }
            val dur = Duration.between(now, effectiveNextDateTime)
            val hours = dur.toHours()
            val mins = (dur.toMinutes() % 60)
            val secs = (dur.seconds % 60)

            val raw = String.format("%02dh %02dm %02ds", hours, mins, secs)
            remainingStr = if (sett.language == AppLanguage.BANGLA) {
                SinglePrayerTime.toBanglaNumerals(raw)
            } else {
                raw
            }

            // Estimate total duration between prayer interval for progress
            val totalSeconds = 3600L * 4 // roughly 4 hours interval
            val remainingSecs = dur.seconds.coerceIn(0, totalSeconds)
            progress = (1.0f - (remainingSecs.toFloat() / totalSeconds.toFloat())).coerceIn(0f, 1f)
        } else {
            remainingStr = if (sett.language == AppLanguage.BANGLA) "অন্য দিন নির্বাচিত" else "Viewing selected date"
            progress = 0f
        }

        // Sehri / Iftar Countdown
        val sehriRemaining = calculateCountDown(now, date, times.sehriEnd, sett.language == AppLanguage.BANGLA)
        val iftarRemaining = calculateCountDown(now, date, times.iftar, sett.language == AppLanguage.BANGLA)

        MainUiState(
            selectedDate = date,
            location = loc,
            settings = sett,
            prayerTimes = times,
            currentPrayer = currP,
            nextPrayer = nextP,
            timeRemainingToNextPrayer = remainingStr,
            countdownProgress = progress,
            hijriDate = hijri,
            banglaDate = bangla,
            sehriRemainingStr = sehriRemaining,
            iftarRemainingStr = iftarRemaining,
            isLocationLoading = isLocLoading,
            locationErrorMessage = locError
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainUiState())

    init {
        // Observe location and settings changes to reschedule prayer alarms automatically
        viewModelScope.launch {
            combine(location, settings) { loc, sett ->
                Pair(loc, sett)
            }.collect { (loc, sett) ->
                alarmScheduler.scheduleAlarmsForTodayAndTomorrow(loc, sett)
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun selectPreviousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    fun selectNextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

    fun selectToday() {
        _selectedDate.value = LocalDate.now()
    }

    fun requestAutoLocation() {
        viewModelScope.launch {
            _isLocationLoading.value = true
            _locationErrorMessage.value = null
            val detected = locationRepo.getCurrentLocation()
            _isLocationLoading.value = false
            if (detected != null) {
                settingsRepo.saveLocation(detected)
            } else {
                _locationErrorMessage.value = "Unable to fetch GPS. You can choose your district manually."
            }
        }
    }

    fun setManualLocation(locationData: LocationData) {
        viewModelScope.launch {
            _locationErrorMessage.value = null
            settingsRepo.saveLocation(locationData)
        }
    }

    fun togglePrayerAzan(prayerType: PrayerType, currentVal: Boolean) {
        viewModelScope.launch {
            settingsRepo.updatePrayerAzan(prayerType, !currentVal)
        }
    }

    fun toggleGlobalAzan(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateAzanGlobal(enabled)
        }
    }

    fun toggleRamadanMode() {
        viewModelScope.launch {
            val current = settings.value.isRamadanModeActive
            settingsRepo.updateRamadanMode(!current)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepo.updateLanguage(language)
        }
    }

    fun setThemeMode(themeMode: AppThemeMode) {
        viewModelScope.launch {
            settingsRepo.updateThemeMode(themeMode)
        }
    }

    fun setCalculationMethod(method: PrayerCalculationMethod) {
        viewModelScope.launch {
            settingsRepo.updateCalculationMethod(method)
        }
    }

    fun setAsrMethod(method: AsrJuristicMethod) {
        viewModelScope.launch {
            settingsRepo.updateAsrMethod(method)
        }
    }

    fun setAzanSoundType(type: AzanSoundType) {
        viewModelScope.launch {
            settingsRepo.updateAzanSoundType(type)
        }
    }

    fun setAzanVolume(volume: Float) {
        viewModelScope.launch {
            settingsRepo.updateAzanVolume(volume)
        }
    }

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateVibration(enabled)
        }
    }

    fun set24Hour(is24Hour: Boolean) {
        viewModelScope.launch {
            settingsRepo.update24HourFormat(is24Hour)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsRepo.setOnboardingCompleted(true)
        }
    }

    private fun calculateCountDown(now: LocalDateTime, date: LocalDate, targetTime: LocalTime, isBangla: Boolean): String {
        val targetDateTime = LocalDateTime.of(date, targetTime)
        if (targetDateTime.isBefore(now)) {
            return if (isBangla) "অতিক্রান্ত" else "Passed"
        }
        val dur = Duration.between(now, targetDateTime)
        val h = dur.toHours()
        val m = (dur.toMinutes() % 60)
        val s = (dur.seconds % 60)
        val raw = String.format("%02d:%02d:%02d", h, m, s)
        return if (isBangla) SinglePrayerTime.toBanglaNumerals(raw) else raw
    }
}
