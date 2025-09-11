package at.orchaldir.gm.core.selector.economy.money

import at.orchaldir.gm.CURRENCY_ID_0
import at.orchaldir.gm.CURRENCY_UNIT_ID_0
import at.orchaldir.gm.DAY0
import at.orchaldir.gm.REALM_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.economy.money.Denomination
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CurrencyTest {

    @Nested
    inner class CanDeleteTest {
        private val currency = Currency(CURRENCY_ID_0)
        private val state = State(
            listOf(
                Storage(currency),
            )
        )

        @Test
        fun `Cannot delete a currency with units`() {
            val unit = CurrencyUnit(CURRENCY_UNIT_ID_0, currency = CURRENCY_ID_0)
            val newState = state.updateStorage(Storage(unit))

            failCanDelete(newState, CURRENCY_UNIT_ID_0)
        }

        @Test
        fun `Cannot delete a currency used by a realm`() {
            val realm = Realm(REALM_ID_0, currency = History(CURRENCY_ID_0))
            val newState = state.updateStorage(Storage(realm))

            failCanDelete(newState, REALM_ID_0)
        }

        @Test
        fun `Cannot delete a currency used by a realm in the past`() {
            val history = History(null, HistoryEntry(CURRENCY_ID_0, DAY0))
            val realm = Realm(REALM_ID_0, currency = history)
            val newState = state.updateStorage(Storage(realm))

            failCanDelete(newState, REALM_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(CURRENCY_ID_0).addId(blockingId),
                state.canDeleteCurrency(CURRENCY_ID_0)
            )
        }
    }

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
            test(0, "0c")
        }

        @Test
        fun `A price in cent`() {
            test(5, "5c")
        }

        @Test
        fun `A price in dollar`() {
            test(200, "$2")
        }

        @Test
        fun `A price in both`() {
            test(123, "$1 23c")
        }

        private fun test(value: Int, result: String) {
            assertEquals(result, currency.display(Price(value)))
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
            test(0, "0 cp")
        }

        @Test
        fun `A price in cp`() {
            test(5, "5 cp")
        }

        @Test
        fun `A price in sp`() {
            test(40, "4 sp")
        }

        @Test
        fun `A price in cp & sp`() {
            test(54, "5 sp 4 cp")
        }

        @Test
        fun `A price in gp`() {
            test(1600, "16 gp")
        }

        @Test
        fun `A price in sp & gp`() {
            test(230, "2 gp 3 sp")
        }

        @Test
        fun `A price in all 3`() {
            test(123, "1 gp 2 sp 3 cp")
        }

        private fun test(value: Int, result: String) {
            assertEquals(result, currency.display(Price(value)))
        }
    }
}