package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    uiState: MainUiState,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val settings = uiState.settings
    val isBangla = settings.language == AppLanguage.BANGLA

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "সেটিংস" else "Settings",
                        fontWeight = FontWeight.Bold
                    )
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Location Settings
            SettingsSectionHeader(title = if (isBangla) "লোকেশন ও অবস্থান" else "Location")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateTo("district_picker") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = IslamicEmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = uiState.location.displayLocation(isBangla),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (uiState.location.isAutoDetected) {
                                    if (isBangla) "স্বয়ংক্রিয় জিপিএস • পরিবর্তন করতে ট্যাপ করুন" else "Auto GPS • Tap to change"
                                } else {
                                    if (isBangla) "ম্যানুয়াল নির্বাচন • পরিবর্তন করতে ট্যাপ করুন" else "Manual • Tap to change"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                        }
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Open", tint = IslamicMutedText)
                }
            }

            // 2. Azan and Notification Settings
            SettingsSectionHeader(title = if (isBangla) "আজান ও নোটিফিকেশন" else "Azan & Alerts")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Master Azan Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBangla) "আজান নোটিফিকেশন চালু" else "Enable Azan Notifications",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isBangla) "ওয়াক্তমত পুশ নোটিফিকেশন এবং অ্যালার্ম দিন" else "Send push alert & alarm at prayer times",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                        }
                        Switch(
                            checked = settings.isAzanGloballyEnabled,
                            onCheckedChange = { viewModel.toggleGlobalAzan(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Sound Type Selector
                    Text(
                        text = if (isBangla) "আজানের সুর ও শব্দ" else "Azan Sound Type",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AzanSoundType.entries.forEach { sound ->
                            val isSel = settings.azanSoundType == sound
                            FilterChip(
                                selected = isSel,
                                onClick = { viewModel.setAzanSoundType(sound) },
                                label = { Text(if (isBangla) sound.nameBn else sound.nameEn) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Volume Slider & Audio Test
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "আজানের ভলিউম" else "Azan Volume",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${(settings.azanVolume * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = IslamicEmeraldPrimary
                        )
                    }

                    Slider(
                        value = settings.azanVolume,
                        onValueChange = { viewModel.setAzanVolume(it) },
                        valueRange = 0.05f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = IslamicEmeraldPrimary,
                            activeTrackColor = IslamicEmeraldPrimary
                        ),
                        modifier = Modifier.testTag("slider_azan_volume")
                    )

                    // Audio Test Preview Button
                    val isPreviewPlaying by viewModel.isPlayingAudioPreview.collectAsState()
                    FilledTonalButton(
                        onClick = {
                            if (isPreviewPlaying) {
                                viewModel.stopAzanVolumePreview()
                            } else {
                                viewModel.playAzanVolumePreview(settings.azanVolume)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_test_azan_volume"),
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
                                if (isBangla) "টেস্ট আজান বন্ধ করুন ⏹" else "Stop Test Azan ⏹"
                            } else {
                                if (isBangla) "🔊 আজান ভলিউম টেস্ট করুন" else "🔊 Test Azan Volume"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Vibration toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "ভাইব্রেশন (কম্পন)" else "Vibration",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = settings.isVibrationEnabled,
                            onCheckedChange = { viewModel.setVibration(it) }
                        )
                    }
                }
            }

            // 2.5 Sehri Wake-up Alarm Settings Card
            SettingsSectionHeader(title = if (isBangla) "সেহরি অ্যালার্ম (ঘুম থেকে উঠার সময়)" else "Sehri Wake-up Alarm")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateTo("sehri_alarm") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = IslamicGoldDark.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Bedtime,
                                    contentDescription = "Sehri Alarm",
                                    tint = IslamicGoldDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBangla) "কাস্টম সেহরি অ্যালার্ম" else "Custom Sehri Alarm",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (settings.isSehriAlarmEnabled) IslamicEmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (settings.isSehriAlarmEnabled) {
                                            if (isBangla) "চালু" else "Active"
                                        } else {
                                            if (isBangla) "বন্ধ" else "Off"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (settings.isSehriAlarmEnabled) IslamicEmeraldPrimary else IslamicMutedText,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (settings.isSehriAlarmEnabled) {
                                    when (settings.sehriAlarmMode) {
                                        SehriAlarmMode.BEFORE_SEHRI_END -> if (isBangla) "সেহরি শেষ হওয়ার ${settings.sehriAlarmOffsetMinutes} মিনিট পূর্বে" else "${settings.sehriAlarmOffsetMinutes} min before Sehri ends"
                                        SehriAlarmMode.CUSTOM_TIME -> if (isBangla) "নির্দিষ্ট সময়: ${String.format("%02d:%02d", settings.sehriAlarmCustomHour, settings.sehriAlarmCustomMinute)}" else "Custom time: ${String.format("%02d:%02d", settings.sehriAlarmCustomHour, settings.sehriAlarmCustomMinute)}"
                                    }
                                } else {
                                    if (isBangla) "সেহরির জন্য নিজের ইচ্ছামতো অ্যালার্ম সেট করুন" else "Set custom alarm to wake up for Sehri"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Sehri Alarm",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3. Asr Juristic Method (Clearly Separated)
            SettingsSectionHeader(title = if (isBangla) "আসরের ওয়াক্ত ও মাযহাব পদ্ধতি" else "Asr Juristic Method")
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
                        text = if (isBangla) "আসর নামাজের মাযহাব নির্বাচন (Default: শাফেয়ী)" else "Select Asr Juristic Method (Default: Shafi)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsrJuristicMethod.entries.forEach { asr ->
                            val isSel = settings.asrMethod == asr
                            FilterChip(
                                selected = isSel,
                                onClick = { viewModel.setAsrMethod(asr) },
                                label = {
                                    Text(
                                        text = if (isBangla) asr.nameBn else asr.nameEn,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chip_asr_${asr.id}")
                            )
                        }
                    }

                    // Educational Scholarly Explanation Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IslamicEmeraldPrimary.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicEmeraldPrimary.copy(alpha = 0.25f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = IslamicEmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBangla) "আসরের ফিকহি গণনা পদ্ধতি" else "Juristic Calculation Method",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicEmeraldPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBangla) {
                                    "আসরের সময় নির্ধারণে বিভিন্ন ফিকহি পদ্ধতিতে ছায়ার অনুপাত ভিন্নভাবে গণনা করা হয়। এখানে নির্বাচিত Shafi পদ্ধতিতে shadow factor 1 ব্যবহার করা হচ্ছে। ব্যবহারকারী চাইলে Hanafi, Maliki বা Hanbali পদ্ধতিও নির্বাচন করতে পারবেন।"
                                } else {
                                    "Different recognized juristic methods use different shadow-ratio conventions when determining the beginning of Asr. The selected Shafi method uses a shadow factor of 1. Users can switch between Shafi, Maliki, Hanbali and Hanafi methods."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla) {
                                    "সহীহ হাদিসে বর্ণিত নামাজের ওয়াক্তসমূহ (যেমন জিবরীল আঃ এর ইমামতি বিষয়ক হাদিস - সহীহ মুসলিম ৬১২, জামে আত-তিরমিযী ১৪৯) এর আলোকে ফুকাহায়ে কেরাম এই ফিকহি পদ্ধতিসমূহ উদ্ভাবন করেছেন।"
                                } else {
                                    "Authentic narrations describing prayer times (such as the reports of Jibril AS leading prayer - Sahih Muslim 612, Jami` at-Tirmidhi 149) were interpreted by classical jurists to establish these recognized calculation methods."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                        }
                    }
                }
            }

            // 4. Astronomical Calculation Method (Clearly Separated)
            SettingsSectionHeader(title = if (isBangla) "নামাজ হিসাবের জ্যোতির্বৈজ্ঞানিক পদ্ধতি" else "Calculation Method & Sun Angles")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBangla) "আন্তর্জাতিক প্রতিষ্ঠান ও পদ্ধতি" else "Astronomical Calculation Method",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    PrayerCalculationMethod.entries.forEach { meth ->
                        val isSel = settings.calculationMethod == meth
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setCalculationMethod(meth) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSel,
                                onClick = { viewModel.setCalculationMethod(meth) },
                                modifier = Modifier.testTag("radio_calc_${meth.id}")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isBangla) meth.nameBn else meth.nameEn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Fajr: ${meth.fajrAngle}° • Isha: ${if (meth.isIshaFixedMinutes) "${meth.ishaMinutes.toInt()}m" else "${meth.ishaAngle}°"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IslamicMutedText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { onNavigateTo("prayer_calculation_details") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_settings_calc_details"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IslamicEmeraldPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "নামাজ গণনার বিস্তারিত বিবরণ স্ক্রিন →" else "Prayer Calculation Details Screen →",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4. App Preferences (Language & Theme)
            SettingsSectionHeader(title = if (isBangla) "অ্যাপের ভাষা ও থিম" else "Language & Appearance")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Language
                    Text(
                        text = if (isBangla) "ভাষা (Language)" else "Language",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            val isSel = settings.language == lang
                            FilterChip(
                                selected = isSel,
                                onClick = { viewModel.setLanguage(lang) },
                                label = { Text(lang.displayName) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Theme
                    Text(
                        text = if (isBangla) "থিম (Theme Mode)" else "Theme Mode",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppThemeMode.entries.forEach { tm ->
                            val isSel = settings.themeMode == tm
                            FilterChip(
                                selected = isSel,
                                onClick = { viewModel.setThemeMode(tm) },
                                label = { Text(if (isBangla) tm.nameBn else tm.nameEn) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // 12/24 Hour format
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "২৪-ঘণ্টা সময় ফরম্যাট" else "24-Hour Time Format",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = settings.is24HourFormat,
                            onCheckedChange = { viewModel.set24Hour(it) }
                        )
                    }
                }
            }

            // 5. About & Developer Info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateTo("about") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = IslamicEmeraldPrimary.copy(alpha = 0.10f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "About", tint = IslamicEmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBangla) "অ্যাপ সম্পর্কে ও ডেভেলপার" else "About App & Developer",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "MD SULTAN MAHAMUD • v1.0.0",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                        }
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Open", tint = IslamicMutedText)
                }
            }

            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 4.dp)
    )
}
