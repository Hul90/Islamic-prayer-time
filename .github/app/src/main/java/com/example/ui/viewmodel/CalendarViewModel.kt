package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.IslamicPrayerApplication
import com.example.calculation.CalendarEngine
import com.example.calculation.PrayerTimeEngine
import com.example.model.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.YearMonth

data class CalendarDayItem(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val hijriDate: IslamicDate,
    val banglaDate: BanglaDate,
    val holiday: BangladeshHoliday?,
    val islamicEvent: IslamicEvent?
)

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val todayDate: LocalDate = LocalDate.now(),
    val todayHijri: IslamicDate = CalendarEngine.gregorianToHijri(LocalDate.now()),
    val todayBangla: BanglaDate = CalendarEngine.gregorianToBangla(LocalDate.now()),
    val daysInGrid: List<CalendarDayItem> = emptyList(),
    val selectedDayHijri: IslamicDate = CalendarEngine.gregorianToHijri(LocalDate.now()),
    val selectedDayBangla: BanglaDate = CalendarEngine.gregorianToBangla(LocalDate.now()),
    val selectedDayHoliday: BangladeshHoliday? = null,
    val selectedDayIslamicEvent: IslamicEvent? = null,
    val selectedDayPrayerTimes: PrayerTimesDay? = null,
    val monthHolidays: List<BangladeshHoliday> = emptyList(),
    val monthIslamicEvents: List<IslamicEvent> = emptyList()
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as IslamicPrayerApplication
    private val settingsRepo = app.settingsRepository

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<CalendarUiState> = combine(
        _currentMonth,
        _selectedDate,
        settingsRepo.savedLocationFlow,
        settingsRepo.settingsFlow
    ) { month, selected, location, settings ->

        val days = generateDaysForMonth(month, selected)
        val holidaysYear = CalendarEngine.getBangladeshHolidaysForYear(month.year)
        val holidaysMonth = holidaysYear.filter { it.date.year == month.year && it.date.monthValue == month.monthValue }

        val today = LocalDate.now()
        val hijriSelected = CalendarEngine.gregorianToHijri(selected)
        val banglaSelected = CalendarEngine.gregorianToBangla(selected)
        val hijriToday = CalendarEngine.gregorianToHijri(today)
        val banglaToday = CalendarEngine.gregorianToBangla(today)
        val holidaySelected = holidaysYear.firstOrNull { it.date.isEqual(selected) }
        val eventSelected = CalendarEngine.ISLAMIC_EVENTS.firstOrNull {
            it.hijriMonth == hijriSelected.month && it.hijriDay == hijriSelected.day
        }
        val prayerTimes = PrayerTimeEngine.calculatePrayerTimes(selected, location, settings)

        CalendarUiState(
            currentMonth = month,
            selectedDate = selected,
            todayDate = today,
            todayHijri = hijriToday,
            todayBangla = banglaToday,
            daysInGrid = days,
            selectedDayHijri = hijriSelected,
            selectedDayBangla = banglaSelected,
            selectedDayHoliday = holidaySelected,
            selectedDayIslamicEvent = eventSelected,
            selectedDayPrayerTimes = prayerTimes,
            monthHolidays = holidaysMonth,
            monthIslamicEvents = CalendarEngine.ISLAMIC_EVENTS
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }

    fun selectToday() {
        _currentMonth.value = YearMonth.now()
        _selectedDate.value = LocalDate.now()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        if (date.monthValue != _currentMonth.value.monthValue || date.year != _currentMonth.value.year) {
            _currentMonth.value = YearMonth.of(date.year, date.monthValue)
        }
    }

    private fun generateDaysForMonth(month: YearMonth, selectedDate: LocalDate): List<CalendarDayItem> {
        val list = mutableListOf<CalendarDayItem>()
        val firstDayOfMonth = month.atDay(1)
        val today = LocalDate.now()
        val holidays = CalendarEngine.getBangladeshHolidaysForYear(month.year)

        // Sunday = 7, Monday = 1 (Java DayOfWeek). Let's start week with Saturday / Sunday.
        // In Bangladesh, the work week starts Sunday, Friday & Saturday are weekends.
        // Let's standard start grid on Sunday (0..6)
        val firstDayOfWeekIndex = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0, Monday = 1, Sat = 6

        // Previous month padding
        val prevMonth = month.minusMonths(1)
        val daysInPrevMonth = prevMonth.lengthOfMonth()
        for (i in (firstDayOfWeekIndex - 1) downTo 0) {
            val d = prevMonth.atDay(daysInPrevMonth - i)
            val h = CalendarEngine.gregorianToHijri(d)
            val b = CalendarEngine.gregorianToBangla(d)
            val hol = holidays.firstOrNull { it.date.isEqual(d) }
            val evt = CalendarEngine.ISLAMIC_EVENTS.firstOrNull { it.hijriMonth == h.month && it.hijriDay == h.day }
            list.add(CalendarDayItem(d, isCurrentMonth = false, isToday = d.isEqual(today), isSelected = d.isEqual(selectedDate), hijriDate = h, banglaDate = b, holiday = hol, islamicEvent = evt))
        }

        // Current month days
        for (dayNum in 1..month.lengthOfMonth()) {
            val d = month.atDay(dayNum)
            val h = CalendarEngine.gregorianToHijri(d)
            val b = CalendarEngine.gregorianToBangla(d)
            val hol = holidays.firstOrNull { it.date.isEqual(d) }
            val evt = CalendarEngine.ISLAMIC_EVENTS.firstOrNull { it.hijriMonth == h.month && it.hijriDay == h.day }
            list.add(CalendarDayItem(d, isCurrentMonth = true, isToday = d.isEqual(today), isSelected = d.isEqual(selectedDate), hijriDate = h, banglaDate = b, holiday = hol, islamicEvent = evt))
        }

        // Next month padding to fill up to complete weeks (multiple of 7)
        var nextMonthDay = 1
        val nextMonth = month.plusMonths(1)
        while (list.size % 7 != 0 || list.size < 35) {
            val d = nextMonth.atDay(nextMonthDay++)
            val h = CalendarEngine.gregorianToHijri(d)
            val b = CalendarEngine.gregorianToBangla(d)
            val hol = holidays.firstOrNull { it.date.isEqual(d) }
            val evt = CalendarEngine.ISLAMIC_EVENTS.firstOrNull { it.hijriMonth == h.month && it.hijriDay == h.day }
            list.add(CalendarDayItem(d, isCurrentMonth = false, isToday = d.isEqual(today), isSelected = d.isEqual(selectedDate), hijriDate = h, banglaDate = b, holiday = hol, islamicEvent = evt))
        }

        return list
    }
}
