package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.AppLanguage
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    isBangla: Boolean,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(if (isBangla) AppLanguage.BANGLA else AppLanguage.ENGLISH) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.requestAutoLocation()
        viewModel.setLanguage(selectedLanguage)
        viewModel.completeOnboarding()
        onFinish()
    }

    fun requestPermissionsAndFinish() {
        val perms = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(perms.toTypedArray())
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        IslamicEmeraldPrimary,
                        IslamicEmeraldDark,
                        Color(0xFF022B1E)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🕌", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (selectedLanguage == AppLanguage.BANGLA) "ইসলামিক নামাজের সময় ও আজান" else "Islamic Prayer Times & Azan",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (selectedLanguage == AppLanguage.BANGLA) "সঠিক নামাজের ওয়াক্ত, আজান অ্যালার্ট, কিবলা কম্পাস ও ৩-ইন-১ ক্যালেন্ডার"
                else "Accurate offline prayer times, Azan push alerts, Qibla compass & 3-in-1 calendar",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Language Selection Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ভাষা নির্বাচন করুন / Select Language",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedLanguage = AppLanguage.BANGLA
                                viewModel.setLanguage(AppLanguage.BANGLA)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedLanguage == AppLanguage.BANGLA) IslamicGold else Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = "বাংলা",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedLanguage == AppLanguage.BANGLA) Color.Black else Color.White
                            )
                        }

                        Button(
                            onClick = {
                                selectedLanguage = AppLanguage.ENGLISH
                                viewModel.setLanguage(AppLanguage.ENGLISH)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedLanguage == AppLanguage.ENGLISH) IslamicGold else Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = "English",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedLanguage == AppLanguage.ENGLISH) Color.Black else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { requestPermissionsAndFinish() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IslamicGold, contentColor = Color.Black)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (selectedLanguage == AppLanguage.BANGLA) "শুরু করুন" else "Get Started",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Start")
                }
            }
        }
    }
}
