package com.example.ui.screens.tools

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGoldDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineDuasScreen(
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf<OfflineDuaItem?>(null) }
    var query by remember { mutableStateOf("") }

    if (selected != null) {
        OfflineDuaDetailScreen(
            item = selected!!,
            isBangla = isBangla,
            onBack = { selected = null }
        )
        return
    }

    val filtered = remember(query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) OFFLINE_DUA_LIST
        else OFFLINE_DUA_LIST.filter { it.title.lowercase().contains(q) || it.id.toString() == q }
    }

    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        if (isBangla) "প্রতিদিনের ব্যবহারিক দোয়া" else "Practical Daily Duas",
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldDark
                    )
                    Text(
                        if (isBangla) "অফলাইন • ইন্টারনেট ছাড়াই পড়ুন" else "Offline • Read without internet",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text(if (isBangla) "দোয়া খুঁজুন" else "Search duas") },
            placeholder = { Text(if (isBangla) "যেমন: ঘুম, নামাজ, সফর..." else "e.g. sleep, prayer, travel...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IslamicGoldDark,
                focusedLabelColor = IslamicGoldDark,
                cursorColor = IslamicGoldDark
            )
        )

        Text(
            text = if (isBangla) "মোট ${OFFLINE_DUA_LIST.size}টি অফলাইন দোয়া/দোয়া-অধ্যায়" else "${OFFLINE_DUA_LIST.size} offline dua/dua sections",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            color = IslamicGoldDark,
            fontWeight = FontWeight.SemiBold
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            items(filtered, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable { selected = item },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = IslamicEmeraldDark.copy(alpha = 0.22f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("${item.id}", color = IslamicGoldDark, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(
                            item.title,
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text("›", fontSize = 28.sp, color = IslamicGoldDark)
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
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(item.title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = IslamicGoldDark)
        }
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                WebView(context).apply {
                    settings.javaScriptEnabled = false
                    settings.domStorageEnabled = false
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    webViewClient = WebViewClient()
                    loadUrl("file:///android_asset/dua_offline/${item.asset}")
                }
            },
            update = { view ->
                val target = "file:///android_asset/dua_offline/${item.asset}"
                if (view.url != target) view.loadUrl(target)
            }
        )
    }
}
