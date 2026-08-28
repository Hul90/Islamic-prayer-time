package com.example.ui.screens.tools

import android.app.DatePickerDialog
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SinglePrayerTime
import com.example.ui.theme.*
import com.example.ui.viewmodel.SultanToolsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateConverterScreen(
    viewModel: SultanToolsViewModel,
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.dateConverterState.collectAsState()
    val context = LocalContext.current

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                viewModel.setConvertDate(LocalDate.of(year, month + 1, dayOfMonth))
            },
            state.inputGregorian.year,
            state.inputGregorian.monthValue - 1,
            state.inputGregorian.dayOfMonth
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "৩-ইন-১ তারিখ কনভার্টার" else "3-in-1 Date Converter",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Selector Card
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
                        text = if (isBangla) "ইংরেজি (গ্রেগরিয়ান) তারিখ নির্বাচন করুন" else "Select Gregorian Date",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val formatted = state.inputGregorian.format(DateTimeFormatter.ofPattern("dd MMMM yyyy (EEEE)"))
                    OutlinedButton(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Date", tint = IslamicEmeraldPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isBangla) SinglePrayerTime.toBanglaNumerals(formatted) else formatted,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setConvertDate(LocalDate.now()) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicEmeraldPrimary)
                        ) {
                            Text(if (isBangla) "আজকের দিন" else "Today")
                        }
                    }
                }
            }

            // 2. Converted Results Cards
            // A. Hijri Date Result
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IslamicEmeraldPrimary.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌙", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBangla) "ইসলামিক হিজরি ক্যালেন্ডার" else "Islamic Hijri Calendar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = IslamicEmeraldPrimary
                            )
                            Text(
                                text = if (isBangla) "উম্মুল কুরা গণনা পদ্ধতি অনুযায়ী" else "Umm al-Qura standard astronomical approximation",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = state.convertedHijri.formatDisplay(isBangla),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = IslamicEmeraldPrimary
                    )
                }
            }

            // B. Bangla Date Result
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IslamicGoldDark.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌾", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBangla) "বাংলাদেশ সংশোধিত বঙ্গাব্দ (বাংলা সন)" else "Bangladesh Revised Bangla Calendar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGoldDark
                            )
                            Text(
                                text = if (isBangla) "বাংলা একাডেমি অনুমোদিত ও সরকারি নিয়ম অনুযায়ী" else "Bangla Academy approved official calculation",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = state.convertedBangla.formatDisplay(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = IslamicGoldDark
                    )
                    Text(
                        text = if (isBangla) "ঋতু: ${state.convertedBangla.season}" else "Season: ${state.convertedBangla.season}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
