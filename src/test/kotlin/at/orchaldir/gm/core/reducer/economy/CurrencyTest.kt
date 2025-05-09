package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteCurrency
import at.orchaldir.gm.core.action.UpdateCurrency
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class CurrencyTest {

    val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Currency(CURRENCY_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteCurrency(CURRENCY_ID_0)

        @Test
        fun `Can delete an existing business`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getCurrencyStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteCurrency(UNKNOWN_CURRENCY_ID)

            assertIllegalArgument("Requires unknown Currency 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete an currency with units`() {
            val unit = CurrencyUnit(CURRENCY_UNIT_ID_0, currency = CURRENCY_ID_0)
            val state = state.updateStorage(Storage(unit))

            assertIllegalArgument("Cannot delete currency 0, because it has units!") {
                REDUCER.invoke(state, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateCurrency(Currency(UNKNOWN_CURRENCY_ID))

            assertIllegalArgument("Requires unknown Currency 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Start date is after end date`() {
            val action = UpdateCurrency(Currency(CURRENCY_ID_0, startDate = DAY2, endDate = DAY1))

            assertIllegalArgument("Start date is after end date!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Must have enough sub denomination for all units`() {
            val unit = CurrencyUnit(CURRENCY_UNIT_ID_0, denomination = 1)
            val state = state.updateStorage(Storage(unit))
            val action = UpdateCurrency(Currency(CURRENCY_ID_0))

            assertIllegalArgument("Currency Units require at least 1 sub denomination!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Test Success`() {
            val currency = Currency(CURRENCY_ID_0, NAME)
            val action = UpdateCurrency(currency)

            assertEquals(currency, REDUCER.invoke(state, action).first.getCurrencyStorage().get(CURRENCY_ID_0))
        }
    }

}