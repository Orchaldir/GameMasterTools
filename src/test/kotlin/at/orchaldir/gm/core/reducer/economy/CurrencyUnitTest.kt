package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteCurrencyUnit
import at.orchaldir.gm.core.action.UpdateCurrencyUnit
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.economy.money.Denomination
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class CurrencyUnitTest {

    val subDenominations = listOf(Pair(Denomination(), 1))
    val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Currency(CURRENCY_ID_0, subDenominations = subDenominations)),
            Storage(CurrencyUnit(CURRENCY_UNIT_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteCurrencyUnit(CURRENCY_UNIT_ID_0)

        @Test
        fun `Can delete an existing business`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getCurrencyUnitStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteCurrencyUnit(UNKNOWN_CURRENCY_UNIT_ID)

            assertIllegalArgument("Requires unknown Currency Unit 99!") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateCurrencyUnit(CurrencyUnit(UNKNOWN_CURRENCY_UNIT_ID))

            assertIllegalArgument("Requires unknown Currency Unit 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown currency`() {
            val action = UpdateCurrencyUnit(CurrencyUnit(CURRENCY_UNIT_ID_0, currency = UNKNOWN_CURRENCY_ID))

            assertIllegalArgument("Requires unknown Currency 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Currency must have the denomination`() {
            val action = UpdateCurrencyUnit(CurrencyUnit(CURRENCY_UNIT_ID_0, denomination = 2))

            assertIllegalState("Currency 0 doesn't have a denomination 2!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A sub denomination is valid`() {
            val unit = CurrencyUnit(CURRENCY_UNIT_ID_0, denomination = 1)
            val action = UpdateCurrencyUnit(unit)

            assertEquals(unit, REDUCER.invoke(state, action).first.getCurrencyUnitStorage().get(CURRENCY_UNIT_ID_0))
        }

        @Test
        fun `Test Success`() {
            val unit = CurrencyUnit(CURRENCY_UNIT_ID_0, NAME)
            val action = UpdateCurrencyUnit(unit)

            assertEquals(unit, REDUCER.invoke(state, action).first.getCurrencyUnitStorage().get(CURRENCY_UNIT_ID_0))
        }
    }

}