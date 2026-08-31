package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AzanSoundType
import com.example.model.SehriAlarmMode
import com.example.model.SinglePrayerTime
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SehriAlarmScreen(
    viewModel: MainViewModel,
    uiState: MainUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings = uiState.settings
    val isBangla = settings.language == AppLanguage.BANGLA
    val prayerTimes = uiState.prayerTimes
    val sehriEndTime = prayerTimes?.sehriEnd ?: LocalTime.of(5, 4)

    // Calculate effective alarm time preview
    val calculatedAlarmTime: LocalTime = when (settings.sehriAlarmMode) {
        SehriAlarmMode.BEFORE_SEHRI_END -> {
            sehriEndTime.minusMinutes(settings.sehriAlarmOffsetMinutes.toLong())
        }
        SehriAlarmMode.CUSTOM_TIME -> {
            LocalTime.of(
                settings.sehriAlarmCustomHour.coerceIn(0, 23),
                settings.sehriAlarmCustomMinute.coerceIn(0, 59)
            )
        }
    }

    val isPreviewPlaying by viewModel.isPlayingAudioPreview.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "সেহরি অ্যালার্ম সেটিংস" else "Sehri Wake-up Alarm",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_sehri_alarm")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Master Switch Card with Hero Visual
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (settings.isSehriAlarmEnabled) IslamicEmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = if (settings.isSehriAlarmEnabled) androidx.compose.foundation.BorderStroke(1.5.dp, IslamicEmeraldPrimary.copy(alpha = 0.4f)) else null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (settings.isSehriAlarmEnabled) IslamicEmeraldPrimary else IslamicGoldDark.copy(alpha = 0.2f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = "Alarm",
                                        tint = if (settings.isSehriAlarmEnabled) Color.White else IslamicGoldDark,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = if (isBangla) "সেহরি অ্যালার্ম" else "Sehri Wake-up Alarm",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (settings.isSehriAlarmEnabled) {
                                        if (isBangla) "অ্যালার্ম সক্রিয় আছে" else "Alarm is active"
                                    } else {
                                        if (isBangla) "অ্যালার্ম বন্ধ রয়েছে" else "Alarm is disabled"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (settings.isSehriAlarmEnabled) IslamicEmeraldPrimary else IslamicMutedText
                                )
                            }
                        }

                        Switch(
                            checked = settings.isSehriAlarmEnabled,
                            onCheckedChange = { viewModel.toggleSehriAlarm(it) },
                            modifier = Modifier.testTag("switch_sehri_alarm_master")
                        )
                    }

                    if (settings.isSehriAlarmEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = IslamicEmeraldPrimary.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Next Alarm Time Highlight
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isBangla) "পরবর্তী অ্যালার্ম বাজবে" else "Next Alarm Rings At",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IslamicMutedText
                                )
                                val timeText = SinglePrayerTime.formatLocalTime(calculatedAlarmTime, settings.is24HourFormat, isBangla)
                                Text(
                                    text = timeText,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = IslamicEmeraldPrimary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isBangla) "আজকের সেহরি শেষ" else "Today's Sehri Ends",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IslamicMutedText
                                )
                                val endText = SinglePrayerTime.formatLocalTime(sehriEndTime, settings.is24HourFormat, isBangla)
                                Text(
                                    text = endText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = settings.isSehriAlarmEnabled) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // 2. Mode Selector: Offset vs Custom Time
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isBangla) "অ্যালার্মের ধরন নির্বাচন করুন" else "Select Alarm Timing Method",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Option A: Before Sehri End
                            val isModeOffset = settings.sehriAlarmMode == SehriAlarmMode.BEFORE_SEHRI_END
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setSehriAlarmMode(SehriAlarmMode.BEFORE_SEHRI_END) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isModeOffset) IslamicEmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                                border = if (isModeOffset) androidx.compose.foundation.BorderStroke(1.5.dp, IslamicEmeraldPrimary) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isModeOffset,
                                        onClick = { viewModel.setSehriAlarmMode(SehriAlarmMode.BEFORE_SEHRI_END) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isBangla) "সেহরি শেষ হওয়ার নির্দিষ্ট সময় পূর্বে" else "Minutes Before Sehri Ends",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (isModeOffset) FontWeight.Bold else FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isBangla) "প্রতিদিনের সেহরি শেষ সময় অনুযায়ী স্বয়ংক্রিয়ভাবে অ্যালার্ম মিলবে" else "Automatically adjusts with daily dynamic Sehri timetable",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = IslamicMutedText
                                        )
                                    }
                                }
                            }

                            // Option B: Custom Fixed Time
                            val isModeCustom = settings.sehriAlarmMode == SehriAlarmMode.CUSTOM_TIME
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setSehriAlarmMode(SehriAlarmMode.CUSTOM_TIME) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isModeCustom) IslamicEmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                                border = if (isModeCustom) androidx.compose.foundation.BorderStroke(1.5.dp, IslamicEmeraldPrimary) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isModeCustom,
                                        onClick = { viewModel.setSehriAlarmMode(SehriAlarmMode.CUSTOM_TIME) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isBangla) "নিজের ইচ্ছামতো নির্দিষ্ট সময়ে (কাস্টম টাইম)" else "Custom Fixed Alarm Time",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (isModeCustom) FontWeight.Bold else FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isBangla) "আপনার পছন্দসই নির্দিষ্ট সময় অনুযায়ী অ্যালার্ম বাজবে (যেমন: ০৪:১৫ AM)" else "Set your own preferred wake-up hour and minute",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = IslamicMutedText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Configuration Sub-Panel based on Mode
                    if (settings.sehriAlarmMode == SehriAlarmMode.BEFORE_SEHRI_END) {
                        // Offset Configuration Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isBangla) "কত মিনিট পূর্বে অ্যালার্ম চান?" else "Minutes Before Sehri End",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = IslamicEmeraldPrimary
                                    ) {
                                        Text(
                                            text = if (isBangla) "${settings.sehriAlarmOffsetMinutes} মিনিট" else "${settings.sehriAlarmOffsetMinutes} min",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                // Quick preset chips
                                val offsetPresets = listOf(15, 20, 30, 45, 60, 90)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    offsetPresets.take(3).forEach { offset ->
                                        val isSel = settings.sehriAlarmOffsetMinutes == offset
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { viewModel.setSehriAlarmOffset(offset) },
                                            label = { Text(if (isBangla) "$offset মি." else "${offset}m") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    offsetPresets.drop(3).forEach { offset ->
                                        val isSel = settings.sehriAlarmOffsetMinutes == offset
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { viewModel.setSehriAlarmOffset(offset) },
                                            label = { Text(if (isBangla) "$offset মি." else "${offset}m") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                // Fine-tune slider
                                Slider(
                                    value = settings.sehriAlarmOffsetMinutes.toFloat(),
                                    onValueChange = { viewModel.setSehriAlarmOffset(it.toInt()) },
                                    valueRange = 5f..120f,
                                    steps = 22,
                                    colors = SliderDefaults.colors(
                                        thumbColor = IslamicEmeraldPrimary,
                                        activeTrackColor = IslamicEmeraldPrimary
                                    ),
                                    modifier = Modifier.testTag("slider_sehri_offset")
                                )
                            }
                        }
                    } else {
                        // Custom Time Configuration Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = if (isBangla) "আপনার পছন্দের সময় নির্ধারণ করুন" else "Set Custom Alarm Time",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Time selector controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Hour Selector
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        IconButton(
                                            onClick = {
                                                val newH = (settings.sehriAlarmCustomHour + 1) % 24
                                                viewModel.setSehriAlarmCustomTime(newH, settings.sehriAlarmCustomMinute)
                                            }
                                        ) {
                                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Hour Up")
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                            modifier = Modifier.size(width = 64.dp, height = 56.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = String.format("%02d", settings.sehriAlarmCustomHour),
                                                    style = MaterialTheme.typography.headlineMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = IslamicEmeraldPrimary
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                val newH = if (settings.sehriAlarmCustomHour - 1 < 0) 23 else settings.sehriAlarmCustomHour - 1
                                                viewModel.setSehriAlarmCustomTime(newH, settings.sehriAlarmCustomMinute)
                                            }
                                        ) {
                                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Hour Down")
                                        }

                                        Text(
                                            text = if (isBangla) "ঘণ্টা" else "Hour",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = IslamicMutedText
                                        )
                                    }

                                    Text(
                                        text = ":",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )

                                    // Minute Selector
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        IconButton(
                                            onClick = {
                                                val newM = (settings.sehriAlarmCustomMinute + 5) % 60
                                                viewModel.setSehriAlarmCustomTime(settings.sehriAlarmCustomHour, newM)
                                            }
                                        ) {
                                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Minute Up")
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                            modifier = Modifier.size(width = 64.dp, height = 56.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = String.format("%02d", settings.sehriAlarmCustomMinute),
                                                    style = MaterialTheme.typography.headlineMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = IslamicEmeraldPrimary
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                val newM = if (settings.sehriAlarmCustomMinute - 5 < 0) 55 else settings.sehriAlarmCustomMinute - 5
                                                viewModel.setSehriAlarmCustomTime(settings.sehriAlarmCustomHour, newM)
                                            }
                                        ) {
                                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Minute Down")
                                        }

                                        Text(
                                            text = if (isBangla) "মিনিট" else "Minute",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = IslamicMutedText
                                        )
                                    }
                                }

                                // Quick time suggestions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(Pair(3, 45), Pair(4, 0), Pair(4, 15), Pair(4, 30)).forEach { (h, m) ->
                                        val isSel = settings.sehriAlarmCustomHour == h && settings.sehriAlarmCustomMinute == m
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { viewModel.setSehriAlarmCustomTime(h, m) },
                                            label = { Text(String.format("%02d:%02d", h, m)) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. Alarm Sound and Vibration Configuration
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = if (isBangla) "অ্যালার্ম সাউন্ড ও ভাইব্রেশন" else "Alarm Sound & Vibration",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Sound Type Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AzanSoundType.entries.forEach { sound ->
                                    val isSel = settings.sehriAlarmSoundType == sound
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { viewModel.setSehriAlarmSoundType(sound) },
                                        label = { Text(if (isBangla) sound.nameBn else sound.nameEn) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Vibration Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBangla) "অ্যালার্মের সময় ভাইব্রেশন" else "Vibration during Alarm",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Switch(
                                    checked = settings.sehriAlarmVibration,
                                    onCheckedChange = { viewModel.setSehriAlarmVibration(it) }
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            // Test Alarm Audio Button
                            FilledTonalButton(
                                onClick = {
                                    if (isPreviewPlaying) {
                                        viewModel.stopAzanVolumePreview()
                                    } else {
                                        viewModel.playAzanVolumePreview(settings.azanVolume)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (isPreviewPlaying) MaterialTheme.colorScheme.errorContainer else IslamicEmeraldPrimary.copy(alpha = 0.15f),
                                    contentColor = if (isPreviewPlaying) MaterialTheme.colorScheme.onErrorContainer else IslamicEmeraldPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPreviewPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "Test Audio",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPreviewPlaying) {
                                        if (isBangla) "অ্যালার্ম সাউন্ড বন্ধ করুন ⏹" else "Stop Test Alarm ⏹"
                                    } else {
                                        if (isBangla) "🔊 সাউন্ড টেস্ট করুন" else "🔊 Test Alarm Sound"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // 5. Helpful Tips for Sehri Wake-up
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = IslamicGoldDark.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGoldDark.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = IslamicGoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isBangla) "সেহরি অ্যালার্মের বিশেষ বৈশিষ্ট্য:" else "Sehri Alarm Highlights:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicGoldDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBangla) {
                                        "• নোটিফিকেশনে ৫ মিনিট স্নুজ (Snooze) এবং বন্ধ করার বোতাম পাবেন।\n• ডিভাইস রিবুট হলেও স্বয়ংক্রিয়ভাবে অ্যালার্ম বহাল থাকবে।\n• তাহাজ্জুদ ও সেহরির বরকতপূর্ণ সময়ে জেগে উঠার জন্য এটি সহায়ক।"
                                    } else {
                                        "• Includes 5-minute Snooze and Dismiss buttons directly in the alert.\n• Alarms persist automatically across device reboots.\n• Designed to help you rise for Tahajjud and the blessed Sehri meal."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
