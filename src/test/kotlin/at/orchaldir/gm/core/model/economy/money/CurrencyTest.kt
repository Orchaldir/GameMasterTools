package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.FANTASY_CURRENCY
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CurrencyTest {

    @Nested
    inner class CalculatePriceFromDenominationsTest {

        @Test
        fun `Zero for all denominations`() {
            assertPrice(listOf(0, 0, 0), Price(0))
        }

        @Test
        fun `All denominations`() {
            assertPrice(listOf(1, 2, 3), Price(321))
        }

        private fun assertPrice(denominations: List<Int>, price: Price) {
            assertEquals(price, FANTASY_CURRENCY.calculatePriceFromDenominations(denominations))
        }

    }

}