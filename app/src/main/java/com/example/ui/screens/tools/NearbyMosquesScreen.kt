package com.example.ui.screens.tools

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LocationData
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyMosquesScreen(
    location: LocationData,
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun openMosquesOnMap() {
        val geoUri = "geo:${location.latitude},${location.longitude}?q=mosque"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val webUri = "https://www.google.com/maps/search/mosque/@${location.latitude},${location.longitude},15z"
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUri)))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "নিকটস্থ মসজিদ" else "Nearby Mosques",
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
            verticalArrangement = Arrangement.Center
        ) {
            Text("🕌", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isBangla) "আপনার আশপাশের মসজিদ খুঁজুন" else "Find Mosques Near You",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isBangla) "বর্তমান অবস্থান (${location.displayLocation(isBangla)}) এর চারপাশে অবস্থিত মসজিদগুলো গুগল ম্যাপসে এক ক্লিকে দেখুন।"
                else "Locate mosques surrounding your current coordinates (${location.cityName}) using Google Maps.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = IslamicMutedText,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { openMosquesOnMap() },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IslamicEmeraldPrimary),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(imageVector = Icons.Default.Map, contentDescription = "Maps")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "ম্যাপসে মসজিদসমূহ দেখুন" else "Open in Maps",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
