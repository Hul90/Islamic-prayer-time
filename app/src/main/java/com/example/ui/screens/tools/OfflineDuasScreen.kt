package com.example.ui.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicAccentGreen
import com.example.ui.theme.IslamicGold

private val ReaderSurface = Color(0xFF122019)
private val ReaderCard = Color(0xFF172820)
private val ReaderArabic = Color(0xFFF4E8C1)
private val ReaderBody = Color(0xFFE4EAE5)
private val ReaderSecondary = Color(0xFFB9C9BE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineDuasScreen(
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val offlineDuas = remember(context) { loadOfflineDuas(context) }
    var selectedDua by remember { mutableStateOf<OfflineDuaItem?>(null) }
    var searchText by remember { mutableStateOf("") }

    val selected = selectedDua
    if (selected != null) {
        OfflineDuaDetailScreen(
            item = selected,
            isBangla = isBangla,
            onBack = { selectedDua = null }
        )
        return
    }

    val search = searchText.trim().lowercase()
    val filteredDuas = remember(offlineDuas, search) {
        if (search.isEmpty()) {
            offlineDuas
        } else {
            offlineDuas.filter { dua ->
                dua.title.lowercase().contains(search) ||
                    dua.id.toString() == search ||
                    dua.blocks.any { block -> block.text.lowercase().contains(search) }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = if (isBangla) "প্রতিদিনের ব্যবহারিক দোয়া" else "Practical Daily Duas",
                        fontWeight = FontWeight.Bold,
                        color = IslamicGold
                    )
                    Text(
                        text = if (isBangla) "সম্পূর্ণ অফলাইন • ইন্টারনেট ছাড়াই পড়ুন" else "Fully offline • No internet required",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = if (isBangla) "ফিরে যান" else "Back"
                    )
                }
            }
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { newValue -> searchText = newValue },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            label = {
                Text(if (isBangla) "দোয়া খুঁজুন" else "Search duas")
            },
            placeholder = {
                Text(if (isBangla) "যেমন: ঘুম, নামাজ, সফর..." else "e.g. sleep, prayer, travel...")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IslamicGold,
                focusedLabelColor = IslamicGold,
                cursorColor = IslamicGold
            )
        )

        Text(
            text = if (isBangla) "${filteredDuas.size}টি দোয়া/অধ্যায়" else "${filteredDuas.size} duas/chapters",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            color = IslamicGold,
            fontWeight = FontWeight.SemiBold
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            items(
                items = filteredDuas,
                key = { dua -> dua.id }
            ) { dua ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedDua = dua },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = IslamicAccentGreen.copy(alpha = 0.20f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = dua.id.toString(),
                                    color = IslamicGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = dua.title,
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text("›", fontSize = 28.sp, color = IslamicGold)
                    }
                }
            }
        }
    }
}

@Composable
private fun OfflineDuaDetailScreen(
    item: OfflineDuaItem,
    isBangla: Boolean,
    onBack: () -> Unit
) {
    var fontScale by remember { mutableStateOf(1f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ReaderSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = if (isBangla) "ফিরে যান" else "Back",
                    tint = ReaderBody
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    color = ReaderBody,
                    maxLines = 2
                )
                Text(
                    text = if (isBangla) "অফলাইন দোয়া • ${item.id}" else "Offline Dua • ${item.id}",
                    fontSize = 12.sp,
                    color = ReaderSecondary
                )
            }
            IconButton(
                onClick = { fontScale = (fontScale - 0.1f).coerceAtLeast(0.8f) }
            ) {
                Icon(Icons.Default.TextDecrease, contentDescription = "Smaller text", tint = ReaderBody)
            }
            IconButton(
                onClick = { fontScale = (fontScale + 0.1f).coerceAtMost(1.5f) }
            ) {
                Icon(Icons.Default.TextIncrease, contentDescription = "Larger text", tint = ReaderBody)
            }
        }

        HorizontalDivider(color = Color(0xFF294238))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = item.blocks,
                key = { block -> "${block.type}:${block.text.hashCode()}" }
            ) { block ->
                when (block.type) {
                    "arabic" -> DuaBlockCard(
                        label = if (isBangla) "আরবি" else "Arabic",
                        text = block.text.removePrefix("আরবি:").trim(),
                        background = Color(0xFF1B3026),
                        textColor = ReaderArabic,
                        fontSize = (24f * fontScale).sp,
                        alignment = TextAlign.Right
                    )
                    "pronunciation" -> DuaBlockCard(
                        label = if (isBangla) "বাংলা উচ্চারণ" else "Pronunciation",
                        text = block.text.removePrefix("উচ্চারণ:").trim(),
                        background = ReaderCard,
                        textColor = ReaderBody,
                        fontSize = (17f * fontScale).sp,
                        alignment = TextAlign.Start
                    )
                    "meaning" -> DuaBlockCard(
                        label = if (isBangla) "বাংলা অর্থ" else "Meaning",
                        text = block.text.removePrefix("অর্থ:").trim(),
                        background = ReaderCard,
                        textColor = ReaderBody,
                        fontSize = (17f * fontScale).sp,
                        alignment = TextAlign.Start
                    )
                    "reference" -> DuaBlockCard(
                        label = if (isBangla) "সূত্র" else "Reference",
                        text = block.text,
                        background = Color(0xFF182A22),
                        textColor = ReaderSecondary,
                        fontSize = (14f * fontScale).sp,
                        alignment = TextAlign.Start
                    )
                    else -> Text(
                        text = block.text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        color = ReaderBody,
                        fontSize = (16f * fontScale).sp,
                        lineHeight = (25f * fontScale).sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DuaBlockCard(
    label: String,
    text: String,
    background: Color,
    textColor: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    alignment: TextAlign
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label,
                color = IslamicGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                color = textColor,
                fontSize = fontSize,
                lineHeight = (fontSize.value * 1.65f).sp,
                textAlign = alignment
            )
        }
    }
}
