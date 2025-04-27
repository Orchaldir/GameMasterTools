package at.orchaldir.gm.core.selector.economy.money

import at.orchaldir.gm.CURRENCY_ID_0
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.Denomination
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CurrencyTest {

    @Nested
    inner class DisplayTest {

        private val currency = Currency(
            CURRENCY_ID_0,
            denomination = Denomination.init("gp"),
            subDenominations = listOf(
                Pair(Denomination.init("cp"), 10),
                Pair(Denomination.init("sp"), 100),
            ),
        )

        @Test
        fun `A price of 0`() {
            assertEquals("0 cp", currency.display(0))
        }
    }
}