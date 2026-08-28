package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculation.CalendarEngine
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CalendarDayItem
import com.example.ui.viewmodel.CalendarUiState
import com.example.ui.viewmodel.CalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    uiState: CalendarUiState,
    isBangla: Boolean,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val month = uiState.currentMonth
    val selectedDate = uiState.selectedDate
    val todayDate = uiState.todayDate

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("calendar_screen"),
        contentPadding = PaddingValues(bottom = 104.dp)
    ) {
        // Top App Bar Title
        item {
            ScreenTitleHeader(isBangla = isBangla)
        }

        // 1. HERO TODAY DATE CARD (Dominant visual focus at top)
        item {
            TodayHeroDateCard(
                todayDate = todayDate,
                todayHijri = uiState.todayHijri,
                todayBangla = uiState.todayBangla,
                isBangla = isBangla,
                onSelectToday = { viewModel.selectToday() }
            )
        }

        // 2. THREE-CALENDAR IDENTITY LEGEND
        item {
            ThreeCalendarLegend(isBangla = isBangla)
        }

        // 3. MONTH NAVIGATION HEADER
        item {
            MonthNavigationHeader(
                month = month,
                selectedDate = selectedDate,
                todayDate = todayDate,
                isBangla = isBangla,
                onPrevMonth = { viewModel.previousMonth() },
                onNextMonth = { viewModel.nextMonth() },
                onTodayClick = { viewModel.selectToday() }
            )
        }

        // 4. DAY OF WEEK HEADER
        item {
            CalendarWeekDaysHeader(isBangla = isBangla)
        }

        // 5. MONTHLY CALENDAR GRID (7 columns)
        item {
            MonthlyCalendarGrid(
                days = uiState.daysInGrid,
                isBangla = isBangla,
                onDateSelected = { viewModel.selectDate(it) }
            )
        }

        // 6. SELECTED DATE DETAILS CARD
        item {
            SelectedDateDetailSection(
                uiState = uiState,
                isBangla = isBangla
            )
        }

        // 7. BANGLADESH GOVERNMENT HOLIDAYS SECTION
        item {
            HolidaysSection(
                month = month,
                holidays = uiState.monthHolidays,
                isBangla = isBangla
            )
        }
    }
}

@Composable
private fun ScreenTitleHeader(isBangla: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = if (isBangla) "ইসলামিক ও জাতীয় ক্যালেন্ডার" else "Islamic & National Calendar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (isBangla) "গ্রেগরিয়ান • বঙ্গাব্দ • হিজরি ত্রি-পঞ্জিকা" else "Gregorian • Bangla • Hijri Tri-Calendar",
                style = MaterialTheme.typography.labelMedium,
                color = IslamicGoldLight
            )
        }
        Surface(
            shape = CircleShape,
            color = IslamicEmeraldPrimary.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, IslamicGoldLight.copy(alpha = 0.4f)),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Calendar",
                    tint = IslamicGoldLight,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * 1. LARGE "TODAY" DATE CARD AT THE TOP
 * Visual hero element where the user can immediately understand TODAY'S Gregorian, Bangla, and Hijri dates within 1-2 seconds.
 */
@Composable
private fun TodayHeroDateCard(
    todayDate: LocalDate,
    todayHijri: IslamicDate,
    todayBangla: BanglaDate,
    isBangla: Boolean,
    onSelectToday: () -> Unit
) {
    val dayNumStr = if (isBangla) SinglePrayerTime.toBanglaNumerals(todayDate.dayOfMonth.toString()) else todayDate.dayOfMonth.toString()
    val monthYearStr = formatMonthYear(todayDate, isBangla)
    val weekdayStr = getWeekdayFullName(todayDate, isBangla)
    val banglaFullStr = todayBangla.formatDisplay(isBangla)
    val hijriFullStr = todayHijri.formatDisplay(isBangla)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
            .clickable { onSelectToday() }
            .testTag("card_today_hero"),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(IslamicGoldLight, IslamicGoldDark))),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            IslamicEmeraldPrimary.copy(alpha = 0.85f),
                            IslamicEmeraldDark.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header badge row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IslamicGold.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, IslamicGoldLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌟 ",
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (isBangla) "আজকের দিন (TODAY)" else "TODAY'S DATE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGoldLight
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = weekdayStr,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Gregorian Display: Large typography
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Big day number
                    Text(
                        text = dayNumStr,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Black,
                        color = IslamicGoldLight,
                        lineHeight = 46.sp,
                        modifier = Modifier.padding(end = 14.dp)
                    )

                    Column {
                        Text(
                            text = monthYearStr,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBangla) "ইংরেজি গ্রেগরিয়ান ক্যালেন্ডার" else "Gregorian Calendar",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = IslamicGold.copy(alpha = 0.25f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Secondary Tri-Calendar Displays: Bangla and Hijri dates
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Bangla Date Row
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF22382B),
                        border = BorderStroke(1.dp, IslamicGoldLight.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🌾", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                            Column {
                                Text(
                                    text = if (isBangla) "বাংলা বঙ্গাব্দ" else "Bangla Calendar",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IslamicGoldLight.copy(alpha = 0.8f),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = banglaFullStr,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Hijri Date Row
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1B382D),
                        border = BorderStroke(1.dp, IslamicAccentCyan.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🌙", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                            Column {
                                Text(
                                    text = if (isBangla) "হিজরি সন (আরবি)" else "Hijri / Islamic Calendar",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IslamicAccentCyan,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = hijriFullStr,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 6. THREE-CALENDAR IDENTITY LEGEND
 */
@Composable
private fun ThreeCalendarLegend(isBangla: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        LegendItem(
            icon = "🗓️",
            title = if (isBangla) "ইংরেজি" else "Gregorian",
            subtitle = if (isBangla) "বড় সংখ্যা" else "Top Number",
            color = IslamicGoldLight,
            modifier = Modifier.weight(1f)
        )
        LegendItem(
            icon = "🌾",
            title = if (isBangla) "বাংলা (B)" else "Bangla (B)",
            subtitle = if (isBangla) "বঙ্গাব্দ তারিখ" else "Bangla Date",
            color = IslamicGold,
            modifier = Modifier.weight(1f)
        )
        LegendItem(
            icon = "🌙",
            title = if (isBangla) "হিজরি (H)" else "Hijri (H)",
            subtitle = if (isBangla) "আরবি তারিখ" else "Hijri Date",
            color = IslamicAccentCyan,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LegendItem(
    icon: String,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 11.sp
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 9.sp
            )
        }
    }
}

/**
 * 2. MONTH NAVIGATION HEADER
 */
@Composable
private fun MonthNavigationHeader(
    month: YearMonth,
    selectedDate: LocalDate,
    todayDate: LocalDate,
    isBangla: Boolean,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClick: () -> Unit
) {
    val monthTitle = formatYearMonthTitle(month, isBangla)
    val hijriApprox = CalendarEngine.gregorianToHijri(month.atDay(15))
    val banglaApprox = CalendarEngine.gregorianToBangla(month.atDay(15))

    val secondaryMonthStr = if (isBangla) {
        "${hijriApprox.monthNameBn} ${SinglePrayerTime.toBanglaNumerals(hijriApprox.year.toString())} • ${banglaApprox.monthNameBn} ${SinglePrayerTime.toBanglaNumerals(banglaApprox.year.toString())}"
    } else {
        "${hijriApprox.monthNameEn} ${hijriApprox.year} AH • ${BanglaDate.BANGLA_MONTHS_EN.getOrElse(banglaApprox.month - 1) { banglaApprox.monthNameBn }} ${banglaApprox.year}"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrevMonth,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("btn_cal_prev")
            ) {
                Surface(
                    shape = CircleShape,
                    color = IslamicEmeraldPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Month",
                            tint = IslamicGoldLight
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = monthTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = secondaryMonthStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = IslamicGoldLight,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                FilledTonalButton(
                    onClick = onTodayClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier
                        .height(26.dp)
                        .testTag("btn_cal_today"),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = IslamicEmeraldPrimary.copy(alpha = 0.2f),
                        contentColor = IslamicGoldLight
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBangla) "আজকের দিনে যান" else "Jump to Today",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("btn_cal_next")
            ) {
                Surface(
                    shape = CircleShape,
                    color = IslamicEmeraldPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Month",
                            tint = IslamicGoldLight
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3. DAY OF WEEK HEADER
 */
@Composable
private fun CalendarWeekDaysHeader(isBangla: Boolean) {
    val daysEn = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val daysBn = listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহঃ", "শুক্র", "শনি")
    val days = if (isBangla) daysBn else daysEn

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            days.forEachIndexed { index, day ->
                val isWeekend = (index == 5 || index == 6) // Friday (5) & Saturday (6)
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isWeekend) Color(0xFFFF5252) else IslamicGoldLight,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 3 & 4. MONTHLY CALENDAR GRID WITH PROMINENT TODAY HIGHLIGHT
 */
@Composable
private fun MonthlyCalendarGrid(
    days: List<CalendarDayItem>,
    isBangla: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            for (row in days.chunked(7)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (item in row) {
                        CalendarDayCell(
                            item = item,
                            isBangla = isBangla,
                            onClick = { onDateSelected(item.date) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 4. INDIVIDUAL DATE CELL WITH STRONG HIERARCHY AND TODAY CARD HIGHLIGHT
 */
@Composable
private fun CalendarDayCell(
    item: CalendarDayItem,
    isBangla: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHoliday = item.holiday != null
    val isIslamicEvent = item.islamicEvent != null
    val isFridayOrSaturday = item.date.dayOfWeek == DayOfWeek.FRIDAY || item.date.dayOfWeek == DayOfWeek.SATURDAY

    // Cell Background styling
    val cellBackgroundBrush: Brush? = when {
        item.isSelected && item.isToday -> Brush.verticalGradient(listOf(IslamicEmeraldPrimary, IslamicEmeraldDark))
        item.isToday -> Brush.verticalGradient(listOf(Color(0xFF13422A), Color(0xFF0D2D1C)))
        item.isSelected -> Brush.verticalGradient(listOf(IslamicGoldDark.copy(alpha = 0.85f), Color(0xFF5A410D)))
        else -> null
    }

    val cellBorder = when {
        item.isToday -> BorderStroke(1.5.dp, IslamicGoldLight)
        item.isSelected -> BorderStroke(1.5.dp, IslamicGold)
        isHoliday && item.isCurrentMonth -> BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f))
        else -> null
    }

    val gregorianTextColor = when {
        item.isToday -> IslamicGoldLight
        item.isSelected -> Color.White
        !item.isCurrentMonth -> IslamicMutedText.copy(alpha = 0.35f)
        isHoliday -> Color(0xFFFF5252)
        isFridayOrSaturday -> Color(0xFFFF8A80)
        else -> MaterialTheme.colorScheme.onSurface
    }

    val banglaDayStr = if (isBangla) SinglePrayerTime.toBanglaNumerals(item.banglaDate.day.toString()) else item.banglaDate.day.toString()
    val hijriDayStr = if (isBangla) SinglePrayerTime.toBanglaNumerals(item.hijriDate.day.toString()) else item.hijriDate.day.toString()
    val gregDayStr = if (isBangla) SinglePrayerTime.toBanglaNumerals(item.date.dayOfMonth.toString()) else item.date.dayOfMonth.toString()

    val labelPrefixB = if (isBangla) "বা " else "B "
    val labelPrefixH = if (isBangla) "হি " else "H "

    Box(
        modifier = modifier
            .aspectRatio(0.82f)
            .padding(2.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (cellBackgroundBrush != null) {
                    Modifier.background(cellBackgroundBrush)
                } else {
                    Modifier.background(Color.Transparent)
                }
            )
            .then(
                if (cellBorder != null) {
                    Modifier.border(cellBorder, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            )
            .clickable { onClick() }
            .testTag("day_cell_${item.date}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 3.dp, horizontal = 2.dp)
        ) {
            // Top Badge for Today if it's today
            if (item.isToday) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = IslamicGoldLight,
                    modifier = Modifier.padding(bottom = 1.dp)
                ) {
                    Text(
                        text = if (isBangla) "আজ" else "TODAY",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 0.5.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(2.dp))
            }

            // TOP: Large & High Contrast Gregorian date number
            Text(
                text = gregDayStr,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (item.isToday || item.isSelected) FontWeight.Black else FontWeight.Bold,
                fontSize = 15.sp,
                color = gregorianTextColor
            )

            // SUB: Bangla and Hijri Sub-numbers clearly distinguished
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Bangla Day
                Text(
                    text = "$labelPrefixB$banglaDayStr",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.isToday || item.isSelected) IslamicGoldLight else if (item.isCurrentMonth) IslamicGold.copy(alpha = 0.9f) else IslamicMutedText.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.width(2.dp))
                // Hijri Day
                Text(
                    text = "$labelPrefixH$hijriDayStr",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.isToday || item.isSelected) IslamicAccentCyan else if (item.isCurrentMonth) IslamicAccentCyan.copy(alpha = 0.9f) else IslamicMutedText.copy(alpha = 0.3f)
                )
            }

            // Indicator Dots for Holiday / Islamic Events
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(5.dp)
            ) {
                if (isHoliday) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF5252))
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
                if (isIslamicEvent) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(IslamicGoldLight)
                    )
                }
            }
        }
    }
}

/**
 * 5. SELECTED DATE DETAILS CARD (WITH THREE-CALENDAR CONVERSION + PRAYER TIMES)
 */
@Composable
private fun SelectedDateDetailSection(
    uiState: CalendarUiState,
    isBangla: Boolean
) {
    val date = uiState.selectedDate
    val hijri = uiState.selectedDayHijri
    val bangla = uiState.selectedDayBangla
    val holiday = uiState.selectedDayHoliday
    val islamicEvent = uiState.selectedDayIslamicEvent
    val times = uiState.selectedDayPrayerTimes

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("card_selected_date_details"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, IslamicEmeraldPrimary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Selected Date Badge + Full Gregorian Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBangla) "নির্বাচিত তারিখের বিস্তারিত" else "Selected Date Details",
                        style = MaterialTheme.typography.labelSmall,
                        color = IslamicGoldLight,
                        fontWeight = FontWeight.Bold
                    )
                    val fullGregStr = formatGregorianFull(date, isBangla)
                    val weekday = getWeekdayFullName(date, isBangla)
                    Text(
                        text = "$fullGregStr ($weekday)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IslamicEmeraldPrimary.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, IslamicEmeraldPrimary)
                ) {
                    Text(
                        text = if (date.isEqual(uiState.todayDate)) {
                            if (isBangla) "আজ" else "Today"
                        } else {
                            if (isBangla) "তারিখ" else "Date"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Three-Calendar Conversion Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bangla Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF203326),
                    border = BorderStroke(1.dp, IslamicGoldLight.copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌾", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "বাংলা বঙ্গাব্দ" else "Bangla",
                                style = MaterialTheme.typography.labelSmall,
                                color = IslamicGoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bangla.formatDisplay(isBangla),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Hijri Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF183329),
                    border = BorderStroke(1.dp, IslamicAccentCyan.copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌙", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "হিজরি সন" else "Hijri",
                                style = MaterialTheme.typography.labelSmall,
                                color = IslamicAccentCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = hijri.formatDisplay(isBangla),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Holiday Alert Banner
            holiday?.let { hol ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF3B1E22),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Celebration,
                            contentDescription = "Holiday",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBangla) hol.nameBn else hol.nameEn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFCDD2)
                            )
                            Text(
                                text = if (hol.type == HolidayType.GENERAL) {
                                    if (isBangla) "বাংলাদেশ জাতীয় সাধারণ ছুটি" else "Bangladesh National Public Holiday"
                                } else {
                                    if (isBangla) "নির্বাহী আদেশে সরকারি ছুটি" else "Executive Order Public Holiday"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFEF9A9A)
                            )
                        }
                    }
                }
            }

            // Islamic Event Banner
            islamicEvent?.let { evt ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF143828),
                    border = BorderStroke(1.dp, IslamicGoldLight.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✨", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBangla) evt.nameBn else evt.nameEn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGoldLight
                            )
                            Text(
                                text = if (isBangla) evt.descriptionBn else evt.descriptionEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Prayer Times for Selected Date
            times?.let { pt ->
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla) "ঐ দিনের নামাজের সময়সূচি" else "Prayer Timings for Date",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                    Text(
                        text = if (isBangla) "আসর: শাফেয়ী/হানাফী সমন্বিত" else "Asr calculation active",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 6 Prayer Times Horizontal Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PrayerTimeChip(
                        name = if (isBangla) "ফজর" else "Fajr",
                        time = pt.fajr.toString(),
                        isBangla = isBangla,
                        modifier = Modifier.weight(1f)
                    )
                    PrayerTimeChip(
                        name = if (isBangla) "সূর্যোদয়" else "Sunrise",
                        time = pt.sunrise.toString(),
                        isBangla = isBangla,
                        modifier = Modifier.weight(1f)
                    )
                    PrayerTimeChip(
                        name = if (isBangla) "যোহর" else "Dhuhr",
                        time = pt.dhuhr.toString(),
                        isBangla = isBangla,
                        modifier = Modifier.weight(1f)
                    )
                    PrayerTimeChip(
                        name = if (isBangla) "আসর" else "Asr",
                        time = pt.asr.toString(),
                        isBangla = isBangla,
                        modifier = Modifier.weight(1f)
                    )
                    PrayerTimeChip(
                        name = if (isBangla) "মাগরিব" else "Maghrib",
                        time = pt.maghrib.toString(),
                        isBangla = isBangla,
                        modifier = Modifier.weight(1f)
                    )
                    PrayerTimeChip(
                        name = if (isBangla) "এশা" else "Isha",
                        time = pt.isha.toString(),
                        isBangla = isBangla,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerTimeChip(
    name: String,
    time: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val displayTime = if (isBangla) SinglePrayerTime.toBanglaNumerals(time) else time
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = IslamicMutedText,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = displayTime,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp
        )
    }
}

/**
 * 7. BANGLADESH GOVERNMENT HOLIDAYS LIST
 */
@Composable
private fun HolidaysSection(
    month: YearMonth,
    holidays: List<BangladeshHoliday>,
    isBangla: Boolean
) {
    val monthTitle = formatYearMonthTitle(month, isBangla)
    Text(
        text = if (isBangla) "চলতি মাসের সরকারি ছুটি ($monthTitle)" else "Government Holidays ($monthTitle)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (holidays.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "এই মাসে কোনো নির্ধারিত সরকারি ছুটি নেই" else "No scheduled government holidays in this month",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IslamicMutedText
                )
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                holidays.forEachIndexed { index, holiday ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBangla) holiday.nameBn else holiday.nameEn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (holiday.type == HolidayType.GENERAL) {
                                    if (isBangla) "সাধারণ ছুটি" else "General Public Holiday"
                                } else {
                                    if (isBangla) "নির্বাহী ছুটি" else "Executive Holiday"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFF5252)
                            )
                        }

                        val dateStr = holiday.date.format(DateTimeFormatter.ofPattern("dd MMMM"))
                        val displayDate = if (isBangla) SinglePrayerTime.toBanglaNumerals(dateStr) else dateStr
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF3B1E22),
                            border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = displayDate,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFCDD2),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (index < holidays.size - 1) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Formatting Helpers
// -------------------------------------------------------------

private fun formatMonthYear(date: LocalDate, isBangla: Boolean): String {
    return if (isBangla) {
        val bnMonths = listOf("জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন", "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর")
        val m = bnMonths[date.monthValue - 1]
        val y = SinglePrayerTime.toBanglaNumerals(date.year.toString())
        "$m $y"
    } else {
        val m = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
        "$m ${date.year}"
    }
}

private fun formatYearMonthTitle(month: YearMonth, isBangla: Boolean): String {
    return if (isBangla) {
        val bnMonths = listOf("জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন", "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর")
        val m = bnMonths[month.monthValue - 1]
        val y = SinglePrayerTime.toBanglaNumerals(month.year.toString())
        "$m $y"
    } else {
        val m = month.month.name.lowercase().replaceFirstChar { it.uppercase() }
        "$m ${month.year}"
    }
}

private fun getWeekdayFullName(date: LocalDate, isBangla: Boolean): String {
    return if (isBangla) {
        when (date.dayOfWeek) {
            DayOfWeek.SUNDAY -> "রবিবার"
            DayOfWeek.MONDAY -> "সোমবার"
            DayOfWeek.TUESDAY -> "মঙ্গলবার"
            DayOfWeek.WEDNESDAY -> "বুধবার"
            DayOfWeek.THURSDAY -> "বৃহস্পতিবার"
            DayOfWeek.FRIDAY -> "শুক্রবার"
            DayOfWeek.SATURDAY -> "শনিবার"
        }
    } else {
        date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

private fun formatGregorianFull(date: LocalDate, isBangla: Boolean): String {
    val day = if (isBangla) SinglePrayerTime.toBanglaNumerals(date.dayOfMonth.toString()) else date.dayOfMonth.toString()
    val monthYear = formatMonthYear(date, isBangla)
    return "$day $monthYear"
}
