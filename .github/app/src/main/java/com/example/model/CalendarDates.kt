package com.example.model

data class IslamicDate(
    val day: Int,
    val month: Int,
    val year: Int,
    val monthNameEn: String,
    val monthNameBn: String
) {
    fun formatDisplay(isBangla: Boolean = false): String {
        return if (isBangla) {
            val dayBn = SinglePrayerTime.toBanglaNumerals(day.toString())
            val yearBn = SinglePrayerTime.toBanglaNumerals(year.toString())
            "$dayBn $monthNameBn $yearBn হিজরি"
        } else {
            "$day $monthNameEn $year AH"
        }
    }

    companion object {
        val ISLAMIC_MONTHS_EN = listOf(
            "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
            "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
        )

        val ISLAMIC_MONTHS_BN = listOf(
            "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
            "জমাদিউল আউয়াল", "জমাদিউস সানি", "রজব", "শাবান",
            "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ্জ"
        )
    }
}

data class BanglaDate(
    val day: Int,
    val month: Int,
    val year: Int,
    val monthNameBn: String,
    val seasonBn: String
) {
    val season: String get() = seasonBn

    fun formatDisplay(isBangla: Boolean = true): String {
        return if (isBangla) {
            val dayBn = SinglePrayerTime.toBanglaNumerals(day.toString())
            val yearBn = SinglePrayerTime.toBanglaNumerals(year.toString())
            "$dayBn $monthNameBn $yearBn বঙ্গাব্দ"
        } else {
            val monthEn = BANGLA_MONTHS_EN.getOrElse(month - 1) { monthNameBn }
            "$day $monthEn $year Bangabda"
        }
    }

    companion object {
        val BANGLA_MONTHS = listOf(
            "বৈশাখ", "জ্যৈষ্ঠ", "আষাঢ়", "শ্রাবণ", "ভাদ্র", "আশ্বিন",
            "কার্তিক", "অগ্রহায়ণ", "পৌষ", "মাঘ", "ফাল্গুন", "চৈত্র"
        )

        val BANGLA_MONTHS_EN = listOf(
            "Baishakh", "Jyeshtha", "Ashhadh", "Shraban", "Bhadra", "Ashwin",
            "Kartik", "Agrahayan", "Poush", "Magh", "Falgun", "Chaitra"
        )

        val BANGLA_SEASONS = listOf(
            "গ্রীষ্ম", "বর্ষা", "শরৎ", "হেমন্ত", "শীত", "বসন্ত"
        )
    }
}
