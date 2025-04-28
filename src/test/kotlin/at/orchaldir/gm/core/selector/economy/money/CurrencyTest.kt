package at.orchaldir.gm.core.selector.economy.money

import at.orchaldir.gm.CURRENCY_ID_0
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.Denomination
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CurrencyTest {

    @Nested
    inner class DisplayDollarTest {

        private val currency = Currency(
            CURRENCY_ID_0,
            denomination = Denomination.init("$", true),
            subDenominations = listOf(
                Pair(Denomination.init("c", false), 100),
            ),
        )

        @Test
        fun `A price of 0`() {
            assertEquals("0c", currency.display(0))
        }

        @Test
        fun `A price in cent`() {
            assertEquals("5c", currency.display(5))
        }

        @Test
        fun `A price in dollar`() {
            assertEquals("$2", currency.display(200))
        }

        @Test
        fun `A price in both`() {
            assertEquals("$1 23c", currency.display(123))
        }
    }

    @Nested
    inner class DisplayFantasyCurrencyTest {

        private val currency = Currency(
            CURRENCY_ID_0,
            denomination = Denomination.init("gp", hasSpace = true),
            subDenominations = listOf(
                Pair(Denomination.init("cp", hasSpace = true), 10),
                Pair(Denomination.init("sp", hasSpace = true), 100),
            ),
        )

        @Test
        fun `A price of 0`() {
            assertEquals("0 cp", currency.display(0))
        }

        @Test
        fun `A price in cp`() {
            assertEquals("5 cp", currency.display(5))
        }

        @Test
        fun `A price in sp`() {
            assertEquals("4 sp", currency.display(40))
        }

        @Test
        fun `A price in cp & sp`() {
            assertEquals("5 sp 4 cp", currency.display(54))
        }

        @Test
        fun `A price in gp`() {
            assertEquals("16 gp", currency.display(1600))
        }

        @Test
        fun `A price in sp & gp`() {
            assertEquals("2 gp 3 sp", currency.display(230))
        }

        @Test
        fun `A price in all 3`() {
            assertEquals("1 gp 2 sp 3 cp", currency.display(123))
        }
    }
}