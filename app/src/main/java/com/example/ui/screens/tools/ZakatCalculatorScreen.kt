package com.example.ui.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SinglePrayerTime
import com.example.model.ZakatCalculatorState
import com.example.model.ZakatNisabStandard
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

    var cashInput by remember(state.cashInHand) {
        mutableStateOf(if (state.cashInHand > 0) state.cashInHand.toPlainString() else "")
    }
    var bankInput by remember(state.bankSavings) {
        mutableStateOf(if (state.bankSavings > 0) state.bankSavings.toPlainString() else "")
    }
    var goldGramsInput by remember(state.goldGrams) {
        mutableStateOf(if (state.goldGrams > 0) state.goldGrams.toPlainString() else "")
    }
    var silverGramsInput by remember(state.silverGrams) {
        mutableStateOf(if (state.silverGrams > 0) state.silverGrams.toPlainString() else "")
    }
    var businessInput by remember(state.businessGoods) {
        mutableStateOf(if (state.businessGoods > 0) state.businessGoods.toPlainString() else "")
    }
    var investmentInput by remember(state.investments) {
        mutableStateOf(if (state.investments > 0) state.investments.toPlainString() else "")
    }
    var debtsInput by remember(state.debtsOwed) {
        mutableStateOf(if (state.debtsOwed > 0) state.debtsOwed.toPlainString() else "")
    }
    var expensesInput by remember(state.expensesDue) {
        mutableStateOf(if (state.expensesDue > 0) state.expensesDue.toPlainString() else "")
    }
    var goldPriceInput by remember(state.goldPricePerGram) {
        mutableStateOf(state.goldPricePerGram.toPlainString())
    }
    var silverPriceInput by remember(state.silverPricePerGram) {
        mutableStateOf(state.silverPricePerGram.toPlainString())
    }

    fun parsePositive(text: String): Double {
        return text.trim().toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    }

    fun syncAll(
        goldP: Double = parsePositive(goldPriceInput),
        silverP: Double = parsePositive(silverPriceInput),
        standard: ZakatNisabStandard = state.nisabStandard
    ) {
        viewModel.updateZakatField(
            cash = parsePositive(cashInput),
            savings = parsePositive(bankInput),
            gold = parsePositive(goldGramsInput),
            silver = parsePositive(silverGramsInput),
            business = parsePositive(businessInput),
            investments = parsePositive(investmentInput),
            debts = parsePositive(debtsInput),
            expenses = parsePositive(expensesInput),
            goldPrice = goldP,
            silverPrice = silverP,
            nisabStandard = standard
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
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_zakat")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            cashInput = ""
                            bankInput = ""
                            goldGramsInput = ""
                            silverGramsInput = ""
                            businessInput = ""
                            investmentInput = ""
                            debtsInput = ""
                            expensesInput = ""
                            viewModel.resetZakatCalculator()
                        }
                    ) {
                        Text(
                            text = if (isBangla) "রিসেট" else "Reset",
                            color = IslamicEmeraldPrimary,
                            fontWeight = FontWeight.Bold
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
            // 1. Result Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isZakatPayable) IslamicEmeraldPrimary.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = if (state.isZakatPayable) androidx.compose.foundation.BorderStroke(1.5.dp, IslamicEmeraldPrimary.copy(alpha = 0.5f)) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = if (isBangla) "প্রদেয় যাকাতের পরিমাণ (২.৫% বা ১/৪০ অংশ)" else "Zakat Payable Amount (2.5% / 1/40th)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val zakatAmountFormatted = "৳ " + decFormat.format(state.zakatPayableAmount)
                    Text(
                        text = if (isBangla) SinglePrayerTime.toBanglaNumerals(zakatAmountFormatted) else zakatAmountFormatted,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = IslamicEmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isBangla) "মোট সম্পদ (Gross Assets):" else "Total Gross Assets:",
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

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isBangla) "মোট দেনা (Liabilities):" else "Total Liabilities:",
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isBangla) "যাকাতযোগ্য নিট সম্পদ:" else "Net Eligible Wealth:",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                            val netStr = "৳ " + decFormat.format(state.netZakatEligibleWealth)
                            Text(
                                text = if (isBangla) SinglePrayerTime.toBanglaNumerals(netStr) else netStr,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isBangla) "সক্রিয় নিসাব সীমা:" else "Active Nisab Threshold:",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicMutedText
                            )
                            val nisabStr = "৳ " + decFormat.format(state.activeNisabThreshold)
                            Text(
                                text = if (isBangla) SinglePrayerTime.toBanglaNumerals(nisabStr) else nisabStr,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGoldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (state.isZakatPayable) IslamicEmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (state.isZakatPayable) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (state.isZakatPayable) IslamicEmeraldPrimary else IslamicGoldDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val statusText = if (state.isZakatPayable) {
                                if (isBangla) "আপনার নিট সম্পদ নিসাব অতিক্রম করেছে — যাকাত ফরজ (প্রদেয়: ২.৫%)"
                                else "Net wealth exceeds the Nisab threshold — Zakat is obligatory (2.5%)"
                            } else {
                                if (isBangla) "সম্পদ নিসাব সীমার নিচে রয়েছে — যাকাত প্রযোজ্য নয়"
                                else "Net wealth is below the Nisab threshold — Zakat is not obligatory"
                            }
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (state.isZakatPayable) IslamicEmeraldPrimary else IslamicGoldDark
                            )
                        }
                    }
                }
            }

            // 2. Nisab Standard & Market Rates Card (Editable & Transparent)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "নিসাব পদ্ধতি ও বাজার দর" else "Nisab Method & Metal Market Rates",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = if (isBangla) "নিসাব মানদণ্ড নির্বাচন করুন (নগদ ও মিশ্র সম্পদে রৌপ্য নিসাব অধিক নিরাপদ):"
                        else "Select Nisab Standard (Silver Nisab is widely recommended for cash & mixed wealth):",
                        style = MaterialTheme.typography.bodySmall,
                        color = IslamicMutedText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ZakatNisabStandard.entries.forEach { standard ->
                            val isSel = state.nisabStandard == standard
                            FilterChip(
                                selected = isSel,
                                onClick = {
                                    viewModel.setZakatNisabStandard(standard)
                                    syncAll(standard = standard)
                                },
                                label = {
                                    Text(
                                        text = if (isBangla) standard.nameBn else standard.nameEn,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chip_nisab_${standard.id}")
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isBangla) state.nisabStandard.descriptionBn else state.nisabStandard.descriptionEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = IslamicMutedText,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = goldPriceInput,
                            onValueChange = {
                                goldPriceInput = it
                                syncAll(goldP = parsePositive(it))
                            },
                            label = { Text(if (isBangla) "স্বর্ণের দর প্রতি গ্রাম (টাকা)" else "Gold Price/Gram (BDT)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_gold_price"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = silverPriceInput,
                            onValueChange = {
                                silverPriceInput = it
                                syncAll(silverP = parsePositive(it))
                            },
                            label = { Text(if (isBangla) "রৌপ্যের দর প্রতি গ্রাম (টাকা)" else "Silver Price/Gram (BDT)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_silver_price"),
                            singleLine = true
                        )
                    }

                    Text(
                        text = if (isBangla)
                            "• স্বর্ণ নিসাব = ৮৭.৪৮ গ্রাম × প্রতি গ্রাম মূল্য = ৳ ${decFormat.format(state.goldNisabThreshold)}\n• রৌপ্য নিসাব = ৬১২.৩৬ গ্রাম × প্রতি গ্রাম মূল্য = ৳ ${decFormat.format(state.silverNisabThreshold)}"
                        else
                            "• Gold Nisab = 87.48g × price/g = ৳ ${decFormat.format(state.goldNisabThreshold)}\n• Silver Nisab = 612.36g × price/g = ৳ ${decFormat.format(state.silverNisabThreshold)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = IslamicGoldDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 3. Asset Inputs Card
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
                        text = if (isBangla) "সম্পদের বিবরণ ও মূল্যায়ন" else "Zakat-Eligible Assets Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = cashInput,
                        onValueChange = { cashInput = it; syncAll() },
                        label = { Text(if (isBangla) "হাতে থাকা নগদ টাকা (টাকা)" else "Cash in Hand (BDT)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("input_cash"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = bankInput,
                        onValueChange = { bankInput = it; syncAll() },
                        label = { Text(if (isBangla) "ব্যাংক ব্যালেন্স ও সঞ্চয় (টাকা)" else "Bank Accounts & Savings (BDT)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("input_savings"),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = goldGramsInput,
                            onValueChange = { goldGramsInput = it; syncAll() },
                            label = { Text(if (isBangla) "স্বর্ণের ওজন (গ্রাম)" else "Gold Weight (Grams)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("input_gold_grams"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = silverGramsInput,
                            onValueChange = { silverGramsInput = it; syncAll() },
                            label = { Text(if (isBangla) "রৌপ্যের ওজন (গ্রাম)" else "Silver Weight (Grams)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("input_silver_grams"),
                            singleLine = true
                        )
                    }

                    if (state.goldGrams > 0 || state.silverGrams > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                if (state.goldGrams > 0) {
                                    Text(
                                        text = if (isBangla) "স্বর্ণের অর্থমূল্য: ৳ ${decFormat.format(state.goldValue)} (${state.goldGrams}g × ৳${state.goldPricePerGram})"
                                        else "Gold Value: ৳ ${decFormat.format(state.goldValue)} (${state.goldGrams}g × ৳${state.goldPricePerGram})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = IslamicEmeraldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (state.silverGrams > 0) {
                                    Text(
                                        text = if (isBangla) "রৌপ্যের অর্থমূল্য: ৳ ${decFormat.format(state.silverValue)} (${state.silverGrams}g × ৳${state.silverPricePerGram})"
                                        else "Silver Value: ৳ ${decFormat.format(state.silverValue)} (${state.silverGrams}g × ৳${state.silverPricePerGram})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = IslamicEmeraldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = businessInput,
                        onValueChange = { businessInput = it; syncAll() },
                        label = { Text(if (isBangla) "ব্যবসার পণ্যের বর্তমান বাজারমূল্য" else "Business Merchandise Market Value") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("input_business"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = investmentInput,
                        onValueChange = { investmentInput = it; syncAll() },
                        label = { Text(if (isBangla) "শেয়ার / বন্ড / ব্যবসায়িক বিনিয়োগ" else "Shares, Mutual Funds & Investments") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("input_investments"),
                        singleLine = true
                    )
                }
            }

            // 4. Liabilities Card
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
                        text = if (isBangla) "কর্তনযোগ্য দেনা ও দায় (Liabilities)" else "Deductible Liabilities & Debts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )

                    OutlinedTextField(
                        value = debtsInput,
                        onValueChange = { debtsInput = it; syncAll() },
                        label = { Text(if (isBangla) "অন্যের কাছে প্রদেয় জরুরি ঋণ" else "Immediate Debts Due to Others") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("input_debts"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = expensesInput,
                        onValueChange = { expensesInput = it; syncAll() },
                        label = { Text(if (isBangla) "বকেয়া বিল, বেতন ও খরচ" else "Overdue Bills, Wages & Immediate Dues") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("input_expenses"),
                        singleLine = true
                    )
                }
            }

            // 5. Scholarly Guidance & Verification Advisory Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = IslamicEmeraldPrimary.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicEmeraldPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Scholarly Guidance",
                        tint = IslamicEmeraldPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isBangla) "শরীয়তসম্মত যাকাত নির্দেশিকা ও সতর্কতা" else "Shariah Advisory & Guidance",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = IslamicEmeraldPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla)
                                "১. নিসাব: স্বর্ণের নিসাব ৭.৫ তোলা (৮৭.৪৮ গ্রাম) এবং রূপার নিসাব ৫২.৫ তোলা (৬১২.৩৬ গ্রাম)। নগদ অর্থ, সঞ্চয় ও ব্যবসায়িক পণ্যের ক্ষেত্রে আলেমগণ সর্বসম্মতিক্রমে রূপার নিসাবকে ভিত্তি হিসেবে অগ্রাধিকার দেন।\n২. বর্ষপূর্তি (হাওল): নিসাব পরিমাণ সম্পদের মালিক হয়ে হিজরি এক চান্দ্রবছর অতিক্রান্ত হলে যাকাত ফরজ হয়।\n৩. নির্ভরযোগ্য আলেম বা স্থানীয় মুফতি সাহেবের সাথে পরামর্শ করে আপনার সুনির্দিষ্ট সম্পদের যাকাত হিসাব চূড়ান্ত করা বাঞ্ছনীয়।"
                            else
                                "1. Nisab Standards: Gold Nisab is 7.5 tola (87.48g) and Silver Nisab is 52.5 tola (612.36g). For cash, bank savings, and merchandise, majority scholars consider Silver Nisab as the prudent threshold.\n2. Hawl Requirement: Wealth must meet or exceed Nisab for one complete lunar year.\n3. Always consult a qualified Islamic scholar or local Mufti to confirm specific assets, deferred liabilities, and local market metal prices.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

private fun Double.toPlainString(): String {
    return if (this == this.toLong().toDouble()) {
        this.toLong().toString()
    } else {
        this.toString()
    }
}
