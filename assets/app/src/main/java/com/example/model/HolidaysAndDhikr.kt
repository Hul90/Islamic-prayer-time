package com.example.model

import java.time.LocalDate

enum class HolidayType {
    GENERAL,       // সাধারণ ছুটি (National Public Holiday)
    EXECUTIVE,     // নির্বাহী আদেশে সরকারি ছুটি (Executive order holiday)
    OPTIONAL       // ঐচ্ছিক ছুটি (Optional holiday)
}

data class BangladeshHoliday(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val date: LocalDate,
    val type: HolidayType,
    val isReligious: Boolean = false,
    val description: String = ""
)

data class IslamicEvent(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val hijriMonth: Int, // 1 to 12
    val hijriDay: Int,
    val descriptionEn: String,
    val descriptionBn: String
)

data class DhikrPreset(
    val id: String,
    val arabicText: String,
    val transliteration: String,
    val translationEn: String,
    val translationBn: String,
    val defaultTarget: Int = 33
) {
    val titleEn: String get() = transliteration
    val titleBn: String get() = translationBn
    val arabic: String get() = arabicText
    val meaningEn: String get() = translationEn
    val meaningBn: String get() = translationBn

    companion object {
        val PRESETS = listOf(
            DhikrPreset(
                "subhanallah",
                "سُبْحَانَ اللَّهِ",
                "SubhanAllah",
                "Glory be to Allah",
                "আল্লাহ পবিত্র ও মহিমান্বিত",
                33
            ),
            DhikrPreset(
                "alhamdulillah",
                "الْحَمْدُ لِلَّهِ",
                "Alhamdulillah",
                "Praise be to Allah",
                "সকল প্রশংসা আল্লাহর জন্য",
                33
            ),
            DhikrPreset(
                "allahuakbar",
                "اللَّهُ أَكْبَرُ",
                "Allahu Akbar",
                "Allah is the Greatest",
                "আল্লাহ সর্বশ্রেষ্ঠ",
                34
            ),
            DhikrPreset(
                "lailahaillallah",
                "لَا إِلَٰهَ إِلَّا ٱللَّٰهُ",
                "La ilaha illallah",
                "There is no deity except Allah",
                "আল্লাহ ছাড়া কোনো সত্য উপাস্য নেই",
                100
            ),
            DhikrPreset(
                "astaghfirullah",
                "أَسْتَغْفِرُ اللَّهَ",
                "Astaghfirullah",
                "I seek forgiveness from Allah",
                "আমি আল্লাহর কাছে ক্ষমা প্রার্থনা করছি",
                100
            ),
            DhikrPreset(
                "salawat",
                "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ",
                "Allahumma Salli Ala Muhammad",
                "O Allah, send blessings upon Muhammad",
                "হে আল্লাহ! মুহাম্মদ (সাঃ)-এর উপর রহমত বর্ষণ করুন",
                100
            ),
            DhikrPreset(
                "lahawla",
                "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
                "La Hawla Wa La Quwwata Illa Billah",
                "There is no power and no strength except with Allah",
                "আল্লাহর সাহায্য ব্যতীত কোনো শক্তি ও সামর্থ্য নেই",
                33
            ),
            DhikrPreset(
                "subhanallah_bihamdihi",
                "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ سُبْحَانَ اللَّهِ الْعَظِيمِ",
                "SubhanAllahi Wa Bihamdihi SubhanAllahil Azeem",
                "Glory be to Allah and His is the praise, Glory be to Allah the Supreme",
                "আল্লাহর প্রশংসার সাথে তাঁর পবিত্রতা ঘোষণা করছি, মহান আল্লাহ অতি পবিত্র",
                100
            )
        )
    }
}
