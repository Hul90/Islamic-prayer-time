package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.IslamicPrayerApplication
import com.example.calculation.CalendarEngine
import com.example.calculation.QiblaCalculator
import com.example.data.db.PrayerRecordEntity
import com.example.data.db.TasbihRecordEntity
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

data class QiblaUiState(
    val qiblaBearing: Double = 0.0,
    val currentHeading: Float = 0f,
    val distanceKm: Double = 0.0,
    val isAligned: Boolean = false,
    val isSensorAvailable: Boolean = true
)

data class TasbihUiState(
    val currentPreset: DhikrPreset = DhikrPreset.PRESETS.first(),
    val count: Int = 0,
    val target: Int = 33,
    val completedRounds: Int = 0,
    val totalCount: Long = 0L,
    val presets: List<DhikrPreset> = DhikrPreset.PRESETS
)

data class ZakatCalculatorState(
    val cashInHand: Double = 0.0,
    val bankSavings: Double = 0.0,
    val goldGrams: Double = 0.0,
    val goldPricePerGram: Double = 10500.0, // Standard approximate BDT per gram (22k gold in Bangladesh)
    val silverGrams: Double = 0.0,
    val silverPricePerGram: Double = 180.0, // Standard approximate BDT per gram
    val businessGoods: Double = 0.0,
    val investments: Double = 0.0,
    val debtsOwed: Double = 0.0,
    val expensesDue: Double = 0.0,
    val nisabThresholdBDT: Double = 87.48 * 10500.0 * 0.85 // ~7.5 tola gold threshold
) {
    val totalAssets: Double
        get() = cashInHand + bankSavings + (goldGrams * goldPricePerGram) + (silverGrams * silverPricePerGram) + businessGoods + investments

    val totalLiabilities: Double
        get() = debtsOwed + expensesDue

    val netZakatEligibleWealth: Double
        get() = (totalAssets - totalLiabilities).coerceAtLeast(0.0)

    val isZakatPayable: Boolean
        get() = netZakatEligibleWealth >= nisabThresholdBDT

    val zakatPayableAmount: Double
        get() = if (isZakatPayable) netZakatEligibleWealth * 0.025 else 0.0
}

data class DateConverterState(
    val inputGregorian: LocalDate = LocalDate.now(),
    val convertedHijri: IslamicDate = CalendarEngine.gregorianToHijri(LocalDate.now()),
    val convertedBangla: BanglaDate = CalendarEngine.gregorianToBangla(LocalDate.now())
)

class SultanToolsViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val app = application as IslamicPrayerApplication
    private val prayerRepo = app.prayerRepository
    private val settingsRepo = app.settingsRepository

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private var hasAccelerometer = false
    private var hasMagnetometer = false

    private val _qiblaState = MutableStateFlow(QiblaUiState())
    val qiblaState: StateFlow<QiblaUiState> = _qiblaState.asStateFlow()

    private val _tasbihState = MutableStateFlow(TasbihUiState())
    val tasbihState: StateFlow<TasbihUiState> = _tasbihState.asStateFlow()

    private val _zakatState = MutableStateFlow(ZakatCalculatorState())
    val zakatState: StateFlow<ZakatCalculatorState> = _zakatState.asStateFlow()

    private val _dateConverterState = MutableStateFlow(DateConverterState())
    val dateConverterState: StateFlow<DateConverterState> = _dateConverterState.asStateFlow()

    private val _todayPrayerRecords = MutableStateFlow<Map<PrayerType, String>>(emptyMap())
    val todayPrayerRecords: StateFlow<Map<PrayerType, String>> = _todayPrayerRecords.asStateFlow()

    val totalPrayedCount: StateFlow<Int> = prayerRepo.getTotalPrayedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Init Qibla from saved location
        viewModelScope.launch {
            settingsRepo.savedLocationFlow.collect { loc ->
                val bearing = QiblaCalculator.calculateQiblaBearing(loc.latitude, loc.longitude)
                val dist = QiblaCalculator.calculateDistanceToMakkahKm(loc.latitude, loc.longitude)
                _qiblaState.update { it.copy(qiblaBearing = bearing, distanceKm = dist) }
            }
        }

        // Init today's prayer tracker records
        loadTodayPrayerRecords()

        // Init first tasbih preset
        loadTasbihForPreset(DhikrPreset.PRESETS.first())
    }

    // --- Qibla Sensor Lifecycle ---
    fun startCompass() {
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val rot = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (rot != null) {
            sensorManager.registerListener(this, rot, SensorManager.SENSOR_DELAY_UI)
            _qiblaState.update { it.copy(isSensorAvailable = true) }
        } else if (accel != null && mag != null) {
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(this, mag, SensorManager.SENSOR_DELAY_UI)
            _qiblaState.update { it.copy(isSensorAvailable = true) }
        } else {
            _qiblaState.update { it.copy(isSensorAvailable = false) }
        }
    }

    fun stopCompass() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            var azimuthDeg = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            azimuthDeg = (azimuthDeg + 360f) % 360f
            updateHeading(azimuthDeg)
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
            hasAccelerometer = true
            computeOrientation()
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            hasMagnetometer = true
            computeOrientation()
        }
    }

    private fun computeOrientation() {
        if (hasAccelerometer && hasMagnetometer) {
            val success = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                var azimuthDeg = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                azimuthDeg = (azimuthDeg + 360f) % 360f
                updateHeading(azimuthDeg)
            }
        }
    }

    private fun updateHeading(heading: Float) {
        val qiblaBearing = _qiblaState.value.qiblaBearing
        val diff = abs(heading - qiblaBearing)
        val isAligned = diff < 3.0 || diff > 357.0

        if (isAligned && !_qiblaState.value.isAligned) {
            triggerVibration(50)
        }

        _qiblaState.update {
            it.copy(currentHeading = heading, isAligned = isAligned)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // --- Tasbih Operations ---
    fun selectTasbihPreset(preset: DhikrPreset) {
        loadTasbihForPreset(preset)
    }

    private fun loadTasbihForPreset(preset: DhikrPreset) {
        viewModelScope.launch {
            val record = prayerRepo.getTasbihRecord(preset.id).first()
            if (record != null) {
                _tasbihState.value = TasbihUiState(
                    currentPreset = preset,
                    count = record.count,
                    target = record.target,
                    completedRounds = record.completedRounds,
                    totalCount = record.totalCount
                )
            } else {
                _tasbihState.value = TasbihUiState(
                    currentPreset = preset,
                    count = 0,
                    target = preset.defaultTarget,
                    completedRounds = 0,
                    totalCount = 0L
                )
            }
        }
    }

    fun incrementTasbih() {
        val state = _tasbihState.value
        val newCount = state.count + 1
        var newRounds = state.completedRounds
        val newTotal = state.totalCount + 1

        triggerVibration(30)

        if (newCount >= state.target) {
            newRounds += 1
            triggerVibration(120)
            _tasbihState.value = state.copy(
                count = 0,
                completedRounds = newRounds,
                totalCount = newTotal
            )
        } else {
            _tasbihState.value = state.copy(
                count = newCount,
                totalCount = newTotal
            )
        }

        // Persist to DB
        viewModelScope.launch {
            prayerRepo.saveTasbihRecord(
                TasbihRecordEntity(
                    presetId = state.currentPreset.id,
                    count = _tasbihState.value.count,
                    target = state.target,
                    totalCount = _tasbihState.value.totalCount,
                    completedRounds = _tasbihState.value.completedRounds
                )
            )
        }
    }

    fun resetTasbih() {
        val state = _tasbihState.value
        _tasbihState.value = state.copy(count = 0)
        viewModelScope.launch {
            prayerRepo.saveTasbihRecord(
                TasbihRecordEntity(
                    presetId = state.currentPreset.id,
                    count = 0,
                    target = state.target,
                    totalCount = state.totalCount,
                    completedRounds = state.completedRounds
                )
            )
        }
    }

    fun setTasbihTarget(newTarget: Int) {
        val state = _tasbihState.value
        _tasbihState.value = state.copy(target = newTarget)
        viewModelScope.launch {
            prayerRepo.saveTasbihRecord(
                TasbihRecordEntity(
                    presetId = state.currentPreset.id,
                    count = state.count,
                    target = newTarget,
                    totalCount = state.totalCount,
                    completedRounds = state.completedRounds
                )
            )
        }
    }

    // --- Prayer Tracker Operations ---
    private fun loadTodayPrayerRecords() {
        val today = LocalDate.now()
        viewModelScope.launch {
            prayerRepo.getPrayerRecordsForDate(today).collect { list ->
                val map = mutableMapOf<PrayerType, String>()
                for (rec in list) {
                    val pType = PrayerType.fromId(rec.prayerName)
                    map[pType] = rec.status
                }
                _todayPrayerRecords.value = map
            }
        }
    }

    fun setPrayerTrackingStatus(prayerType: PrayerType, status: String) {
        viewModelScope.launch {
            prayerRepo.setPrayerStatus(LocalDate.now(), prayerType, status)
            triggerVibration(40)
        }
    }

    // --- Zakat Calculator Operations ---
    fun updateZakatField(
        cash: Double = _zakatState.value.cashInHand,
        savings: Double = _zakatState.value.bankSavings,
        gold: Double = _zakatState.value.goldGrams,
        silver: Double = _zakatState.value.silverGrams,
        business: Double = _zakatState.value.businessGoods,
        investments: Double = _zakatState.value.investments,
        debts: Double = _zakatState.value.debtsOwed,
        expenses: Double = _zakatState.value.expensesDue
    ) {
        _zakatState.value = _zakatState.value.copy(
            cashInHand = cash,
            bankSavings = savings,
            goldGrams = gold,
            silverGrams = silver,
            businessGoods = business,
            investments = investments,
            debtsOwed = debts,
            expensesDue = expenses
        )
    }

    // --- Date Converter Operations ---
    fun setConvertDate(date: LocalDate) {
        val hijri = CalendarEngine.gregorianToHijri(date)
        val bangla = CalendarEngine.gregorianToBangla(date)
        _dateConverterState.value = DateConverterState(
            inputGregorian = date,
            convertedHijri = hijri,
            convertedBangla = bangla
        )
    }

    private fun triggerVibration(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }
}
