package com.example.ui.screens.tools

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DhikrPreset
import com.example.model.SinglePrayerTime
import com.example.ui.theme.*
import com.example.ui.viewmodel.SultanToolsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    viewModel: SultanToolsViewModel,
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.tasbihState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    val progress = (state.count.toFloat() / state.target.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "tasbih_progress")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "ডিজিটাল তাসবিহ" else "Digital Tasbih",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Reset")
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Preset Selector Carousel
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isBangla) "জিকির নির্বাচন করুন" else "Select Dhikr",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = IslamicMutedText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.presets) { preset ->
                        val isSelected = preset.id == state.currentPreset.id
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) IslamicEmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.selectTasbihPreset(preset) }
                        ) {
                            Text(
                                text = if (isBangla) preset.titleBn else preset.titleEn,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 2. Active Dhikr Card (Arabic Calligraphy & Meaning)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.currentPreset.arabic,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicEmeraldPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.currentPreset.transliteration,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isBangla) state.currentPreset.meaningBn else state.currentPreset.meaningEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = IslamicMutedText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 3. Huge Circular Tap Button
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .shadow(10.dp, CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                IslamicEmeraldPrimary,
                                IslamicEmeraldDark
                            )
                        )
                    )
                    .clickable { viewModel.incrementTasbih() }
                    .testTag("btn_tasbih_tap"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(230.dp),
                    color = IslamicGold,
                    strokeWidth = 8.dp,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val countDisplay = if (isBangla) SinglePrayerTime.toBanglaNumerals(state.count.toString()) else state.count.toString()
                    Text(
                        text = countDisplay,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = if (isBangla) "লক্ষ্য: ${SinglePrayerTime.toBanglaNumerals(state.target.toString())}" else "Target: ${state.target}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IslamicGoldLight
                    )
                    Text(
                        text = if (isBangla) "ট্যাপ করুন" else "TAP TO COUNT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                }
            }

            // 4. Target Quick Switch & Completed Stats
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val targets = listOf(33, 99, 100, 1000)
                    targets.forEach { t ->
                        val isSel = state.target == t
                        FilterChip(
                            selected = isSel,
                            onClick = { viewModel.setTasbihTarget(t) },
                            label = { Text(if (isBangla) SinglePrayerTime.toBanglaNumerals(t.toString()) else t.toString()) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = if (isBangla) "সম্পূর্ণ রাউন্ড: ${SinglePrayerTime.toBanglaNumerals(state.completedRounds.toString())}" else "Completed: ${state.completedRounds}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = IslamicEmeraldPrimary
                    )
                    Text(
                        text = if (isBangla) "সর্বমোট গণনা: ${SinglePrayerTime.toBanglaNumerals(state.totalCount.toString())}" else "Total Count: ${state.totalCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldDark
                    )
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(if (isBangla) "কাউন্টার রিসেট করবেন?" else "Reset Counter?") },
            text = { Text(if (isBangla) "বর্তমান গণনা শূন্য করা হবে।" else "This will reset the current count back to zero.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetTasbih()
                    showResetDialog = false
                }) {
                    Text(if (isBangla) "রিসেট" else "Reset", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(if (isBangla) "বাতিল" else "Cancel")
                }
            }
        )
    }
}
