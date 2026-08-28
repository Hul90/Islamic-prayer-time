package com.example.ui.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PrayerType
import com.example.model.SinglePrayerTime
import com.example.ui.theme.*
import com.example.ui.viewmodel.SultanToolsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTrackerScreen(
    viewModel: SultanToolsViewModel,
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val records by viewModel.todayPrayerRecords.collectAsState()
    val totalCount by viewModel.totalPrayedCount.collectAsState()

    val today = LocalDate.now()
    val formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"))

    val prayers = listOf(
        PrayerType.FAJR,
        PrayerType.DHUHR,
        PrayerType.ASR,
        PrayerType.MAGHRIB,
        PrayerType.ISHA
    )

    val prayedToday = prayers.count { records[it] == "PRAYED" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "দৈনিক নামাজ ট্র্যাকার" else "Prayer Tracker",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Progress Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = if (isBangla) SinglePrayerTime.toBanglaNumerals(formattedDate) else formattedDate,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "আজকের অগ্রগতি ($prayedToday / ৫)" else "Today's Progress ($prayedToday / 5)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = IslamicEmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (isBangla) "সর্বমোট আদায়: ${SinglePrayerTime.toBanglaNumerals(totalCount.toString())} বার" else "Total Tracked: $totalCount",
                                style = MaterialTheme.typography.labelMedium,
                                color = IslamicGoldDark,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { (prayedToday / 5f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = IslamicEmeraldPrimary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }

            // 2. Checklist for each prayer
            items(prayers) { pType ->
                val status = records[pType] ?: "NOT_MARKED"
                val isPrayed = status == "PRAYED"
                val isMissed = status == "MISSED"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPrayed) IslamicEmeraldPrimary.copy(alpha = 0.12f)
                        else if (isMissed) Color(0xFFFFEBEE)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isPrayed) IslamicEmeraldPrimary
                                        else if (isMissed) Color(0xFFD32F2F)
                                        else MaterialTheme.colorScheme.surface
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPrayed) Icons.Default.Check
                                    else if (isMissed) Icons.Default.Close
                                    else Icons.Default.Circle,
                                    contentDescription = "Status",
                                    tint = if (isPrayed || isMissed) Color.White else IslamicMutedText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = if (isBangla) pType.nameBn else pType.nameEn,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isPrayed) (if (isBangla) "আদায় হয়েছে (Alhamdulillah)" else "Prayed")
                                    else if (isMissed) (if (isBangla) "ছুটে গেছে (কাজা পড়ুন)" else "Missed")
                                    else (if (isBangla) "এখনো হিসাব দেওয়া হয়নি" else "Not Marked"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isPrayed) IslamicEmeraldPrimary else if (isMissed) Color(0xFFC62828) else IslamicMutedText
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalButton(
                                onClick = {
                                    viewModel.setPrayerTrackingStatus(pType, if (isPrayed) "NOT_MARKED" else "PRAYED")
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (isPrayed) IslamicEmeraldPrimary else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(
                                    text = if (isBangla) "আদায়" else "Prayed",
                                    color = if (isPrayed) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            FilledTonalButton(
                                onClick = {
                                    viewModel.setPrayerTrackingStatus(pType, if (isMissed) "NOT_MARKED" else "MISSED")
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (isMissed) Color(0xFFD32F2F) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(
                                    text = if (isBangla) "কাজা" else "Missed",
                                    color = if (isMissed) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 3. Hadith Reminder
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = IslamicEmeraldPrimary.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBangla) "নবীজি (ﷺ) বলেছেন:" else "Prophet Muhammad (ﷺ) said:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = IslamicEmeraldPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "“কেয়ামতের দিন বান্দার কাছ থেকে সর্বপ্রথম যে আমলের হিসাব নেওয়া হবে, তা হলো তার নামাজ।” (তিরমিজি)"
                            else "“The first matter that the slave will be brought to account for on the Day of Judgment is the prayer.” (Tirmidhi)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
