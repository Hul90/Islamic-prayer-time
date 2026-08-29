package com.example.ui.navigation

sealed class Screen(val route: String, val titleEn: String, val titleBn: String) {
    data object Home : Screen("home", "Home", "হোম")
    data object Prayer : Screen("prayer", "Prayer", "নামাজ")
    data object Calendar : Screen("calendar", "Calendar", "ক্যালেন্ডার")
    data object SultanTools : Screen("sultan_tools", "Sultan Tools", "সুলতান টুলস")
    data object Settings : Screen("settings", "Settings", "সেটিংস")

    // Sub-screens
    data object Qibla : Screen("tool_qibla", "Qibla Finder", "কিবলা কম্পাস")
    data object Tasbih : Screen("tool_tasbih", "Digital Tasbih", "ডিজিটাল তাসবিহ")
    data object PrayerTracker : Screen("tool_tracker", "Prayer Tracker", "নামাজ ট্র্যাকার")
    data object DateConverter : Screen("tool_converter", "Date Converter", "তারিখ কনভার্টার")
    data object ZakatCalculator : Screen("tool_zakat", "Zakat Calculator", "যাকাত ক্যালকুলেটর")
    data object DhikrTimer : Screen("tool_dhikr_timer", "Dhikr Timer", "জিকির টাইমার")
    data object NearbyMosques : Screen("tool_mosques", "Nearby Mosques", "নিকটস্থ মসজিদ")
    data object RamadanMode : Screen("ramadan_mode", "Ramadan Mode", "রমজান মোড")
    data object About : Screen("about", "About", "অ্যাপ সম্পর্কে")
    data object DistrictPicker : Screen("district_picker", "Select Location", "লোকেশন নির্বাচন")
    data object PrayerCalculationDetails : Screen("prayer_calculation_details", "Calculation Details", "গণনার বিবরণ")
    data object NamazLearning : Screen("tool_namaz_learning", "Namaz Guide", "নামাজ শিক্ষা")
    data object Duas : Screen("tool_duas", "Duas & Zikr", "সকল দোয়া ও যিকির")
    data object OfflineDuas : Screen("tool_offline_duas", "Practical Daily Duas", "প্রতিদিনের ব্যবহারিক দোয়া")
    data object Quran : Screen("tool_quran", "AL QURAN", "আল কুরআন")
}
