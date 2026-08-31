package com.example.model

enum class PrayerCalculationMethod(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val isIshaFixedMinutes: Boolean = false,
    val ishaMinutes: Double = 0.0
) {
    KARACHI(
        "karachi",
        "University of Islamic Sciences, Karachi (Bangladesh Default)",
        "ইসলামিক বিজ্ঞান বিশ্ববিদ্যালয়, করাচি (বাংলাদেশ স্ট্যান্ডার্ড)",
        18.0,
        18.0
    ),
    MUSLIM_WORLD_LEAGUE(
        "mwl",
        "Muslim World League (MWL)",
        "মুসলিম ওয়ার্ল্ড লীগ",
        18.0,
        17.0
    ),
    EGYPTIAN(
        "egyptian",
        "Egyptian General Authority of Survey",
        "মিশরীয় সাধারণ জরিপ কর্তৃপক্ষ",
        19.5,
        17.5
    ),
    UMM_AL_QURA(
        "umm_al_qura",
        "Umm al-Qura University, Makkah",
        "উম্মুল কুরা বিশ্ববিদ্যালয়, মক্কা",
        18.5,
        0.0,
        isIshaFixedMinutes = true,
        ishaMinutes = 90.0
    ),
    ISNA(
        "isna",
        "Islamic Society of North America (ISNA)",
        "উত্তর আমেরিকা ইসলামিক সোসাইটি (ISNA)",
        15.0,
        15.0
    ),
    DUBAI(
        "dubai",
        "Dubai (UAE)",
        "দুবাই (সংযুক্ত আরব আমিরাত)",
        18.2,
        18.2
    ),
    MOONSIGHTING_COMMITTEE(
        "moonsighting",
        "Moonsighting Committee Worldwide",
        "মুনসাইটিং কমিটি বিশ্বব্যাপী",
        18.0,
        18.0
    );

    val nameEn: String get() = titleEn
    val nameBn: String get() = titleBn

    companion object {
        fun fromId(id: String): PrayerCalculationMethod {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: KARACHI
        }
    }
}

enum class AsrJuristicMethod(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val shadowFactor: Double,
    val descriptionEn: String,
    val descriptionBn: String
) {
    SHAFI(
        "shafi",
        "Shafi (1x Shadow - App Default)",
        "শাফেয়ী (১ গুণ ছায়া - অ্যাপ ডিফল্ট)",
        1.0,
        "Shadow factor 1.0. Classical juristic interpretation based on reports of Jibril (AS) leading prayer (Sahih Muslim 612, Jami` at-Tirmidhi 149).",
        "ছায়ার অনুপাত ১.০। জিবরীল (আঃ) এর নামাজের ইমামতি সংক্রান্ত হাদিসের ফিকহি ব্যাখ্যা (সহীহ মুসলিম ৬১২, জামে আত-তিরমিযী ১৪৯)।"
    ),
    MALIKI(
        "maliki",
        "Maliki (1x Shadow)",
        "মালেকী (১ গুণ ছায়া)",
        1.0,
        "Shadow factor 1.0. Classical Maliki juristic calculation method.",
        "ছায়ার অনুপাত ১.০। মালেকী ফিকহ অনুযায়ী আসরের সময় গণনা পদ্ধতি।"
    ),
    HANBALI(
        "hanbali",
        "Hanbali (1x Shadow)",
        "হাম্বলী (১ গুণ ছায়া)",
        1.0,
        "Shadow factor 1.0. Classical Hanbali juristic calculation method.",
        "ছায়ার অনুপাত ১.০। হাম্বলী ফিকহ অনুযায়ী আসরের সময় গণনা পদ্ধতি।"
    ),
    HANAFI(
        "hanafi",
        "Hanafi (2x Shadow)",
        "হানাফী (২ গুণ ছায়া)",
        2.0,
        "Shadow factor 2.0. Classical Hanafi juristic calculation method where Asr begins when shadow reaches twice the object length.",
        "ছায়ার অনুপাত ২.০। হানাফী ফিকহ অনুযায়ী বস্তুর ছায়া দ্বিগুণ হলে আসরের সময় গণনা পদ্ধতি।"
    );

    val nameEn: String get() = titleEn
    val nameBn: String get() = titleBn

    companion object {
        fun fromId(id: String): AsrJuristicMethod {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: SHAFI
        }
    }
}

enum class HighLatitudeAdjustment(
    val id: String,
    val titleEn: String,
    val titleBn: String
) {
    NONE("none", "None", "কোনোটি নয়"),
    MIDNIGHT("midnight", "Middle of the Night", "মধ্যরাত সমন্বয়"),
    ONE_SEVENTH("one_seventh", "One Seventh of Night", "রাতের এক-সপ্তমাংশ"),
    ANGLE_BASED("angle_based", "Angle Based", "কোণ ভিত্তিক");

    val nameEn: String get() = titleEn
    val nameBn: String get() = titleBn

    companion object {
        fun fromId(id: String): HighLatitudeAdjustment {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: NONE
        }
    }
}

enum class AzanSoundType(
    val id: String,
    val titleEn: String,
    val titleBn: String
) {
    FULL_AZAN("full_azan", "Azan MP3", "আজান MP3"),
    SHORT_BEEP("short_beep", "Soft Chime", "মৃদু শব্দ"),
    SILENT("silent", "Silent", "নীরব");

    val nameEn: String get() = titleEn
    val nameBn: String get() = titleBn

    companion object {
        fun fromId(id: String): AzanSoundType {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: FULL_AZAN
        }
    }
}

enum class AppThemeMode(val id: String, val titleEn: String, val titleBn: String) {
    SYSTEM("system", "Follow System", "ডিভাইস সিস্টেম"),
    LIGHT("light", "Light Mode", "লাইট মোড"),
    DARK("dark", "Dark Mode", "ডার্ক মোড");

    val nameEn: String get() = titleEn
    val nameBn: String get() = titleBn

    companion object {
        fun fromId(id: String): AppThemeMode {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: SYSTEM
        }
    }
}

enum class AppLanguage(val id: String, val code: String, val title: String) {
    BANGLA("bn", "bn", "বাংলা (Bangla)"),
    ENGLISH("en", "en", "English");

    val displayName: String get() = title

    companion object {
        fun fromId(id: String): AppLanguage {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: BANGLA
        }
    }
}

enum class SehriAlarmMode(val id: String, val titleEn: String, val titleBn: String) {
    BEFORE_SEHRI_END("before_sehri_end", "Before Sehri Ends", "সেহরি শেষ হওয়ার পূর্বে"),
    CUSTOM_TIME("custom_time", "Custom Exact Time", "নির্দিষ্ট সময়ে");

    val nameEn: String get() = titleEn
    val nameBn: String get() = titleBn

    companion object {
        fun fromId(id: String): SehriAlarmMode {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: BEFORE_SEHRI_END
        }
    }
}

data class PrayerSettings(
    val calculationMethod: PrayerCalculationMethod = PrayerCalculationMethod.KARACHI,
    val asrMethod: AsrJuristicMethod = AsrJuristicMethod.SHAFI,
    val highLatitudeAdjustment: HighLatitudeAdjustment = HighLatitudeAdjustment.NONE,
    val is24HourFormat: Boolean = false,
    val fajrOffsetMinutes: Int = 0,
    val dhuhrOffsetMinutes: Int = 0,
    val asrOffsetMinutes: Int = 0,
    val maghribOffsetMinutes: Int = 0,
    val ishaOffsetMinutes: Int = 0,
    // Azan toggles
    val isAzanGloballyEnabled: Boolean = true,
    val fajrAzan: Boolean = true,
    val dhuhrAzan: Boolean = true,
    val asrAzan: Boolean = true,
    val maghribAzan: Boolean = true,
    val ishaAzan: Boolean = true,
    val azanSoundType: AzanSoundType = AzanSoundType.FULL_AZAN,
    val azanVolume: Float = 0.9f,
    val isVibrationEnabled: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.BANGLA,
    val isRamadanModeActive: Boolean = false,
    val isOnboardingCompleted: Boolean = false,
    // Sehri Wake-up Alarm Settings
    val isSehriAlarmEnabled: Boolean = false,
    val sehriAlarmMode: SehriAlarmMode = SehriAlarmMode.BEFORE_SEHRI_END,
    val sehriAlarmOffsetMinutes: Int = 30, // 30 minutes before Sehri end by default
    val sehriAlarmCustomHour: Int = 4,
    val sehriAlarmCustomMinute: Int = 0,
    val sehriAlarmSoundType: AzanSoundType = AzanSoundType.FULL_AZAN,
    val sehriAlarmVibration: Boolean = true
)
