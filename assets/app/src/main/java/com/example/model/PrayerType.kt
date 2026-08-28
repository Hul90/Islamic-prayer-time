package com.example.model

enum class PrayerType(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val isPrimaryPrayer: Boolean = true
) {
    FAJR("fajr", "Fajr", "ফজর", true),
    SUNRISE("sunrise", "Sunrise", "সূর্যোদয়", false),
    DHUHR("dhuhr", "Dhuhr", "যোহর", true),
    ASR("asr", "Asr", "আসর", true),
    SUNSET("sunset", "Sunset", "সূর্যাস্ত", false),
    MAGHRIB("maghrib", "Maghrib", "মাগরিব", true),
    ISHA("isha", "Isha", "এশা", true);

    companion object {
        fun fromId(id: String): PrayerType {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: FAJR
        }
    }
}

enum class PrayerStatus {
    PASSED,
    CURRENT,
    NEXT,
    UPCOMING
}
