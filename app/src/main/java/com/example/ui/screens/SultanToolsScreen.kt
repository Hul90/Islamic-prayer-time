package com.example.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class SultanToolMenuItem(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val icon: ImageVector,
    val route: String,
    val iconBgColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SultanToolsScreen(
    isBangla: Boolean,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val toolList = listOf(
        SultanToolMenuItem(
            "namaz_learn",
            "Authentic Namaz Guide",
            "সহীহ নামাজ শিক্ষা ও হাদীস দলিল",
            "Step-by-step prayer guide with verified Hadith references",
            "হাদীস রেফারেন্স সহ নামাজের নিয়ম ও দোয়া",
            Icons.Default.MenuBook,
            "tool_namaz_learning",
            IslamicEmeraldPrimary
        ),
        SultanToolMenuItem(
            "duas",
            "All Duas & Zikr — Contents",
            "সকল দোয়া ও যিকির — সূচিপত্র",
            "Complete Dua & Zikr index with Arabic, Bangla and English",
            "হিসনুল মুসলিমের সম্পূর্ণ দোয়া ও যিকিরের সূচিপত্র",
            Icons.Default.VolunteerActivism,
            "tool_duas",
            IslamicGoldDark
        ),
        SultanToolMenuItem(
            "offline_daily_duas",
            "Practical Daily Duas",
            "প্রতিদিনের ব্যবহারিক দোয়া",
            "Native offline dua reader with 160+ dua sections",
            "ইন্টারনেট ছাড়াই পড়ার জন্য অ্যাপের ভিতরে সংরক্ষিত ১৬০+ দোয়া ও অধ্যায়",
            Icons.Default.Bookmark,
            "tool_offline_duas",
            IslamicGoldDark
        ),
        SultanToolMenuItem(
            "quran",
            "AL QURAN",
            "আল কুরআন",
            "Read the Quran with Arabic text and Bangla translation",
            "আরবি কুরআন ও বাংলা অনুবাদ পড়ুন",
            Icons.Default.MenuBook,
            "tool_quran",
            IslamicEmeraldPrimary
        ),
        SultanToolMenuItem(
            "qibla",
            "Qibla Finder",
            "কিবলা কম্পাস",
            "Real-time sensor compass pointing directly to Kaaba",
            "সরাসরি কাবার দিক নির্দেশক সেন্সর কম্পাস",
            Icons.Default.Explore,
            "tool_qibla",
            IslamicEmeraldPrimary
        ),
        SultanToolMenuItem(
            "tasbih",
            "Digital Tasbih",
            "ডিজিটাল তাসবিহ",
            "Count your dhikr with vibration & goal tracking",
            "জিকির গণনা, লক্ষ্য নির্ধারণ ও ভাইব্রেশন সুবিধা",
            Icons.Default.TouchApp,
            "tool_tasbih",
            IslamicGoldDark
        ),
        SultanToolMenuItem(
            "tracker",
            "Daily Prayer Tracker",
            "দৈনিক নামাজ ট্র্যাকার",
            "Track daily 5 prayers and view monthly streak",
            "দৈনিক ৫ ওয়াক্ত নামাজের হিসাব রাখুন",
            Icons.Default.FactCheck,
            "tool_tracker",
            Color(0xFF2E7D32)
        ),
        SultanToolMenuItem(
            "converter",
            "3-Way Date Converter",
            "তারিখ কনভার্টার (৩-ইন-১)",
            "Convert between Gregorian, Hijri & Bangla calendars",
            "ইংরেজি, হিজরি ও বাংলা তারিখ রূপান্তর করুন",
            Icons.Default.SyncAlt,
            "tool_converter",
            Color(0xFF00838F)
        ),
        SultanToolMenuItem(
            "zakat",
            "Zakat Calculator",
            "যাকাত ক্যালকুলেটর",
            "Calculate your 2.5% Zakat based on Nisab value",
            "নিসাব ও সম্পদের ভিত্তিতে সঠিক যাকাত হিসাব করুন",
            Icons.Default.Calculate,
            "tool_zakat",
            Color(0xFF6A1B9A)
        ),
        SultanToolMenuItem(
            "dhikr_timer",
            "Dhikr & Meditation Timer",
            "জিকির ও মোরাকাবা টাইমার",
            "Timed peaceful sessions with subtle reminders",
            "নির্দিষ্ট সময়ের জিকির ও নিরব ইবাদতের টাইমার",
            Icons.Default.HourglassBottom,
            "tool_dhikr_timer",
            Color(0xFF0288D1)
        ),
        SultanToolMenuItem(
            "mosques",
            "Nearby Mosques",
            "নিকটস্থ মসজিদ",
            "Locate mosques around your current location",
            "আপনার আশপাশের মসজিদসমূহ খুঁজে নিন",
            Icons.Default.Mosque,
            "tool_mosques",
            Color(0xFFD84315)
        ),
        SultanToolMenuItem(
            "ramadan",
            "Ramadan Mode & Duas",
            "রমজান মোড ও দোয়া",
            "Sehri/Iftar countdowns, Duas & 30-day schedule",
            "সেহরি-ইফতারের সময়সূচি ও প্রয়োজনীয় দোয়া",
            Icons.Default.Bedtime,
            "ramadan_mode",
            IslamicEmeraldDark
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IslamicEmeraldPrimary.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🛠️", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (isBangla) "সুলতান ইসলামিক টুলস" else "Sultan Islamic Tools",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IslamicEmeraldPrimary
                        )
                        Text(
                            text = if (isBangla) "আপনার ইবাদত ও দ্বীনি জিন্দেগিকে সহজ করতে প্রয়োজনীয় টুলস" else "Essential offline tools for your daily Islamic life",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        items(toolList) { tool ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onNavigateTo(tool.route) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(tool.iconBgColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.titleEn,
                            tint = tool.iconBgColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBangla) tool.titleBn else tool.titleEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isBangla) tool.descriptionBn else tool.descriptionEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = IslamicMutedText
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = IslamicMutedText
                    )
                }
            }
        }
    }
}
