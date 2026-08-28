package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.AMMUNITION_ID_0
import at.orchaldir.gm.TREASURE_PARCEL_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.gm.treasure.AmmunitionParcel
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcel
import at.orchaldir.gm.core.model.item.ammunition.Ammunition
import at.orchaldir.gm.core.model.util.quantity.FixedNumber
import at.orchaldir.gm.core.selector.item.ammunition.canDeleteAmmunition
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AmmunitionTest {

    @Nested
    inner class CanDeleteTest {
        private val unit = Ammunition(AMMUNITION_ID_0)
        private val state = State(
            listOf(
                Storage(unit),
            )
        )

        @Test
        fun `Cannot delete an ammunition in a treasure parcel`() {
            val entry = AmmunitionParcel(mapOf(AMMUNITION_ID_0 to FixedNumber(1)))
            val parcel = TreasureParcel(TREASURE_PARCEL_ID_0, entry = entry)
            val newState = state.updateStorage(parcel)

            failCanDelete(newState, TREASURE_PARCEL_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(AMMUNITION_ID_0).addId(blockingId),
                state.canDeleteAmmunition(AMMUNITION_ID_0)
            )
        }
    }

}