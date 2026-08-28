package com.example.calculation

import com.example.model.*
import java.time.LocalDate
import java.time.Month
import kotlin.math.floor

object CalendarEngine {

    /**
     * Converts a Gregorian LocalDate to Islamic Hijri date using accurate astronomical approximation.
     */
    fun gregorianToHijri(date: LocalDate, adjustmentDays: Int = 0): IslamicDate {
        val adjustedDate = date.plusDays(adjustmentDays.toLong())
        val day = adjustedDate.dayOfMonth
        val month = adjustedDate.monthValue
        val year = adjustedDate.year

        var m = month
        var y = year
        if (m < 3) {
            y -= 1
            m += 12
        }

        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5

        // Epoch of Islamic Calendar is JD 1948439.5
        val epochIslamicJD = 1948439.5
        val z = jd - epochIslamicJD
        val cyc = floor(z / 10631.0)
        val j = z - 10631.0 * cyc
        val jYear = floor((j - 0.25) / 354.367068)
        val iYear = (30 * cyc + jYear + 1).toInt()

        val dayInYear = j - floor(jYear * 354.367068)
        val iMonth = (floor((dayInYear + 28.5001) / 29.5)).toInt().coerceIn(1, 12)
        val iDay = (dayInYear - floor((iMonth - 1) * 29.5) + 1).toInt().coerceIn(1, 30)

        val monthIndex = iMonth - 1
        val monthEn = IslamicDate.ISLAMIC_MONTHS_EN.getOrElse(monthIndex) { "Ramadan" }
        val monthBn = IslamicDate.ISLAMIC_MONTHS_BN.getOrElse(monthIndex) { "রমজান" }

        return IslamicDate(
            day = iDay,
            month = iMonth,
            year = iYear,
            monthNameEn = monthEn,
            monthNameBn = monthBn
        )
    }

    /**
     * Converts a Gregorian LocalDate to Bangladesh revised Bangla Calendar date.
     * In Bangladesh (Bangla Academy standard):
     * - Pahela Baishakh is April 14.
     * - First 6 months (Baishakh to Ashwin): 31 days each.
     * - Next 6 months (Kartik to Chaitra): 30 days each (Falgun has 31 in leap years).
     * - Bengali Year: Jan 1 to Apr 13 is (Gregorian Year - 594), Apr 14 to Dec 31 is (Gregorian Year - 593).
     */
    fun gregorianToBangla(date: LocalDate): BanglaDate {
        val year = date.year
        val isLeapYear = date.isLeapYear

        // Reference point: April 14 of current Gregorian year is 1st Boishakh
        val boishakh1 = LocalDate.of(year, Month.APRIL, 14)

        val banglaYear: Int
        val dayOffset: Long

        if (date.isBefore(boishakh1)) {
            banglaYear = year - 594
            val prevBoishakh1 = LocalDate.of(year - 1, Month.APRIL, 14)
            dayOffset = java.time.temporal.ChronoUnit.DAYS.between(prevBoishakh1, date)
        } else {
            banglaYear = year - 593
            dayOffset = java.time.temporal.ChronoUnit.DAYS.between(boishakh1, date)
        }

        // Days in Bengali months (standard revised calendar in Bangladesh)
        // 1: Boishakh (31), 2: Jyeshtha (31), 3: Ashhadh (31), 4: Shraban (31), 5: Bhadra (31), 6: Ashwin (31)
        // 7: Kartik (30), 8: Ogrohayon (30), 9: Poush (30), 10: Magh (30), 11: Falgun (30 or 31 in leap year), 12: Choitra (30)
        val monthLengths = intArrayOf(
            31, 31, 31, 31, 31, 31,
            30, 30, 30, 30, if (isLeapYear) 31 else 30, 30
        )

        var remainingDays = dayOffset.toInt()
        var bMonth = 1
        for (i in 0 until 12) {
            val len = monthLengths[i]
            if (remainingDays < len) {
                bMonth = i + 1
                break
            }
            remainingDays -= len
        }
        val bDay = remainingDays + 1

        val monthNameBn = BanglaDate.BANGLA_MONTHS.getOrElse(bMonth - 1) { "বৈশাখ" }
        val seasonIndex = ((bMonth - 1) / 2) % 6
        val seasonBn = BanglaDate.BANGLA_SEASONS.getOrElse(seasonIndex) { "গ্রীষ্ম" }

        return BanglaDate(
            day = bDay,
            month = bMonth,
            year = banglaYear,
            monthNameBn = monthNameBn,
            seasonBn = seasonBn
        )
    }

    /**
     * Comprehensive Bangladesh Government Holidays for any year (Fixed National + Variable Religious approximations).
     */
    fun getBangladeshHolidaysForYear(year: Int): List<BangladeshHoliday> {
        val list = mutableListOf<BangladeshHoliday>()

        // Fixed National / General Holidays
        list.add(BangladeshHoliday("bday_feb21", "Shaheed Day & International Mother Language Day", "শহীদ দিবস ও আন্তর্জাতিক মাতৃভাষা দিবস", LocalDate.of(year, Month.FEBRUARY, 21), HolidayType.GENERAL, false))
        list.add(BangladeshHoliday("bday_mar17", "Sheikh Mujibur Rahman's Birthday / National Children's Day", "জাতির পিতা বঙ্গবন্ধু শেখ মুজিবুর রহমানের জন্মদিবস", LocalDate.of(year, Month.MARCH, 17), HolidayType.GENERAL, false))
        list.add(BangladeshHoliday("bday_mar26", "Independence & National Day", "স্বাধীনতা ও জাতীয় দিবস", LocalDate.of(year, Month.MARCH, 26), HolidayType.GENERAL, false))
        list.add(BangladeshHoliday("bday_apr14", "Bengali New Year (Pohela Boishakh)", "পহেলা বৈশাখ (বাংলা নববর্ষ)", LocalDate.of(year, Month.APRIL, 14), HolidayType.GENERAL, false))
        list.add(BangladeshHoliday("bday_may01", "May Day (International Workers' Day)", "মে দিবস", LocalDate.of(year, Month.MAY, 1), HolidayType.GENERAL, false))
        list.add(BangladeshHoliday("bday_aug15", "National Mourning Day", "জাতীয় শোক দিবস", LocalDate.of(year, Month.AUGUST, 15), HolidayType.EXECUTIVE, false))
        list.add(BangladeshHoliday("bday_dec16", "Victory Day", "বিজয় দিবস", LocalDate.of(year, Month.DECEMBER, 16), HolidayType.GENERAL, false))
        list.add(BangladeshHoliday("bday_dec25", "Christmas Day (Borodin)", "বড়দিন (যিশু খ্রিস্টের জন্মদিন)", LocalDate.of(year, Month.DECEMBER, 25), HolidayType.GENERAL, true))

        // Lunar / Variable Religious Holidays mapped per calendar year
        when (year) {
            2024 -> {
                list.add(BangladeshHoliday("shab_e_barat_2024", "Shab-e-Barat", "পবিত্র শবে বরাত", LocalDate.of(2024, Month.FEBRUARY, 26), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("shab_e_qadr_2024", "Shab-e-Qadr", "পবিত্র শবে কদর", LocalDate.of(2024, Month.APRIL, 7), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("eid_ul_fitr_2024", "Eid-ul-Fitr Holiday", "পবিত্র ঈদুল ফিতর", LocalDate.of(2024, Month.APRIL, 11), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("buddha_purnima_2024", "Buddha Purnima", "বুদ্ধ পূর্ণিমা", LocalDate.of(2024, Month.MAY, 22), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("eid_ul_adha_2024", "Eid-ul-Adha Holiday", "পবিত্র ঈদুল আযহা", LocalDate.of(2024, Month.JUNE, 17), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("ashura_2024", "Holy Ashura", "পবিত্র আশুরা", LocalDate.of(2024, Month.JULY, 17), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("janmashtami_2024", "Janmashtami", "শুভ জন্মাষ্টমী", LocalDate.of(2024, Month.AUGUST, 26), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("eid_e_miladunnabi_2024", "Eid-e-Miladunnabi", "পবিত্র ঈদে মিলাদুন্নবী (সাঃ)", LocalDate.of(2024, Month.SEPTEMBER, 16), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("durga_puja_2024", "Durga Puja (Bijoya Dashami)", "দুর্গাপূজা (বিজয়া দশমী)", LocalDate.of(2024, Month.OCTOBER, 13), HolidayType.GENERAL, true))
            }
            2025 -> {
                list.add(BangladeshHoliday("shab_e_barat_2025", "Shab-e-Barat", "পবিত্র শবে বরাত", LocalDate.of(2025, Month.FEBRUARY, 15), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("shab_e_qadr_2025", "Shab-e-Qadr", "পবিত্র শবে কদর", LocalDate.of(2025, Month.MARCH, 28), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("eid_ul_fitr_2025", "Eid-ul-Fitr Holiday", "পবিত্র ঈদুল ফিতর", LocalDate.of(2025, Month.MARCH, 31), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("buddha_purnima_2025", "Buddha Purnima", "বুদ্ধ পূর্ণিমা", LocalDate.of(2025, Month.MAY, 12), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("eid_ul_adha_2025", "Eid-ul-Adha Holiday", "পবিত্র ঈদুল আযহা", LocalDate.of(2025, Month.JUNE, 7), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("ashura_2025", "Holy Ashura", "পবিত্র আশুরা", LocalDate.of(2025, Month.JULY, 6), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("janmashtami_2025", "Janmashtami", "শুভ জন্মাষ্টমী", LocalDate.of(2025, Month.AUGUST, 16), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("eid_e_miladunnabi_2025", "Eid-e-Miladunnabi", "পবিত্র ঈদে মিলাদুন্নবী (সাঃ)", LocalDate.of(2025, Month.SEPTEMBER, 5), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("durga_puja_2025", "Durga Puja (Bijoya Dashami)", "দুর্গাপূজা (বিজয়া দশমী)", LocalDate.of(2025, Month.OCTOBER, 2), HolidayType.GENERAL, true))
            }
            2026 -> {
                list.add(BangladeshHoliday("shab_e_barat_2026", "Shab-e-Barat", "পবিত্র শবে বরাত", LocalDate.of(2026, Month.FEBRUARY, 4), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("shab_e_qadr_2026", "Shab-e-Qadr", "পবিত্র শবে কদর", LocalDate.of(2026, Month.MARCH, 17), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("eid_ul_fitr_2026", "Eid-ul-Fitr Holiday", "পবিত্র ঈদুল ফিতর", LocalDate.of(2026, Month.MARCH, 21), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("buddha_purnima_2026", "Buddha Purnima", "বুদ্ধ পূর্ণিমা", LocalDate.of(2026, Month.MAY, 1), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("eid_ul_adha_2026", "Eid-ul-Adha Holiday", "পবিত্র ঈদুল আযহা", LocalDate.of(2026, Month.MAY, 27), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("ashura_2026", "Holy Ashura", "পবিত্র আশুরা", LocalDate.of(2026, Month.JUNE, 26), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("janmashtami_2026", "Janmashtami", "শুভ জন্মাষ্টমী", LocalDate.of(2026, Month.SEPTEMBER, 4), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("eid_e_miladunnabi_2026", "Eid-e-Miladunnabi", "পবিত্র ঈদে মিলাদুন্নবী (সাঃ)", LocalDate.of(2026, Month.AUGUST, 26), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("durga_puja_2026", "Durga Puja (Bijoya Dashami)", "দুর্গাপূজা (বিজয়া দশমী)", LocalDate.of(2026, Month.OCTOBER, 20), HolidayType.GENERAL, true))
            }
            2027 -> {
                list.add(BangladeshHoliday("shab_e_barat_2027", "Shab-e-Barat", "পবিত্র শবে বরাত", LocalDate.of(2027, Month.JANUARY, 24), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("shab_e_qadr_2027", "Shab-e-Qadr", "পবিত্র শবে কদর", LocalDate.of(2027, Month.MARCH, 6), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("eid_ul_fitr_2027", "Eid-ul-Fitr Holiday", "পবিত্র ঈদুল ফিতর", LocalDate.of(2027, Month.MARCH, 10), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("eid_ul_adha_2027", "Eid-ul-Adha Holiday", "পবিত্র ঈদুল আযহা", LocalDate.of(2027, Month.MAY, 17), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("ashura_2027", "Holy Ashura", "পবিত্র আশুরা", LocalDate.of(2027, Month.JUNE, 16), HolidayType.EXECUTIVE, true))
                list.add(BangladeshHoliday("eid_e_miladunnabi_2027", "Eid-e-Miladunnabi", "পবিত্র ঈদে মিলাদুন্নবী (সাঃ)", LocalDate.of(2027, Month.AUGUST, 15), HolidayType.GENERAL, true))
                list.add(BangladeshHoliday("durga_puja_2027", "Durga Puja (Bijoya Dashami)", "দুর্গাপূজা (বিজয়া দশমী)", LocalDate.of(2027, Month.OCTOBER, 10), HolidayType.GENERAL, true))
            }
            else -> {
                // Dynamic astronomical fallback for years beyond hardcoded mappings
                val hijri1Ramadan = findGregorianForHijri(year, 9, 1)
                val hijriEidFitr = findGregorianForHijri(year, 10, 1)
                val hijriEidAdha = findGregorianForHijri(year, 12, 10)
                val hijriAshura = findGregorianForHijri(year, 1, 10)

                hijriEidFitr?.let { list.add(BangladeshHoliday("eid_fitr_gen", "Eid-ul-Fitr (Approximate)", "ঈদুল ফিতর (চাঁদ দেখা সাপেক্ষে)", it, HolidayType.GENERAL, true)) }
                hijriEidAdha?.let { list.add(BangladeshHoliday("eid_adha_gen", "Eid-ul-Adha (Approximate)", "ঈদুল আযহা (চাঁদ দেখা সাপেক্ষে)", it, HolidayType.GENERAL, true)) }
                hijriAshura?.let { list.add(BangladeshHoliday("ashura_gen", "Ashura (Approximate)", "আশুরা (চাঁদ দেখা সাপেক্ষে)", it, HolidayType.EXECUTIVE, true)) }
            }
        }

        return list.sortedBy { it.date }
    }

    /**
     * List of key Islamic annual events for religious observance awareness.
     */
    val ISLAMIC_EVENTS = listOf(
        IslamicEvent("event_islamic_new_year", "Islamic New Year", "হিজরি নববর্ষ", 1, 1, "1st Muharram - Start of the new Hijri year", "১লা মুহাররম - নতুন হিজরি সনের সূচনা"),
        IslamicEvent("event_ashura", "Day of Ashura", "পবিত্র আশুরা", 1, 10, "10th Muharram - Fasting on the Day of Ashura", "১০ই মুহাররম - ঐতিহাসিক আশুরা ও রোজার দিন"),
        IslamicEvent("event_miladunnabi", "Eid-e-Miladunnabi (Mawlid)", "ঈদে মিলাদুন্নবী (সাঃ)", 3, 12, "12th Rabi' al-Awwal - Birth of Prophet Muhammad (PBUH)", "১২ই রবিউল আউয়াল - প্রিয় নবীজি (সাঃ)-এর শুভ জন্মদিন"),
        IslamicEvent("event_meraj", "Shab-e-Meraj (Isra & Mi'raj)", "পবিত্র শবে মেরাজ", 7, 27, "27th Rajab - The Night Journey and Heavenly Ascension", "২৭শে রজব - রাসুলুল্লাহ (সাঃ)-এর ঊর্ধ্বাকাশ পরিভ্রমণের পবিত্র রাত"),
        IslamicEvent("event_barat", "Shab-e-Barat (Mid-Sha'ban)", "পবিত্র শবে বরাত", 8, 15, "15th Sha'ban - Night of Record and Forgiveness", "১৫ই শাবান - পুণ্যময় শবে বরাত ও ক্ষমার রজনী"),
        IslamicEvent("event_ramadan_start", "Start of Ramadan", "পবিত্র রমজান শুরু", 9, 1, "1st Ramadan - First day of Holy Fasting month", "১লা রমজান - রহমত, মাগফিরাত ও নাজাতের মাস শুরু"),
        IslamicEvent("event_qadr", "Laylat al-Qadr (Night of Power)", "পবিত্র লাইলাতুল কদর", 9, 27, "27th Ramadan (Odd night) - Night better than a thousand months", "২৭শে রমজান - হাজার মাসের চেয়ে শ্রেষ্ঠ রজনী"),
        IslamicEvent("event_eid_fitr", "Eid-ul-Fitr", "পবিত্র ঈদুল ফিতর", 10, 1, "1st Shawwal - Islamic Festival of Breaking the Fast", "১লা শাওয়াল - মুসলমানদের প্রধান আনন্দোৎসব"),
        IslamicEvent("event_arafah", "Day of Arafah", "পবিত্র আরাফাতের দিন", 12, 9, "9th Dhu al-Hijjah - The climax of Hajj pilgrimage", "৯ই জিলহজ্জ - হজের প্রধান রুকন আরাফাতের ময়দানে অবস্থান"),
        IslamicEvent("event_eid_adha", "Eid-ul-Adha", "পবিত্র ঈদুল আযহা", 12, 10, "10th Dhu al-Hijjah - Festival of Sacrifice", "১০ই জিলহজ্জ - আত্মত্যাগের মহান কুরবানী ঈদ")
    )

    private fun findGregorianForHijri(targetGregYear: Int, targetHijriMonth: Int, targetHijriDay: Int): LocalDate? {
        var testDate = LocalDate.of(targetGregYear, 1, 1)
        val endOfYear = LocalDate.of(targetGregYear, 12, 31)
        while (!testDate.isAfter(endOfYear)) {
            val hijri = gregorianToHijri(testDate)
            if (hijri.month == targetHijriMonth && hijri.day == targetHijriDay) {
                return testDate
            }
            testDate = testDate.plusDays(1)
        }
        return null
    }
}
