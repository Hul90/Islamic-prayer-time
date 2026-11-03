package com.example.model

enum class ZakatNisabStandard(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val grams: Double,
    val descriptionEn: String,
    val descriptionBn: String
) {
    SILVER(
        id = "silver",
        nameEn = "Silver Nisab (52.5 Tola / 612.36g)",
        nameBn = "রৌপ্য নিসাব (৫২.৫ তোলা / ৬১২.৩৬ গ্রাম)",
        grams = 612.36,
        descriptionEn = "Classical standard recommended by scholars for cash, savings, and mixed wealth (200 Dirhams = 612.36g silver).",
        descriptionBn = "নগদ টাকা, সঞ্চয় ও মিশ্র সম্পদের ক্ষেত্রে অধিকাংশ ফকিহগণের মতে রৌপ্যের নিসাব প্রযোজ্য (২০০ দিরহাম = ৬১২.৩৬ গ্রাম রূপা)।"
    ),
    GOLD(
        id = "gold",
        nameEn = "Gold Nisab (7.5 Tola / 87.48g)",
        nameBn = "স্বর্ণ নিসাব (৭.৫ তোলা / ৮৭.৪৮ গ্রাম)",
        grams = 87.48,
        descriptionEn = "Traditional standard for gold holdings (20 Dinars / Mithqals = 87.48g pure gold).",
        descriptionBn = "খাঁটি স্বর্ণের জন্য নির্ধারিত নিসাব (২০ দিনার / মিসকাল = ৮৭.৪৮ গ্রাম খাঁটি সোনা)।"
    )
}

data class ZakatCalculatorState(
    val cashInHand: Double = 0.0,
    val bankSavings: Double = 0.0,
    val goldGrams: Double = 0.0,
    val goldPricePerGram: Double = 11000.0, // Reference editable market price per gram (e.g. BDT)
    val silverGrams: Double = 0.0,
    val silverPricePerGram: Double = 200.0,  // Reference editable market price per gram (e.g. BDT)
    val businessGoods: Double = 0.0,
    val investments: Double = 0.0,
    val debtsOwed: Double = 0.0,
    val expensesDue: Double = 0.0,
    val nisabStandard: ZakatNisabStandard = ZakatNisabStandard.SILVER
) {
    companion object {
        const val GOLD_NISAB_GRAMS = 87.48
        const val SILVER_NISAB_GRAMS = 612.36
        const val ZAKAT_RATE = 0.025 // 2.5% or 1/40th
    }

    val goldValue: Double
        get() = (goldGrams.coerceAtLeast(0.0) * goldPricePerGram.coerceAtLeast(0.0))

    val silverValue: Double
        get() = (silverGrams.coerceAtLeast(0.0) * silverPricePerGram.coerceAtLeast(0.0))

    val liquidCashAndAssets: Double
        get() = cashInHand.coerceAtLeast(0.0) +
                bankSavings.coerceAtLeast(0.0) +
                businessGoods.coerceAtLeast(0.0) +
                investments.coerceAtLeast(0.0)

    val totalAssets: Double
        get() = goldValue + silverValue + liquidCashAndAssets

    val totalLiabilities: Double
        get() = debtsOwed.coerceAtLeast(0.0) + expensesDue.coerceAtLeast(0.0)

    val netZakatEligibleWealth: Double
        get() = (totalAssets - totalLiabilities).coerceAtLeast(0.0)

    val goldNisabThreshold: Double
        get() = GOLD_NISAB_GRAMS * goldPricePerGram.coerceAtLeast(0.0)

    val silverNisabThreshold: Double
        get() = SILVER_NISAB_GRAMS * silverPricePerGram.coerceAtLeast(0.0)

    val activeNisabThreshold: Double
        get() = when (nisabStandard) {
            ZakatNisabStandard.GOLD -> goldNisabThreshold
            ZakatNisabStandard.SILVER -> silverNisabThreshold
        }

    val isZakatPayable: Boolean
        get() = activeNisabThreshold > 0.0 && netZakatEligibleWealth >= activeNisabThreshold

    val zakatPayableAmount: Double
        get() = if (isZakatPayable) netZakatEligibleWealth * ZAKAT_RATE else 0.0
}
