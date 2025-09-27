package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
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
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(CurrencyUnit(UNKNOWN_CURRENCY_UNIT_ID))

            assertIllegalArgument("Requires unknown Currency Unit 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown currency`() {
            val action = UpdateAction(CurrencyUnit(CURRENCY_UNIT_ID_0, currency = UNKNOWN_CURRENCY_ID))

            assertIllegalArgument("Requires unknown Currency 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Currency must have the denomination`() {
            val action = UpdateAction(CurrencyUnit(CURRENCY_UNIT_ID_0, denomination = 2))

            assertIllegalState("Currency 0 doesn't have a denomination 2!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A sub denomination is valid`() {
            val unit = CurrencyUnit(CURRENCY_UNIT_ID_0, denomination = 1)
            val action = UpdateAction(unit)

            assertEquals(unit, REDUCER.invoke(state, action).first.getCurrencyUnitStorage().get(CURRENCY_UNIT_ID_0))
        }

        @Test
        fun `Test Success`() {
            val unit = CurrencyUnit(CURRENCY_UNIT_ID_0, NAME)
            val action = UpdateAction(unit)

            assertEquals(unit, REDUCER.invoke(state, action).first.getCurrencyUnitStorage().get(CURRENCY_UNIT_ID_0))
        }
    }

}