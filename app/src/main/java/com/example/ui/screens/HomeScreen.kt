package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    uiState: MainUiState,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = uiState.settings.language == AppLanguage.BANGLA
    val prayerTimes = uiState.prayerTimes
    val nextPrayer = uiState.nextPrayer
    val currentPrayer = uiState.currentPrayer

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Top Bar / Location & Date Header
        item {
            HeaderSection(
                location = uiState.location,
                hijriDate = uiState.hijriDate,
                banglaDate = uiState.banglaDate,
                isBangla = isBangla,
                isLocationLoading = uiState.isLocationLoading,
                onRefreshLocation = { viewModel.requestAutoLocation() },
                onSelectLocation = { onNavigateTo("district_picker") }
            )
        }

        // 2. Next Prayer Hero Card with Live Countdown
        item {
            NextPrayerHeroCard(
                nextPrayer = nextPrayer,
                currentPrayer = currentPrayer,
                timeRemaining = uiState.timeRemainingToNextPrayer,
                progress = uiState.countdownProgress,
                is24Hour = uiState.settings.is24HourFormat,
                isBangla = isBangla
            )
        }

        // 3. Ramadan Mode Card (if active or quick toggle)
        item {
            RamadanQuickCard(
                isRamadanActive = uiState.settings.isRamadanModeActive,
                isSehriAlarmEnabled = uiState.settings.isSehriAlarmEnabled,
                sehriCountdown = uiState.sehriRemainingStr,
                iftarCountdown = uiState.iftarRemainingStr,
                sehriTime = prayerTimes?.sehriEnd?.toString() ?: "--:--",
                iftarTime = prayerTimes?.iftar?.toString() ?: "--:--",
                isBangla = isBangla,
                onOpenRamadan = { onNavigateTo("ramadan_mode") },
                onOpenSehriAlarm = { onNavigateTo("sehri_alarm") },
                onToggleRamadan = { viewModel.toggleRamadanMode() }
            )
        }

        // 4. Daily Prayer Times Grid
        item {
            SectionHeader(
                title = if (isBangla) "আজকের নামাজের সময়সূচি" else "Today's Prayer Schedule",
                actionText = if (isBangla) "সম্পূর্ণ তালিকা" else "Full Schedule",
                onActionClick = { onNavigateTo("prayer") }
            )
        }

        item {
            prayerTimes?.let { pt ->
                PrayerTimesListCard(
                    prayerTimes = pt,
                    currentPrayer = currentPrayer,
                    nextPrayer = nextPrayer,
                    settings = uiState.settings,
                    isBangla = isBangla,
                    onToggleAzan = { pType, currVal -> viewModel.togglePrayerAzan(pType, currVal) }
                )
            }
        }

        // 5. Quick Sultan Tools Grid
        item {
            SectionHeader(
                title = if (isBangla) "সুলতান টুলস (দ্রুত এক্সেস)" else "Sultan Tools (Quick Access)",
                actionText = if (isBangla) "সকল টুলস" else "View All",
                onActionClick = { onNavigateTo("sultan_tools") }
            )
        }

        item {
            QuickToolsGrid(
                isBangla = isBangla,
                onNavigateTo = onNavigateTo
            )
        }
    }
}

@Composable
private fun HeaderSection(
    location: LocationData,
    hijriDate: IslamicDate,
    banglaDate: BanglaDate,
    isBangla: Boolean,
    isLocationLoading: Boolean,
    onRefreshLocation: () -> Unit,
    onSelectLocation: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Location row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectLocation() }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = IslamicGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = location.displayLocation(isBangla),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (location.isAutoDetected) {
                                if (isBangla) "স্বয়ংক্রিয় জিপিএস • ট্যাপ করে পরিবর্তন" else "GPS Auto • Tap to change"
                            } else {
                                if (isBangla) "ম্যানুয়াল লোকেশন • ট্যাপ করে পরিবর্তন" else "Manual Location • Tap to change"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = IslamicMutedText
                        )
                    }
                }

                IconButton(
                    onClick = onRefreshLocation,
                    modifier = Modifier.testTag("btn_refresh_location")
                ) {
                    if (isLocationLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = IslamicGold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            // Calendar badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Hijri Date Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = IslamicEmeraldPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌙 ", fontSize = 14.sp)
                        Text(
                            text = hijriDate.formatDisplay(isBangla),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = IslamicEmeraldPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Bangla Date Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = IslamicGoldDark.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌾 ", fontSize = 14.sp)
                        Text(
                            text = banglaDate.formatDisplay(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = IslamicGoldDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NextPrayerHeroCard(
    nextPrayer: SinglePrayerTime?,
    currentPrayer: SinglePrayerTime?,
    timeRemaining: String,
    progress: Float,
    is24Hour: Boolean,
    isBangla: Boolean
) {
    val nextName = if (isBangla) nextPrayer?.type?.nameBn ?: "ফজর" else nextPrayer?.type?.nameEn ?: "Fajr"
    val nextTime = if (is24Hour) {
        nextPrayer?.formatted24Hour(isBangla) ?: "--:--"
    } else {
        nextPrayer?.formatted12Hour(isBangla) ?: "--:--"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            IslamicEmeraldPrimary,
                            IslamicEmeraldDark
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBangla) "পরবর্তী ওয়াক্ত" else "NEXT PRAYER",
                            style = MaterialTheme.typography.labelMedium,
                            color = IslamicGoldLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = nextName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = nextTime,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Countdown Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Countdown",
                            tint = IslamicGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "বাকি সময়:" else "Time Remaining:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Text(
                        text = timeRemaining,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = IslamicGold,
                    trackColor = Color.White.copy(alpha = 0.2f),
                )
            }
        }
    }
}

@Composable
private fun RamadanQuickCard(
    isRamadanActive: Boolean,
    isSehriAlarmEnabled: Boolean,
    sehriCountdown: String,
    iftarCountdown: String,
    sehriTime: String,
    iftarTime: String,
    isBangla: Boolean,
    onOpenRamadan: () -> Unit,
    onOpenSehriAlarm: () -> Unit,
    onToggleRamadan: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌙", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "সেহরি ও ইফতার সময়সূচি" else "Sehri & Iftar Timings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                TextButton(onClick = onOpenRamadan) {
                    Text(
                        text = if (isBangla) "রমজান মোড →" else "Ramadan Mode →",
                        style = MaterialTheme.typography.labelLarge,
                        color = IslamicEmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sehri Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onOpenSehriAlarm() },
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isBangla) "সেহরি শেষ" else "Sehri Ends",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = "Alarm",
                                tint = if (isSehriAlarmEnabled) IslamicEmeraldPrimary else IslamicMutedText.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = sehriTime,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isBangla) "বাকি: $sehriCountdown" else "Left: $sehriCountdown",
                            style = MaterialTheme.typography.labelSmall,
                            color = IslamicGoldDark
                        )
                    }
                }

                // Iftar Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBangla) "ইফতার শুরু" else "Iftar Starts",
                            style = MaterialTheme.typography.bodySmall,
                            color = IslamicMutedText
                        )
                        Text(
                            text = iftarTime,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isBangla) "বাকি: $iftarCountdown" else "Left: $iftarCountdown",
                            style = MaterialTheme.typography.labelSmall,
                            color = IslamicEmeraldPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sehri Alarm Quick Entry Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onOpenSehriAlarm() },
                shape = RoundedCornerShape(10.dp),
                color = if (isSehriAlarmEnabled) IslamicEmeraldPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = "Sehri Alarm",
                            tint = if (isSehriAlarmEnabled) IslamicEmeraldPrimary else IslamicGoldDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "সেহরি ও তাহাজ্জুদ অ্যালার্ম" else "Sehri & Tahajjud Alarm",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isSehriAlarmEnabled) {
                                if (isBangla) "চালু আছে 🔔" else "Active 🔔"
                            } else {
                                if (isBangla) "সেট করুন ⚙️" else "Set Alarm ⚙️"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSehriAlarmEnabled) IslamicEmeraldPrimary else IslamicGoldDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrayerTimesListCard(
    prayerTimes: PrayerTimesDay,
    currentPrayer: SinglePrayerTime?,
    nextPrayer: SinglePrayerTime?,
    settings: PrayerSettings,
    isBangla: Boolean,
    onToggleAzan: (PrayerType, Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            val list = prayerTimes.getPrayerList()
            list.forEachIndexed { index, prayer ->
                val isNext = nextPrayer?.type == prayer.type
                val isCurrent = currentPrayer?.type == prayer.type
                val isAzanEnabled = when (prayer.type) {
                    PrayerType.FAJR -> settings.fajrAzan
                    PrayerType.DHUHR -> settings.dhuhrAzan
                    PrayerType.ASR -> settings.asrAzan
                    PrayerType.MAGHRIB -> settings.maghribAzan
                    PrayerType.ISHA -> settings.ishaAzan
                    PrayerType.SUNRISE, PrayerType.SUNSET -> false
                }

                PrayerRowItem(
                    prayer = prayer,
                    isNext = isNext,
                    isCurrent = isCurrent,
                    isAzanEnabled = isAzanEnabled,
                    is24Hour = settings.is24HourFormat,
                    isBangla = isBangla,
                    asrMethod = settings.asrMethod,
                    onToggleAzan = { onToggleAzan(prayer.type, isAzanEnabled) }
                )

                if (index < list.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerRowItem(
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
        isNext -> IslamicEmeraldPrimary.copy(alpha = 0.10f)
        isCurrent -> IslamicGold.copy(alpha = 0.12f)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
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
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isNext || isCurrent) FontWeight.Bold else FontWeight.Normal,
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
                if (isNext) {
                    Text(
                        text = if (isBangla) "পরবর্তী ওয়াক্ত" else "Next Prayer",
                        style = MaterialTheme.typography.labelSmall,
                        color = IslamicEmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                } else if (isCurrent) {
                    Text(
                        text = if (isBangla) "বর্তমান ওয়াক্ত" else "Current Prayer",
                        style = MaterialTheme.typography.labelSmall,
                        color = IslamicGoldDark,
                        fontWeight = FontWeight.Bold
                    )
                }
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
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

data class QuickToolItem(
    val titleEn: String,
    val titleBn: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@Composable
private fun QuickToolsGrid(
    isBangla: Boolean,
    onNavigateTo: (String) -> Unit
) {
    val tools = listOf(
        QuickToolItem("Qibla Finder", "কিবলা কম্পাস", Icons.Default.Explore, "tool_qibla", IslamicEmeraldPrimary),
        QuickToolItem("Tasbih", "ডিজিটাল তাসবিহ", Icons.Default.TouchApp, "tool_tasbih", IslamicGoldDark),
        QuickToolItem("Prayer Tracker", "নামাজ ট্র্যাকার", Icons.Default.FactCheck, "tool_tracker", Color(0xFF2E7D32)),
        QuickToolItem("Calendar", "ক্যালেন্ডার", Icons.Default.CalendarMonth, "calendar", Color(0xFF00838F)),
        QuickToolItem("Zakat Calculator", "যাকাত ক্যালকুলেটর", Icons.Default.Calculate, "tool_zakat", Color(0xFF6A1B9A)),
        QuickToolItem("Nearby Mosque", "নিকটস্থ মসজিদ", Icons.Default.Mosque, "tool_mosques", Color(0xFFD84315))
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        for (i in tools.indices step 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (j in 0 until 3) {
                    if (i + j < tools.size) {
                        val item = tools[i + j]
                        QuickToolCard(
                            item = item,
                            isBangla = isBangla,
                            onClick = { onNavigateTo(item.route) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickToolCard(
    item: QuickToolItem,
    isBangla: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(item.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.titleEn,
                    tint = item.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isBangla) item.titleBn else item.titleEn,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
