package com.example.ui.screens.tools

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SinglePrayerTime
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrTimerScreen(
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMinutes by remember { mutableIntStateOf(10) }
    var remainingSeconds by remember { mutableIntStateOf(10 * 60) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
        } else if (remainingSeconds == 0) {
            isRunning = false
        }
    }

    val totalSec = (selectedMinutes * 60).toFloat()
    val progress = if (totalSec > 0) (remainingSeconds.toFloat() / totalSec).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "timer_progress")

    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", mins, secs)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "জিকির ও মোরাকাবা টাইমার" else "Dhikr & Meditation Timer",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Duration Preset Chips
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isBangla) "সময়কাল নির্বাচন করুন" else "Select Session Duration",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IslamicMutedText
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(5, 10, 15, 20, 30).forEach { m ->
                        val isSel = selectedMinutes == m && !isRunning
                        FilterChip(
                            selected = isSel,
                            enabled = !isRunning,
                            onClick = {
                                selectedMinutes = m
                                remainingSeconds = m * 60
                            },
                            label = { Text(if (isBangla) "${SinglePrayerTime.toBanglaNumerals(m.toString())} মি." else "$m min") }
                        )
                    }
                }
            }

            // 2. Circular Countdown
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(230.dp),
                    color = IslamicEmeraldPrimary,
                    strokeWidth = 10.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isBangla) SinglePrayerTime.toBanglaNumerals(timeFormatted) else timeFormatted,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isRunning) (if (isBangla) "ইবাদত চলছে..." else "In Progress...") else (if (isBangla) "প্রস্তুত" else "Ready"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isRunning) IslamicEmeraldPrimary else IslamicMutedText
                    )
                }
            }

            // 3. Play / Pause / Reset Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isRunning = false
                        remainingSeconds = selectedMinutes * 60
                    },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = "Reset", tint = IslamicMutedText, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.width(20.dp))

                FloatingActionButton(
                    onClick = { isRunning = !isRunning },
                    containerColor = IslamicEmeraldPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.size(68.dp)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}
