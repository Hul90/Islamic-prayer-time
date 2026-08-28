package com.example.ui.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SinglePrayerTime
import com.example.ui.theme.*
import com.example.ui.viewmodel.SultanToolsViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatCalculatorScreen(
    viewModel: SultanToolsViewModel,
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.zakatState.collectAsState()
    val decFormat = remember { DecimalFormat("#,##,###.##") }

    var cashInput by remember { mutableStateOf(if (state.cashInHand > 0) state.cashInHand.toString() else "") }
    var bankInput by remember { mutableStateOf(if (state.bankSavings > 0) state.bankSavings.toString() else "") }
    var goldGramsInput by remember { mutableStateOf(if (state.goldGrams > 0) state.goldGrams.toString() else "") }
    var silverGramsInput by remember { mutableStateOf(if (state.silverGrams > 0) state.silverGrams.toString() else "") }
    var businessInput by remember { mutableStateOf(if (state.businessGoods > 0) state.businessGoods.toString() else "") }
    var investmentInput by remember { mutableStateOf(if (state.investments > 0) state.investments.toString() else "") }
    var debtsInput by remember { mutableStateOf(if (state.debtsOwed > 0) state.debtsOwed.toString() else "") }
    var expensesInput by remember { mutableStateOf(if (state.expensesDue > 0) state.expensesDue.toString() else "") }

    fun syncData() {
        viewModel.updateZakatField(
            cash = cashInput.toDoubleOrNull() ?: 0.0,
            savings = bankInput.toDoubleOrNull() ?: 0.0,
            gold = goldGramsInput.toDoubleOrNull() ?: 0.0,
            silver = silverGramsInput.toDoubleOrNull() ?: 0.0,
            business = businessInput.toDoubleOrNull() ?: 0.0,
            investments = investmentInput.toDoubleOrNull() ?: 0.0,
            debts = debtsInput.toDoubleOrNull() ?: 0.0,
            expenses = expensesInput.toDoubleOrNull() ?: 0.0
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "যাকাত ক্যালকুলেটর" else "Zakat Calculator",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Result Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isZakatPayable) IslamicEmeraldPrimary.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = if (isBangla) "প্রদেয় যাকাতের পরিমাণ (২.৫%)" else "Total Zakat Payable (2.5%)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val zakatAmountFormatted = "৳ " + decFormat.format(state.zakatPayableAmount)
                    Text(
                        text = if (isBangla) SinglePrayerTime.toBanglaNumerals(zakatAmountFormatted) else zakatAmountFormatted,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = IslamicEmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isBangla) "মোট সম্পদ:" else "Total Assets:",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                            val totalAssetStr = "৳ " + decFormat.format(state.totalAssets)
                            Text(
                                text = if (isBangla) SinglePrayerTime.toBanglaNumerals(totalAssetStr) else totalAssetStr,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column {
                            Text(
                                text = if (isBangla) "মোট দেনা/দায়:" else "Liabilities:",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                            val totalLiabStr = "৳ " + decFormat.format(state.totalLiabilities)
                            Text(
                                text = if (isBangla) SinglePrayerTime.toBanglaNumerals(totalLiabStr) else totalLiabStr,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    val statusText = if (state.isZakatPayable) {
                        if (isBangla) "✓ আপনার মোট নিট সম্পদ নিসাবের ঊর্ধ্বে (যাকাত ফরজ)" else "✓ Your net wealth is above Nisab (Zakat is obligatory)"
                    } else {
                        if (isBangla) "ℹ আপনার সম্পদ নিসাব সীমার নিচে (যাকাত ফরজ নয়)" else "ℹ Your wealth is below Nisab threshold"
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isZakatPayable) IslamicEmeraldPrimary else IslamicGoldDark
                    )
                }
            }

            // 2. Asset Inputs Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isBangla) "সম্পদের বিবরণ (টাকায়)" else "Assets Breakdown (in BDT)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = cashInput,
                        onValueChange = { cashInput = it; syncData() },
                        label = { Text(if (isBangla) "হাতে থাকা নগদ টাকা" else "Cash in Hand") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = bankInput,
                        onValueChange = { bankInput = it; syncData() },
                        label = { Text(if (isBangla) "ব্যাংক ব্যালেন্স ও সঞ্চয়" else "Bank Accounts & Savings") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = goldGramsInput,
                        onValueChange = { goldGramsInput = it; syncData() },
                        label = { Text(if (isBangla) "স্বর্ণের পরিমাণ (গ্রাম)" else "Gold (Grams)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = silverGramsInput,
                        onValueChange = { silverGramsInput = it; syncData() },
                        label = { Text(if (isBangla) "রৌপ্যের পরিমাণ (গ্রাম)" else "Silver (Grams)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = businessInput,
                        onValueChange = { businessInput = it; syncData() },
                        label = { Text(if (isBangla) "ব্যবসার পণ্যের মূল্য" else "Business Merchandise Value") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = investmentInput,
                        onValueChange = { investmentInput = it; syncData() },
                        label = { Text(if (isBangla) "শেয়ার / বন্ড / বিনিয়োগ" else "Shares & Investments") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // 3. Liabilities Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isBangla) "ঋণ ও বকেয়া দেনা (টাকায়)" else "Debts & Liabilities (in BDT)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )

                    OutlinedTextField(
                        value = debtsInput,
                        onValueChange = { debtsInput = it; syncData() },
                        label = { Text(if (isBangla) "অন্যের কাছে দেনা / ঋণ" else "Immediate Debts Payable") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = expensesInput,
                        onValueChange = { expensesInput = it; syncData() },
                        label = { Text(if (isBangla) "বকেয়া বিল ও খরচ" else "Overdue Expenses & Bills") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}
