package com.example.ui.screens.tools

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SinglePrayerTime
import com.example.ui.theme.*
import com.example.ui.viewmodel.SultanToolsViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    viewModel: SultanToolsViewModel,
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val qiblaState by viewModel.qiblaState.collectAsState()

    DisposableEffect(Unit) {
        viewModel.startCompass()
        onDispose {
            viewModel.stopCompass()
        }
    }

    // Smooth compass rotation animation
    val animatedHeading by animateFloatAsState(
        targetValue = qiblaState.currentHeading,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "heading"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "কিবলা কম্পাস" else "Qibla Compass",
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (qiblaState.isAligned) IslamicEmeraldPrimary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isBangla) "কিবলার কোণ" else "Qibla Bearing",
                            style = MaterialTheme.typography.bodySmall,
                            color = IslamicMutedText
                        )
                        val angle = "${qiblaState.qiblaBearing.roundToInt()}°"
                        Text(
                            text = if (isBangla) SinglePrayerTime.toBanglaNumerals(angle) else angle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    VerticalDivider(modifier = Modifier.height(36.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isBangla) "মক্কা থেকে দূরত্ব" else "Distance to Makkah",
                            style = MaterialTheme.typography.bodySmall,
                            color = IslamicMutedText
                        )
                        val dist = "${qiblaState.distanceKm.roundToInt()} km"
                        Text(
                            text = if (isBangla) SinglePrayerTime.toBanglaNumerals(dist) else dist,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldDark
                        )
                    }
                }
            }

            // 2. Compass Dial with Animated Qibla Needle
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background compass rose
                CompassDial(
                    heading = animatedHeading,
                    qiblaBearing = qiblaState.qiblaBearing.toFloat(),
                    isAligned = qiblaState.isAligned
                )

                // Central Alignment Indicator
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (qiblaState.isAligned) IslamicEmeraldPrimary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🕋",
                        fontSize = 24.sp
                    )
                }
            }

            // 3. Status & Alignment Feedback
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (qiblaState.isAligned) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = IslamicEmeraldPrimary
                    ) {
                        Text(
                            text = if (isBangla) "✓ আপনি কাবার মুখোমুখি হয়েছেন" else "✓ You are facing the Holy Kaaba",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (isBangla) "কম্পাসের তীরটি সবুজ চিহ্নের সাথে মিলান" else "Align the needle with the green indicator",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isBangla) "সঠিক ফলাফলের জন্য ফোনটি সমতল রাখুন এবং প্রয়োজনে '8' আকারে ঘুরান" else "Hold phone flat & wave in figure-8 if compass needs calibration",
                    style = MaterialTheme.typography.labelSmall,
                    color = IslamicMutedText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun CompassDial(
    heading: Float,
    qiblaBearing: Float,
    isAligned: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f

        // Outer Ring
        drawCircle(
            color = if (isAligned) IslamicEmeraldPrimary else Color(0xFFBDBDBD),
            radius = radius,
            center = center,
            style = Stroke(width = 4.dp.toPx())
        )

        // Inner glowing ring
        drawCircle(
            color = if (isAligned) IslamicEmeraldPrimary.copy(alpha = 0.15f) else Color.Transparent,
            radius = radius - 10.dp.toPx()
        )

        // Draw ticks rotated by heading
        rotate(-heading, pivot = center) {
            // Cardinal direction marks
            for (i in 0 until 360 step 15) {
                val tickLength = if (i % 90 == 0) 18.dp.toPx() else if (i % 45 == 0) 12.dp.toPx() else 6.dp.toPx()
                val tickStroke = if (i % 90 == 0) 3.dp.toPx() else 1.5.dp.toPx()
                val tickColor = if (i == 0) Color(0xFFD32F2F) else Color(0xFF757575)

                rotate(i.toFloat(), pivot = center) {
                    drawLine(
                        color = tickColor,
                        start = Offset(center.x, center.y - radius + 4.dp.toPx()),
                        end = Offset(center.x, center.y - radius + 4.dp.toPx() + tickLength),
                        strokeWidth = tickStroke
                    )
                }
            }

            // Qibla Target Marker on dial
            rotate(qiblaBearing, pivot = center) {
                // Gold / Green indicator arrow pointing outwards
                val markerPath = Path().apply {
                    moveTo(center.x, center.y - radius + 6.dp.toPx())
                    lineTo(center.x - 12.dp.toPx(), center.y - radius + 26.dp.toPx())
                    lineTo(center.x + 12.dp.toPx(), center.y - radius + 26.dp.toPx())
                    close()
                }
                drawPath(markerPath, color = if (isAligned) IslamicEmeraldPrimary else IslamicGoldDark)
            }
        }

        // Phone Heading Needle (Always points up in device frame)
        val needlePath = Path().apply {
            moveTo(center.x, center.y - radius + 32.dp.toPx())
            lineTo(center.x - 8.dp.toPx(), center.y)
            lineTo(center.x + 8.dp.toPx(), center.y)
            close()
        }
        drawPath(needlePath, color = if (isAligned) IslamicEmeraldPrimary else Color(0xFFD32F2F))
    }
}
