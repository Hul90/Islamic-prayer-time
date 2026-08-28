package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculation.PrayerTimeEngine
import com.example.model.AppLanguage
import com.example.model.SinglePrayerTime
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RamadanModeScreen(
    viewModel: MainViewModel,
    uiState: MainUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = uiState.settings.language == AppLanguage.BANGLA
    val prayerTimes = uiState.prayerTimes

    // Generate 30 days Ramadan calendar starting from today
    val today = LocalDate.now()
    val ramadanDays = remember(uiState.location, uiState.settings) {
        (0 until 30).map { offset ->
            val d = today.plusDays(offset.toLong())
            val times = PrayerTimeEngine.calculatePrayerTimes(d, uiState.location, uiState.settings)
            Triple(d, times.sehriEnd, times.iftar)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "পবিত্র মাহে রমজান মোড" else "Holy Ramadan Mode",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Live Sehri & Iftar Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = IslamicEmeraldPrimary.copy(alpha = 0.12f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌙", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) "আজকের সেহরি ও ইফতার" else "Today's Sehri & Iftar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = IslamicEmeraldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isBangla) "সেহরির শেষ সময়" else "Sehri Ends",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IslamicMutedText
                                )
                                Text(
                                    text = prayerTimes?.sehriEnd?.toString() ?: "--:--",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isBangla) "বাকি: ${uiState.sehriRemainingStr}" else "Remaining: ${uiState.sehriRemainingStr}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IslamicGoldDark
                                )
                            }

                            Column {
                                Text(
                                    text = if (isBangla) "ইফতারের সময়" else "Iftar Starts",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IslamicMutedText
                                )
                                Text(
                                    text = prayerTimes?.iftar?.toString() ?: "--:--",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicEmeraldPrimary
                                )
                                Text(
                                    text = if (isBangla) "বাকি: ${uiState.iftarRemainingStr}" else "Remaining: ${uiState.iftarRemainingStr}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IslamicEmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 2. Ramadan Duas (Sehri Niyyah & Iftar Dua)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (isBangla) "রোজার নিয়ত (সেহরি)" else "Niyyah for Fasting (Sehri)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "نَوَيْتُ اَنْ اُصُوْمَ غَدًا مِّنْ شَهْرِ رَمَضَانَ الْمُبَارَكِ فَرْضًا لَكَ يَارَبِّ فَتَقَبَّلْ مِنِّي",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicEmeraldPrimary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "উচ্চারণ: নাওয়াইতু আন আছুমা গাদাম মিন শাহরি রমাদানাল মুবারাকি ফারদাল্লাকা ইয়া রাব্বি ফাতাকাব্বাল মিন্নি।"
                            else "Transliteration: Nawaytu an asuma ghadan min shahri ramadan al-mubarak fardal laka ya Rabbi fataqabbal minni.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isBangla) "ইফতারের দোয়া" else "Dua for Iftar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اللَّهُمَّ لَكَ صُمْتُ وَعَلَى رِزْقِكَ أَفْطَرْتُ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicEmeraldPrimary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "উচ্চারণ: আল্লাহুম্মা লাকা ছুমতু ওয়া আলা রিযক্বিকা আফত্বারতু।"
                            else "Transliteration: Allahumma laka sumtu wa 'ala rizqika aftartu.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // 3. 30 Days Ramadan Timetable
            item {
                Text(
                    text = if (isBangla) "৩০ দিনের সেহরি ও ইফতার সূচি" else "30-Day Ramadan Timetable",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBangla) "রোজা / তারিখ" else "Day / Date", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = IslamicMutedText)
                            Text(text = if (isBangla) "সেহরি শেষ" else "Sehri", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = IslamicMutedText)
                            Text(text = if (isBangla) "ইফতার" else "Iftar", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = IslamicMutedText)
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        ramadanDays.forEachIndexed { idx, (date, sehri, iftar) ->
                            val isCurrent = date.isEqual(today)
                            val dayNum = idx + 1
                            val dateStr = date.format(DateTimeFormatter.ofPattern("dd MMM"))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrent) IslamicEmeraldPrimary.copy(alpha = 0.12f) else Color.Transparent)
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBangla) "রোজা ${SinglePrayerTime.toBanglaNumerals(dayNum.toString())} (${SinglePrayerTime.toBanglaNumerals(dateStr)})" else "Day $dayNum ($dateStr)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) IslamicEmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isBangla) SinglePrayerTime.toBanglaNumerals(sehri.toString()) else sehri.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isBangla) SinglePrayerTime.toBanglaNumerals(iftar.toString()) else iftar.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicEmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
