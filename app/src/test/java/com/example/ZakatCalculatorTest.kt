package com.example

import com.example.model.ZakatCalculatorState
import com.example.model.ZakatNisabStandard
import org.junit.Assert.*
import org.junit.Test

class ZakatCalculatorTest {

    @Test
    fun `test gold nisab calculation with exactly 87_48g and no unjustified multipliers`() {
        val goldPricePerGram = 11000.0
        val state = ZakatCalculatorState(
            goldPricePerGram = goldPricePerGram,
            silverPricePerGram = 200.0,
            nisabStandard = ZakatNisabStandard.GOLD
        )

        // Exact Gold Nisab should be 87.48 * price without any 0.85 multiplier
        val expectedGoldNisab = 87.48 * goldPricePerGram
        assertEquals(expectedGoldNisab, state.goldNisabThreshold, 0.001)
        assertEquals(expectedGoldNisab, state.activeNisabThreshold, 0.001)
    }

    @Test
    fun `test silver nisab calculation with exactly 612_36g`() {
        val silverPricePerGram = 200.0
        val state = ZakatCalculatorState(
            goldPricePerGram = 11000.0,
            silverPricePerGram = silverPricePerGram,
            nisabStandard = ZakatNisabStandard.SILVER
        )

        // Exact Silver Nisab should be 612.36 * silver price
        val expectedSilverNisab = 612.36 * silverPricePerGram
        assertEquals(expectedSilverNisab, state.silverNisabThreshold, 0.001)
        assertEquals(expectedSilverNisab, state.activeNisabThreshold, 0.001)
    }

    @Test
    fun `test zakat payable when net wealth is at or above nisab`() {
        // Silver Nisab at 200 BDT/g = 612.36 * 200 = 122,472 BDT
        val state = ZakatCalculatorState(
            cashInHand = 100000.0,
            bankSavings = 50000.0,
            goldGrams = 10.0, // 10 * 10,000 = 100,000
            goldPricePerGram = 10000.0,
            silverGrams = 0.0,
            silverPricePerGram = 200.0,
            businessGoods = 20000.0,
            investments = 30000.0,
            debtsOwed = 50000.0,
            expensesDue = 10000.0,
            nisabStandard = ZakatNisabStandard.SILVER
        )

        // Total Gross Assets = 100k + 50k + 100k + 0 + 20k + 30k = 300,000
        assertEquals(300000.0, state.totalAssets, 0.001)

        // Total Liabilities = 50k + 10k = 60,000
        assertEquals(60000.0, state.totalLiabilities, 0.001)

        // Net Wealth = 300,000 - 60,000 = 240,000
        assertEquals(240000.0, state.netZakatEligibleWealth, 0.001)

        // Nisab threshold = 612.36 * 200 = 122,472
        assertTrue(state.isZakatPayable)

        // Zakat Amount = 240,000 * 0.025 = 6,000
        val expectedZakat = 240000.0 * 0.025
        assertEquals(expectedZakat, state.zakatPayableAmount, 0.001)
    }

    @Test
    fun `test zakat not payable when net wealth is below nisab`() {
        // Silver Nisab at 200 BDT/g = 122,472 BDT
        val state = ZakatCalculatorState(
            cashInHand = 50000.0,
            bankSavings = 20000.0,
            debtsOwed = 10000.0,
            silverPricePerGram = 200.0,
            nisabStandard = ZakatNisabStandard.SILVER
        )

        // Net wealth = 70,000 - 10,000 = 60,000 < 122,472
        assertFalse(state.isZakatPayable)
        assertEquals(0.0, state.zakatPayableAmount, 0.001)
    }

    @Test
    fun `test negative input validation prevents negative assets or zakat`() {
        val state = ZakatCalculatorState(
            cashInHand = -5000.0,
            bankSavings = -1000.0,
            goldGrams = -5.0,
            debtsOwed = 100000.0,
            expensesDue = 50000.0
        )

        assertEquals(0.0, state.totalAssets, 0.001)
        assertEquals(0.0, state.netZakatEligibleWealth, 0.001)
        assertFalse(state.isZakatPayable)
        assertEquals(0.0, state.zakatPayableAmount, 0.001)
    }
}
