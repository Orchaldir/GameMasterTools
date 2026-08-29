package at.orchaldir.gm.core.selector.economy.money

import at.orchaldir.gm.CURRENCY_UNIT_ID_0
import at.orchaldir.gm.TREASURE_PARCEL_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.gm.treasure.MoneyParcel
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcel
import at.orchaldir.gm.core.model.util.quantity.FixedNumber
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CurrencyUnitTest {

    @Nested
    inner class CanDeleteTest {
        private val unit = CurrencyUnit(CURRENCY_UNIT_ID_0)
        private val state = State(
            listOf(
                Storage(unit),
            )
        )

        @Test
        fun `Cannot delete a currency in a treasure parcel`() {
            val entry = MoneyParcel(mapOf(CURRENCY_UNIT_ID_0 to FixedNumber(1)))
            val parcel = TreasureParcel(TREASURE_PARCEL_ID_0, entry = entry)
            val newState = state.updateStorage(parcel)

            failCanDelete(newState, TREASURE_PARCEL_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(CURRENCY_UNIT_ID_0).addId(blockingId),
                state.canDeleteCurrencyUnit(CURRENCY_UNIT_ID_0)
            )
        }
    }

}