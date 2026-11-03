package com.example.ui.screens.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AllahNamesData
import com.example.model.AllahName
import com.example.ui.theme.*

private val DeepEmeraldCard = Color(0xFF0F241A)
private val LightEmeraldCard = Color(0xFFFFFFFF)
private val DeepEmeraldSurface = Color(0xFF081912)
private val GoldAccent = Color(0xFFD4AF37)
private val SoftGold = Color(0xFFF3E5AB)
private val MutedTextLight = Color(0xFF7A8B80)
private val MutedTextDark = Color(0xFFA5B8AD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllahNamesScreen(
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("all") }
    var isGridView by rememberSaveable { mutableStateOf(false) }
    var selectedName by remember { mutableStateOf<AllahName?>(null) }
    var favoriteIds by rememberSaveable { mutableStateOf(setOf<Int>()) }

    BackHandler {
        if (selectedName != null) {
            selectedName = null
        } else {
            onBack()
        }
    }

    val allNames = AllahNamesData.names

    val categories = remember(isBangla) {
        listOf(
            "all" to if (isBangla) "সকল নাম (৯৯)" else "All Names (99)",
            "fav" to if (isBangla) "পছন্দের তালিকা" else "Favorites",
            "রহমত" to if (isBangla) "রহমত" else "Mercy",
            "ক্ষমতা" to if (isBangla) "ক্ষমতা" else "Power",
            "জ্ঞান" to if (isBangla) "জ্ঞান" else "Knowledge",
            "ক্ষমা" to if (isBangla) "ক্ষমা" else "Forgiveness",
            "রিযিক" to if (isBangla) "রিযিক" else "Sustenance",
            "মর্যাদা" to if (isBangla) "মর্যাদা" else "Majesty",
            "সৃষ্টি" to if (isBangla) "সৃষ্টি" else "Creation",
            "ন্যায়বিচার" to if (isBangla) "ইনসাফ ও বিচার" else "Justice"
        )
    }

    val filteredList = remember(searchQuery, selectedCategory, favoriteIds) {
        var result = if (searchQuery.isBlank()) {
            allNames
        } else {
            AllahNamesData.search(searchQuery)
        }

        result = when (selectedCategory) {
            "all" -> result
            "fav" -> result.filter { it.id in favoriteIds }
            else -> result.filter { it.category == selectedCategory }
        }

        result
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) "আল্লাহর ৯৯টি পবিত্র নাম" else "99 Names of Allah",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = IslamicGold
                        )
                        Text(
                            text = if (isBangla) "আসমাউল হুসনা • অর্থ ও তাৎপর্যসহ" else "Asmaul Husna • With Meanings & Virtues",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_allah_names")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isBangla) "ফিরে যান" else "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isGridView = !isGridView },
                        modifier = Modifier.testTag("btn_toggle_view_mode")
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription = if (isGridView) "List View" else "Grid View",
                            tint = IslamicGold
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
        ) {
            // Header Hadith Card
            HadithHeaderCard(
                isBangla = isBangla,
                totalCount = allNames.size,
                filteredCount = filteredList.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("search_allah_names"),
                placeholder = {
                    Text(
                        text = if (isBangla) "নাম, উচ্চারণ বা অর্থ দিয়ে খুঁজুন..." else "Search by name, meaning or number...",
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = IslamicGold
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IslamicGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { (key, label) ->
                    val isSelected = selectedCategory == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = key },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (key == "fav") {
                            {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IslamicEmeraldDark,
                            selectedLabelColor = IslamicGold,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) IslamicGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            // Results count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) {
                        "মোট নাম: ${toBanglaDigits(filteredList.size)} টি"
                    } else {
                        "Showing ${filteredList.size} names"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (favoriteIds.isNotEmpty()) {
                    Text(
                        text = if (isBangla) "পছন্দ: ${toBanglaDigits(favoriteIds.size)} টি" else "Favorites: ${favoriteIds.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = IslamicGold,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Content List or Grid
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(54.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = if (isBangla) "কোনো নাম পাওয়া যায়নি" else "No names found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isBangla) "অনুগ্রহ করে অন্য শব্দ বা ক্যাটাগরি দিয়ে চেষ্টা করুন।" else "Try searching with a different keyword or category.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        AllahNameGridCard(
                            item = item,
                            isBangla = isBangla,
                            isFavorite = item.id in favoriteIds,
                            onToggleFavorite = {
                                favoriteIds = if (item.id in favoriteIds) {
                                    favoriteIds - item.id
                                } else {
                                    favoriteIds + item.id
                                }
                            },
                            onClick = { selectedName = item }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        AllahNameListCard(
                            item = item,
                            isBangla = isBangla,
                            isFavorite = item.id in favoriteIds,
                            onToggleFavorite = {
                                favoriteIds = if (item.id in favoriteIds) {
                                    favoriteIds - item.id
                                } else {
                                    favoriteIds + item.id
                                }
                            },
                            onClick = { selectedName = item }
                        )
                    }
                }
            }
        }
    }

    // Detail Dialog
    selectedName?.let { nameItem ->
        AllahNameDetailDialog(
            item = nameItem,
            isBangla = isBangla,
            isFavorite = nameItem.id in favoriteIds,
            onToggleFavorite = {
                favoriteIds = if (nameItem.id in favoriteIds) {
                    favoriteIds - nameItem.id
                } else {
                    favoriteIds + nameItem.id
                }
            },
            onDismiss = { selectedName = null }
        )
    }
}

@Composable
private fun HadithHeaderCard(
    isBangla: Boolean,
    totalCount: Int,
    filteredCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = IslamicEmeraldDark
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            IslamicEmeraldDark,
                            Color(0xFF0F3A26)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = IslamicGold.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = IslamicGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isBangla) "পবিত্র হাদিসের সুসংবাদ" else "Authentic Hadith Glad Tidings",
                        fontWeight = FontWeight.Bold,
                        color = IslamicGold,
                        fontSize = 14.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = IslamicGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = if (isBangla) "৯৯টি নাম" else "99 Names",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "إِنَّ لِلَّهِ تِسْعَةً وَتِسْعِينَ اسْمًا، مَنْ أَحْصَاهَا دَخَلَ الْجَنَّةَ",
                fontSize = 16.sp,
                color = SoftGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isBangla) {
                    "রাসূলুল্লাহ (ﷺ) বলেছেন: \"নিশ্চয়ই আল্লাহর নিরানব্বইটি নাম রয়েছে; যে ব্যক্তি এগুলো সংরক্ষণ/মুখস্থ করবে, সে জান্নাতে প্রবেশ করবে।\" (সহীহ বুখারী ২৭৩৬, সহীহ মুসলিম ২৬৭৭)"
                } else {
                    "The Messenger of Allah (ﷺ) said: \"Allah has ninety-nine names; whoever preserves and memorizes them will enter Paradise.\" (Sahih Bukhari 2736, Muslim 2677)"
                },
                fontSize = 12.sp,
                color = Color(0xFFD6E4DB),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun AllahNameListCard(
    item: AllahName,
    isBangla: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("allah_name_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Number badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(IslamicEmeraldDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) toBanglaDigits(item.id) else item.id.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBangla) item.transliterationBn else item.transliterationEn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = IslamicEmeraldPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = item.transliterationEn,
                            fontSize = 11.sp,
                            color = IslamicGoldDark,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (isBangla) item.meaningBn else item.meaningEn,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Arabic text in large font
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = item.arabic,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGold,
                    textAlign = TextAlign.End
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AllahNameGridCard(
    item: AllahName,
    isBangla: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("allah_name_grid_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = IslamicEmeraldDark,
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isBangla) toBanglaDigits(item.id) else item.id.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGold
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Arabic text prominently
            Text(
                text = item.arabic,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isBangla) item.transliterationBn else item.transliterationEn,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (isBangla) item.meaningBn else item.meaningEn,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun AllahNameDetailDialog(
    item: AllahName,
    isBangla: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current
    var dhikrCount by rememberSaveable { mutableIntStateOf(0) }
    var targetDhikr by rememberSaveable { mutableIntStateOf(33) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IslamicEmeraldDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = if (isBangla) "পবিত্র নাম #${toBanglaDigits(item.id)}" else "Holy Name #${item.id}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                val shareText = buildString {
                                    append("আল্লাহর পবিত্র নাম #${item.id}\n")
                                    append("${item.arabic}\n")
                                    append("উচ্চারণ: ${item.transliterationBn} (${item.transliterationEn})\n")
                                    append("অর্থ: ${item.meaningBn}\n\n")
                                    append("তাৎপর্য ও ফযিলত:\n${item.fazilatBn}\n\n")
                                    append("— সুলতান ইসলামিক প্রেয়ার অ্যাপ")
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Holy Name"))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                val textToCopy = "${item.arabic} — ${item.transliterationBn} (${item.transliterationEn})\nঅর্থ: ${item.meaningBn}\n${item.fazilatBn}"
                                clipboardManager.setText(AnnotatedString(textToCopy))
                                Toast.makeText(
                                    context,
                                    if (isBangla) "কপি করা হয়েছে" else "Copied to clipboard",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hero Arabic Calligraphy Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = IslamicEmeraldDark
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        IslamicEmeraldDark,
                                        Color(0xFF0F3222)
                                    )
                                )
                            )
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.arabic,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftGold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.transliterationBn,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = item.transliterationEn,
                            fontSize = 14.sp,
                            color = IslamicGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Meaning Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = IslamicGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isBangla) "পবিত্র নামের অর্থ" else "Meaning of Holy Name",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = IslamicGold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.meaningBn,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.meaningEn,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Significance / Fazilat Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = IslamicGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isBangla) "তাৎপর্য ও ফযিলত" else "Significance & Virtues",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = IslamicGold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.fazilatBn,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dhikr Practice Counter
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = IslamicEmeraldDark.copy(alpha = 0.85f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "এই নামের জিকির করুন" else "Recite this Name (Dhikr)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SoftGold
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(33, 100).forEach { target ->
                                    val isSelected = targetDhikr == target
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) IslamicGold else Color.White.copy(alpha = 0.1f),
                                        modifier = Modifier.clickable { targetDhikr = target }
                                    ) {
                                        Text(
                                            text = "${target}x",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Big Counter Tap Area
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            IslamicGold,
                                            IslamicGoldDark
                                        )
                                    )
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dhikrCount++
                                }
                                .testTag("btn_name_dhikr_tap"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isBangla) toBanglaDigits(dhikrCount) else dhikrCount.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B2A20)
                                )
                                Text(
                                    text = if (isBangla) "স্পর্শ করুন" else "Tap",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1B2A20)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) {
                                    "লক্ষ্য: ${toBanglaDigits(targetDhikr)} বার (${toBanglaDigits(dhikrCount)} সম্পন্ন)"
                                } else {
                                    "Goal: $targetDhikr ($dhikrCount completed)"
                                },
                                fontSize = 12.sp,
                                color = SoftGold
                            )

                            TextButton(
                                onClick = { dhikrCount = 0 }
                            ) {
                                Text(
                                    text = if (isBangla) "রিসেট" else "Reset",
                                    color = SoftGold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicEmeraldDark
                    )
                ) {
                    Text(
                        text = if (isBangla) "বন্ধ করুন" else "Close",
                        color = IslamicGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun toBanglaDigits(number: Int): String {
    val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    val str = number.toString()
    val builder = StringBuilder()
    for (ch in str) {
        if (ch in '0'..'9') {
            builder.append(banglaDigits[ch - '0'])
        } else {
            builder.append(ch)
        }
    }
    return builder.toString()
}
