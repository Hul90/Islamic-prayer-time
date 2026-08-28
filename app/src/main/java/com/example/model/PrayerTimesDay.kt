package com.example.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class SinglePrayerTime(
    val type: PrayerType,
    val time: LocalTime,
    val isAzanEnabled: Boolean = true
) {
    fun formatted12Hour(isBangla: Boolean = false): String {
        val hour24 = time.hour
        val minute = time.minute
        val period = if (hour24 >= 12) {
            if (isBangla) "অপরাহ্ন" else "PM"
        } else {
            if (isBangla) "পূর্বাহ্ন" else "AM"
        }
        val hour12 = when (val h = hour24 % 12) {
            0 -> 12
            else -> h
        }
        val rawTime = String.format("%02d:%02d %s", hour12, minute, period)
        return if (isBangla) toBanglaNumerals(rawTime) else rawTime
    }

    fun formatted24Hour(isBangla: Boolean = false): String {
        val raw = String.format("%02d:%02d", time.hour, time.minute)
        return if (isBangla) toBanglaNumerals(raw) else raw
    }

    companion object {
        fun toBanglaNumerals(input: String): String {
            val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
            val sb = java.lang.StringBuilder()
            for (ch in input) {
                if (ch in '0'..'9') {
                    sb.append(banglaDigits[ch - '0'])
                } else {
                    sb.append(ch)
                }
            }
            return sb.toString()
        }
    }
}

data class PrayerTimesDay(
    val date: LocalDate,
    val fajr: LocalTime,
    val sunrise: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime,
    val midnight: LocalTime,
    val sehriEnd: LocalTime,
    val iftar: LocalTime,
    val sunset: LocalTime = maghrib
) {
    fun getPrayerList(): List<SinglePrayerTime> {
        return listOf(
            SinglePrayerTime(PrayerType.FAJR, fajr),
            SinglePrayerTime(PrayerType.SUNRISE, sunrise, isAzanEnabled = false),
            SinglePrayerTime(PrayerType.DHUHR, dhuhr),
            SinglePrayerTime(PrayerType.ASR, asr),
            SinglePrayerTime(PrayerType.MAGHRIB, maghrib),
            SinglePrayerTime(PrayerType.ISHA, isha)
        )
    }

    fun getFullTimelineList(): List<SinglePrayerTime> {
        return listOf(
            SinglePrayerTime(PrayerType.FAJR, fajr),
            SinglePrayerTime(PrayerType.SUNRISE, sunrise, isAzanEnabled = false),
            SinglePrayerTime(PrayerType.DHUHR, dhuhr),
            SinglePrayerTime(PrayerType.ASR, asr),
            SinglePrayerTime(PrayerType.SUNSET, sunset, isAzanEnabled = false),
            SinglePrayerTime(PrayerType.MAGHRIB, maghrib),
            SinglePrayerTime(PrayerType.ISHA, isha)
        )
    }

    fun getTimeForPrayer(type: PrayerType): LocalTime {
        return when (type) {
            PrayerType.FAJR -> fajr
            PrayerType.SUNRISE -> sunrise
            PrayerType.DHUHR -> dhuhr
            PrayerType.ASR -> asr
            PrayerType.SUNSET -> sunset
            PrayerType.MAGHRIB -> maghrib
            PrayerType.ISHA -> isha
        }
    }

    fun getCurrentAndNextPrayer(currentTime: LocalDateTime): Pair<SinglePrayerTime?, SinglePrayerTime> {
        val nowTime = currentTime.toLocalTime()
        val list = getPrayerList()

        if (nowTime.isBefore(fajr)) {
            return Pair(null, list.first { it.type == PrayerType.FAJR })
        } else if (nowTime.isBefore(sunrise)) {
            return Pair(list.first { it.type == PrayerType.FAJR }, list.first { it.type == PrayerType.SUNRISE })
        } else if (nowTime.isBefore(dhuhr)) {
            return Pair(list.first { it.type == PrayerType.SUNRISE }, list.first { it.type == PrayerType.DHUHR })
        } else if (nowTime.isBefore(asr)) {
            return Pair(list.first { it.type == PrayerType.DHUHR }, list.first { it.type == PrayerType.ASR })
        } else if (nowTime.isBefore(maghrib)) {
            return Pair(list.first { it.type == PrayerType.ASR }, list.first { it.type == PrayerType.MAGHRIB })
        } else if (nowTime.isBefore(isha)) {
            return Pair(list.first { it.type == PrayerType.MAGHRIB }, list.first { it.type == PrayerType.ISHA })
        } else {
            return Pair(list.first { it.type == PrayerType.ISHA }, SinglePrayerTime(PrayerType.FAJR, fajr))
        }
    }
}
