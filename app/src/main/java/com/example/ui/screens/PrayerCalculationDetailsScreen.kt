package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AsrJuristicMethod
import com.example.model.SinglePrayerTime
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicEmeraldPrimary
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicMutedText
import com.example.ui.viewmodel.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerCalculationDetailsScreen(
    uiState: MainUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings = uiState.settings
    val location = uiState.location
    val isBangla = settings.language == AppLanguage.BANGLA
    val prayerTimes = uiState.prayerTimes

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "নামাজ গণনার বিস্তারিত বিবরণ" else "Prayer Calculation Details",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_calc_details")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
            // 1. Current Astronomical & Method Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isBangla) "বর্তমান গণনা প্যারামিটার" else "Active Calculation Parameters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = IslamicEmeraldPrimary
                    )

                    CalculationInfoRow(
                        label = if (isBangla) "অবস্থান (Location)" else "Location",
                        value = location.displayLocation(isBangla),
                        isBangla = isBangla
                    )

                    CalculationInfoRow(
                        label = if (isBangla) "অক্ষাংশ (Latitude)" else "Latitude",
                        value = "${String.format("%.4f", location.latitude)}° N",
                        isBangla = isBangla
                    )

                    CalculationInfoRow(
                        label = if (isBangla) "দ্রাঘিমাংশ (Longitude)" else "Longitude",
                        value = "${String.format("%.4f", location.longitude)}° E",
                        isBangla = isBangla
                    )

                    CalculationInfoRow(
                        label = if (isBangla) "টাইমজোন (Timezone)" else "Timezone",
                        value = location.timeZoneId,
                        isBangla = isBangla
                    )

                    CalculationInfoRow(
                        label = if (isBangla) "নামাজ হিসাবের পদ্ধতি" else "Calculation Method",
                        value = if (isBangla) settings.calculationMethod.titleBn else settings.calculationMethod.titleEn,
                        isBangla = isBangla
                    )

                    CalculationInfoRow(
                        label = if (isBangla) "আসর মাযহাব (Asr Juristic Method)" else "Asr Juristic Method",
                        value = when (settings.asrMethod) {
                            AsrJuristicMethod.SHAFI -> if (isBangla) "Shafi (শাফেয়ী)" else "Shafi"
                            AsrJuristicMethod.MALIKI -> if (isBangla) "Maliki (মালেকী)" else "Maliki"
                            AsrJuristicMethod.HANBALI -> if (isBangla) "Hanbali (হাম্বলী)" else "Hanbali"
                            AsrJuristicMethod.HANAFI -> if (isBangla) "Hanafi (হানাফী)" else "Hanafi"
                        },
                        highlight = true,
                        isBangla = isBangla
                    )

                    CalculationInfoRow(
                        label = if (isBangla) "আসর শ্যাডো ফ্যাক্টর (Asr Shadow Factor)" else "Asr Shadow Factor",
                        value = if (settings.asrMethod.shadowFactor == 2.0) "2" else "1",
                        highlight = true,
                        isBangla = isBangla
                    )

                    CalculationInfoRow(
                        label = if (isBangla) "ফজর সৌর কোণ (Fajr Angle)" else "Fajr Angle",
                        value = "${settings.calculationMethod.fajrAngle}°",
                        isBangla = isBangla
                    )

                    CalculationInfoRow(
                        label = if (isBangla) "এশা সৌর কোণ (Isha Angle)" else "Isha Angle",
                        value = if (settings.calculationMethod.isIshaFixedMinutes) {
                            "+${settings.calculationMethod.ishaMinutes.toInt()} mins (Fixed)"
                        } else {
                            "${settings.calculationMethod.ishaAngle}°"
                        },
                        isBangla = isBangla
                    )

                    CalculationInfoRow(
                        label = if (isBangla) "উচ্চ অক্ষাংশ নিয়ম (High Latitude Rule)" else "High Latitude Rule",
                        value = if (isBangla) settings.highLatitudeAdjustment.titleBn else settings.highLatitudeAdjustment.titleEn,
                        isBangla = isBangla
                    )

                    CalculationInfoRow(
                        label = if (isBangla) "মিনিট সমন্বয় (Minute Adjustments)" else "Minute Adjustments",
                        value = if (isBangla) "F: ${settings.fajrOffsetMinutes}m, D: ${settings.dhuhrOffsetMinutes}m, A: ${settings.asrOffsetMinutes}m, M: ${settings.maghribOffsetMinutes}m, I: ${settings.ishaOffsetMinutes}m"
                        else "F: ${settings.fajrOffsetMinutes}m, D: ${settings.dhuhrOffsetMinutes}m, A: ${settings.asrOffsetMinutes}m, M: ${settings.maghribOffsetMinutes}m, I: ${settings.ishaOffsetMinutes}m",
                        isBangla = isBangla
                    )
                }
            }

            // 2. Educational Juristic Card: Respectful and Accurate Juristic Explanation
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IslamicEmeraldPrimary.copy(alpha = 0.08f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicEmeraldPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Scholarly Evidence",
                            tint = IslamicEmeraldPrimary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isBangla) "আসরের ফিকহি গণনা পদ্ধতি" else "Juristic Calculation Method",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IslamicEmeraldPrimary
                        )
                    }

                    Text(
                        text = if (isBangla) {
                            "আসরের সময় নির্ধারণে বিভিন্ন ফিকহি পদ্ধতিতে ছায়ার অনুপাত ভিন্নভাবে গণনা করা হয়। এখানে নির্বাচিত Shafi পদ্ধতিতে shadow factor 1 ব্যবহার করা হচ্ছে। ব্যবহারকারী চাইলে Hanafi, Maliki বা Hanbali পদ্ধতিও নির্বাচন করতে পারবেন।"
                        } else {
                            "Different recognized juristic methods use different shadow-ratio conventions when determining the beginning of Asr. The selected Shafi method uses a shadow factor of 1. Users can switch between Shafi, Maliki, Hanbali and Hanafi methods."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    HorizontalDivider(color = IslamicEmeraldPrimary.copy(alpha = 0.2f))

                    Text(
                        text = if (isBangla) {
                            "সহীহ হাদিসে বর্ণিত নামাজের ওয়াক্তসমূহ, যার মধ্যে জিবরীল (আঃ) এর মহানবী ﷺ কে বিভিন্ন সময়ে নামাজের ইমামতি করানোর বর্ণনা রয়েছে (সহীহ মুসলিম ৬১২, জামে আত-তিরমিযী ১৪৯), সেগুলোর আলোকে ফুকাহায়ে কেরাম ফিকহি ব্যাখ্যা বিশ্লেষণ করে আসরের সময় নির্ধারণের এসব স্বীকৃত পদ্ধতি গ্রহণ করেছেন। এটি একটি ফিকহি গণনা পদ্ধতি (Juristic Calculation Method)।"
                        } else {
                            "The authentic narrations describe prayer times, including the reports about Jibril (peace be upon him) leading the Prophet ﷺ in prayer at different times (Sahih Muslim 612, Jami` at-Tirmidhi 149). Classical jurists interpreted these evidences and developed different recognized methods for determining the beginning of Asr as Juristic Calculation Methods."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3. Today's Computed Timeline Table
            prayerTimes?.let { pt ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isBangla) "আজকের গণনাকৃত সঠিক সময়মালা" else "Today's Calculated Exact Times",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        val rows = listOf(
                            Pair(if (isBangla) "ফজর (Fajr)" else "Fajr", pt.fajr.toString()),
                            Pair(if (isBangla) "সূর্যোদয় (Sunrise)" else "Sunrise", pt.sunrise.toString()),
                            Pair(if (isBangla) "যোহর (Dhuhr)" else "Dhuhr", pt.dhuhr.toString()),
                            Pair(if (isBangla) "আসর (${settings.asrMethod.nameEn})" else "Asr (${settings.asrMethod.nameEn})", pt.asr.toString()),
                            Pair(if (isBangla) "সূর্যাস্ত (Sunset)" else "Sunset", pt.sunset.toString()),
                            Pair(if (isBangla) "মাগরিব (Maghrib)" else "Maghrib", pt.maghrib.toString()),
                            Pair(if (isBangla) "এশা (Isha)" else "Isha", pt.isha.toString())
                        )

                        rows.forEachIndexed { idx, pair ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pair.first,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isBangla) SinglePrayerTime.toBanglaNumerals(pair.second) else pair.second,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicEmeraldPrimary
                                )
                            }
                            if (idx < rows.size - 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculationInfoRow(
    label: String,
    value: String,
    highlight: Boolean = false,
    isBangla: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.weight(1.1f)
        )
        Text(
            text = if (isBangla) SinglePrayerTime.toBanglaNumerals(value) else value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlight) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (highlight) IslamicGoldDark else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.3f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
