package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculation.PrayerTimeEngine
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(
    viewModel: MainViewModel,
    uiState: MainUiState,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBangla = uiState.settings.language == AppLanguage.BANGLA
    val selectedDate = uiState.selectedDate
    val prayerTimes = uiState.prayerTimes
    val isToday = selectedDate.isEqual(LocalDate.now())

    // DatePicker Dialog helper
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                viewModel.setSelectedDate(LocalDate.of(year, month + 1, dayOfMonth))
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Date Navigation Selector
        item {
            DateSelectorBar(
                selectedDate = selectedDate,
                isToday = isToday,
                isBangla = isBangla,
                onPreviousDay = { viewModel.selectPreviousDay() },
                onNextDay = { viewModel.selectNextDay() },
                onSelectToday = { viewModel.selectToday() },
                onOpenDatePicker = { datePickerDialog.show() }
            )
        }

        // 2. Hijri & Bangla Date Sub-header
        item {
            DateInfoBanner(
                hijriDate = uiState.hijriDate,
                banglaDate = uiState.banglaDate,
                isBangla = isBangla
            )
        }

        // 3. Highlighted Next Prayer Hero Card with live countdown when viewing today
        if (isToday) {
            item {
                NextPrayerHeroCard(
                    nextPrayer = uiState.nextPrayer,
                    currentPrayer = uiState.currentPrayer,
                    timeRemaining = uiState.timeRemainingToNextPrayer,
                    progress = uiState.countdownProgress,
                    is24Hour = uiState.settings.is24HourFormat,
                    isBangla = isBangla
                )
            }
        }

        // 4. Daily Prayer Times List (Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha)
        item {
            Text(
                text = if (isBangla) "ওয়াক্ত ও নামাজের সময়সূচি" else "Prayer Times & Solar Schedule",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
        }

        item {
            prayerTimes?.let { pt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        val prayers = pt.getFullTimelineList()

                        prayers.forEachIndexed { index, prayer ->
                            val isNext = isToday && uiState.nextPrayer?.type == prayer.type
                            val isCurrent = isToday && uiState.currentPrayer?.type == prayer.type
                            val isAzanEnabled = when (prayer.type) {
                                PrayerType.FAJR -> uiState.settings.fajrAzan
                                PrayerType.DHUHR -> uiState.settings.dhuhrAzan
                                PrayerType.ASR -> uiState.settings.asrAzan
                                PrayerType.MAGHRIB -> uiState.settings.maghribAzan
                                PrayerType.ISHA -> uiState.settings.ishaAzan
                                PrayerType.SUNRISE, PrayerType.SUNSET -> false
                            }

                            PrayerDetailRow(
                                prayer = prayer,
                                isNext = isNext,
                                isCurrent = isCurrent,
                                isAzanEnabled = isAzanEnabled,
                                is24Hour = uiState.settings.is24HourFormat,
                                isBangla = isBangla,
                                asrMethod = uiState.settings.asrMethod,
                                onToggleAzan = { viewModel.togglePrayerAzan(prayer.type, isAzanEnabled) }
                            )

                            if (index < prayers.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Additional Islamic Special Timings Card
        item {
            Text(
                text = if (isBangla) "অতিরিক্ত গুরুত্বপূর্ণ ওয়াক্ত ও সময়" else "Additional Islamic Timings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
            )
        }

        item {
            prayerTimes?.let { pt ->
                AdditionalTimingsCard(
                    prayerTimes = pt,
                    is24Hour = uiState.settings.is24HourFormat,
                    isBangla = isBangla
                )
            }
        }

        // 6. Calculation Information Card at bottom
        item {
            PrayerCalculationSummaryCard(
                uiState = uiState,
                isBangla = isBangla,
                onOpenDetails = { onNavigateTo("prayer_calculation_details") }
            )
        }

        // 7. Tomorrow Preview Banner
        item {
            TomorrowPreviewCard(
                location = uiState.location,
                settings = uiState.settings,
                isBangla = isBangla,
                onViewTomorrow = { viewModel.setSelectedDate(LocalDate.now().plusDays(1)) }
            )
        }
    }
}

@Composable
private fun DateSelectorBar(
    selectedDate: LocalDate,
    isToday: Boolean,
    isBangla: Boolean,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onSelectToday: () -> Unit,
    onOpenDatePicker: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousDay, modifier = Modifier.testTag("btn_prev_day")) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous Day",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenDatePicker() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"))
                val display = if (isBangla) SinglePrayerTime.toBanglaNumerals(formattedDate) else formattedDate
                Text(
                    text = display,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isToday) {
                    Text(
                        text = if (isBangla) "আজকের দিন (Today)" else "Today",
                        style = MaterialTheme.typography.labelSmall,
                        color = IslamicEmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = if (isBangla) "ক্যালেন্ডার পরিবর্তন করতে ট্যাপ করুন" else "Tap to change date",
                        style = MaterialTheme.typography.bodySmall,
                        color = IslamicMutedText
                    )
                }
            }

            IconButton(onClick = onNextDay, modifier = Modifier.testTag("btn_next_day")) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next Day",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun DateInfoBanner(
    hijriDate: IslamicDate,
    banglaDate: BanglaDate,
    isBangla: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = IslamicEmeraldPrimary.copy(alpha = 0.1f)
        ) {
            Text(
                text = "🌙 ${hijriDate.formatDisplay(isBangla)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = IslamicEmeraldPrimary,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }

        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = IslamicGoldDark.copy(alpha = 0.1f)
        ) {
            Text(
                text = "🌾 ${banglaDate.formatDisplay()}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = IslamicGoldDark,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PrayerDetailRow(
    prayer: SinglePrayerTime,
    isNext: Boolean,
    isCurrent: Boolean,
    isAzanEnabled: Boolean,
    is24Hour: Boolean,
    isBangla: Boolean,
    asrMethod: AsrJuristicMethod = AsrJuristicMethod.SHAFI,
    onToggleAzan: () -> Unit
) {
    val name = if (isBangla) prayer.type.nameBn else prayer.type.nameEn
    val time = if (is24Hour) prayer.formatted24Hour(isBangla) else prayer.formatted12Hour(isBangla)

    val rowBg = when {
        isNext -> IslamicEmeraldPrimary.copy(alpha = 0.12f)
        isCurrent -> IslamicGold.copy(alpha = 0.12f)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isNext) IslamicEmeraldPrimary
                        else if (isCurrent) IslamicGold
                        else MaterialTheme.colorScheme.surface
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (prayer.type) {
                        PrayerType.FAJR -> Icons.Default.WbTwilight
                        PrayerType.SUNRISE -> Icons.Default.WbSunny
                        PrayerType.DHUHR -> Icons.Default.LightMode
                        PrayerType.ASR -> Icons.Default.WbCloudy
                        PrayerType.SUNSET -> Icons.Default.WbTwilight
                        PrayerType.MAGHRIB -> Icons.Default.NightsStay
                        PrayerType.ISHA -> Icons.Default.Bedtime
                    },
                    contentDescription = name,
                    tint = if (isNext || isCurrent) Color.White else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (prayer.type == PrayerType.ASR) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = IslamicEmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = when (asrMethod) {
                                    AsrJuristicMethod.SHAFI -> if (isBangla) "শাফেয়ী (১ গুণ)" else "Shafi (1x)"
                                    AsrJuristicMethod.MALIKI -> if (isBangla) "মালেকী (১ গুণ)" else "Maliki (1x)"
                                    AsrJuristicMethod.HANBALI -> if (isBangla) "হাম্বলী (১ গুণ)" else "Hanbali (1x)"
                                    AsrJuristicMethod.HANAFI -> if (isBangla) "হানাফী (২ গুণ)" else "Hanafi (2x)"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = IslamicEmeraldPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = when (prayer.type) {
                        PrayerType.FAJR -> if (isBangla) "ভোরের ফরজ নামাজ (২ রাকাত)" else "Dawn Prayer (2 Rak'ahs)"
                        PrayerType.SUNRISE -> if (isBangla) "ইশরাকের নামাজের সময় শুরু" else "Ishraq / Sunrise"
                        PrayerType.DHUHR -> if (isBangla) "দুপুরের ফরজ নামাজ (৪ রাকাত)" else "Noon Prayer (4 Rak'ahs)"
                        PrayerType.ASR -> if (isBangla) "বিকেলের ফরজ নামাজ (৪ রাকাত)" else "Afternoon Prayer (4 Rak'ahs)"
                        PrayerType.SUNSET -> if (isBangla) "সূর্যাস্তের সময়" else "Astronomical Sunset"
                        PrayerType.MAGHRIB -> if (isBangla) "সূর্যাস্তের ফরজ নামাজ (৩ রাকাত)" else "Sunset Prayer (3 Rak'ahs)"
                        PrayerType.ISHA -> if (isBangla) "রাতের ফরজ নামাজ (৪ রাকাত + বিতর)" else "Night Prayer (4 Rak'ahs + Witr)"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = IslamicMutedText
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = time,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isNext) IslamicEmeraldPrimary else MaterialTheme.colorScheme.onSurface
            )

            if (prayer.type.isPrimaryPrayer) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onToggleAzan,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isAzanEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = "Toggle Azan",
                        tint = if (isAzanEnabled) IslamicGoldDark else IslamicMutedText,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerCalculationSummaryCard(
    uiState: MainUiState,
    isBangla: Boolean,
    onOpenDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Calculation Info",
                        tint = IslamicEmeraldPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "নামাজ গণনার তথ্য ও সেটিংস" else "Calculation Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "গণনা পদ্ধতি:" else "Calculation Method:",
                    style = MaterialTheme.typography.bodySmall,
                    color = IslamicMutedText
                )
                Text(
                    text = if (isBangla) uiState.settings.calculationMethod.titleBn else uiState.settings.calculationMethod.titleEn,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "আসর মাযহাব:" else "Asr Juristic Method:",
                    style = MaterialTheme.typography.bodySmall,
                    color = IslamicMutedText
                )
                Text(
                    text = if (isBangla) uiState.settings.asrMethod.nameBn else uiState.settings.asrMethod.nameEn,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = IslamicEmeraldPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "অবস্থান:" else "Location:",
                    style = MaterialTheme.typography.bodySmall,
                    color = IslamicMutedText
                )
                Text(
                    text = uiState.location.displayLocation(isBangla),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "টাইমজোন:" else "Timezone:",
                    style = MaterialTheme.typography.bodySmall,
                    color = IslamicMutedText
                )
                Text(
                    text = uiState.location.timeZoneId,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onOpenDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_open_calc_details"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IslamicEmeraldPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "নামাজ গণনার বিস্তারিত বিবরণ দেখুন →" else "Prayer Calculation Details →",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdditionalTimingsCard(
    prayerTimes: PrayerTimesDay,
    is24Hour: Boolean,
    isBangla: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val tahajjudStart = prayerTimes.midnight.plusHours(1)
            val items = listOf(
                Pair(if (isBangla) "সেহরির শেষ সময়" else "Sehri Ends", prayerTimes.sehriEnd.toString()),
                Pair(if (isBangla) "ইফতারের সময়" else "Iftar Time", prayerTimes.iftar.toString()),
                Pair(if (isBangla) "তাহাজ্জুদ উত্তম সময়" else "Tahajjud Time", tahajjudStart.toString()),
                Pair(if (isBangla) "ইসলামিক মধ্যরাত" else "Islamic Midnight", prayerTimes.midnight.toString())
            )

            items.forEachIndexed { index, pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pair.first,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isBangla) SinglePrayerTime.toBanglaNumerals(pair.second) else pair.second,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = IslamicEmeraldPrimary
                    )
                }

                if (index < items.size - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
            }
        }
    }
}

@Composable
private fun TomorrowPreviewCard(
    location: LocationData,
    settings: PrayerSettings,
    isBangla: Boolean,
    onViewTomorrow: () -> Unit
) {
    val tomorrow = LocalDate.now().plusDays(1)
    val timesTomorrow = PrayerTimeEngine.calculatePrayerTimes(tomorrow, location, settings)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onViewTomorrow() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = IslamicEmeraldPrimary.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBangla) "আগামীকালের ফজর নামাজ" else "Tomorrow's Fajr",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isBangla) "সময়: ${SinglePrayerTime.toBanglaNumerals(timesTomorrow.fajr.toString())}" else "Time: ${timesTomorrow.fajr}",
                    style = MaterialTheme.typography.bodySmall,
                    color = IslamicEmeraldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            TextButton(onClick = onViewTomorrow) {
                Text(
                    text = if (isBangla) "আগামীকাল দেখুন →" else "View Tomorrow →",
                    fontWeight = FontWeight.Bold,
                    color = IslamicEmeraldPrimary
                )
            }
        }
    }
}
