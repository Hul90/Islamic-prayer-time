package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun openEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:sultanmahamud5497@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "Islamic Prayer Times Feedback")
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun dialPhone() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:01740236384")
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "অ্যাপ সম্পর্কে" else "About App",
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Branding Icon & Title
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(IslamicEmeraldPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🕌", fontSize = 40.sp)
            }

            Text(
                text = if (isBangla) "ইসলামিক নামাজের সময় ও সুলতান টুলস" else "Islamic Prayer Times & Sultan Tools",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Version 1.0.0 (Build 100) • Offline-First Engine",
                style = MaterialTheme.typography.bodySmall,
                color = IslamicMutedText
            )

            // Developer Card
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
                        text = if (isBangla) "ডেভেলপারের তথ্য" else "Developer Information",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = IslamicEmeraldPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "MD SULTAN MAHAMUD",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isBangla) "অ্যান্ড্রয়েড অ্যাপ্লিকেশন নির্মাতা ও সফটওয়্যার ডেভেলপার" else "Android Application Engineer",
                        style = MaterialTheme.typography.bodySmall,
                        color = IslamicMutedText
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Email Row (clickable)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { openEmail() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = IslamicEmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBangla) "ইমেইল (যোগাযোগ করতে ট্যাপ করুন)" else "Email (Tap to send email)",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                            Text(
                                text = "sultanmahamud5497@gmail.com",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Phone Row (clickable)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { dialPhone() }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", tint = IslamicGoldDark)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBangla) "মোবাইল নাম্বার (কল করতে ট্যাপ করুন)" else "Phone / WhatsApp (Tap to call)",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                            Text(
                                text = "01740-236384",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Key Highlights Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isBangla) "অ্যাপের প্রধান বৈশিষ্ট্যসমূহ" else "Core Highlights",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val points = if (isBangla) listOf(
                        "🔒 সম্পূর্ণ অফলাইন ও গোপনীয়তা বান্ধব - ইন্টারনেট ছাড়াই সকল হিসাব নিখুঁতভাবে চলে।",
                        "🕌 সুমধুর আজান অ্যালার্ট ও পুশ নোটিফিকেশন সুবিধা।",
                        "🧭 রিয়েল-টাইম কিবলা কম্পাস ও মক্কা দূরত্ব নির্দেশক।",
                        "📅 ৩-ইন-১ ক্যালেন্ডার (ইংরেজি, বাংলা ও হিজরি) ও সরকারি ছুটির তালিকা।",
                        "📿 ডিজিটাল তাসবিহ ও দৈনিক ৫ ওয়াক্ত নামাজ ট্র্যাকার।"
                    ) else listOf(
                        "🔒 100% Offline-First - All astronomical calculations done on-device.",
                        "🕌 User-provided Azan MP3 playback at prayer time with alarm notifications.",
                        "🧭 Real-time sensory Qibla compass & distance to Kaaba.",
                        "📅 3-in-1 Unified Calendar (Gregorian, Bangla, Hijri) + National Holidays.",
                        "📿 Digital Tasbih & Daily 5-Prayer habit tracker."
                    )

                    points.forEach { pt ->
                        Text(
                            text = pt,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Text(
                text = "Made with ❤️ for the Muslim Ummah by MD SULTAN MAHAMUD",
                style = MaterialTheme.typography.labelSmall,
                color = IslamicMutedText,
                textAlign = TextAlign.Center
            )
        }
    }
}
