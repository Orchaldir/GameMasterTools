package at.orchaldir.gm.core.selector.gm

import at.orchaldir.gm.TREASURE_PARCEL_ID_0
import at.orchaldir.gm.TREASURE_PARCEL_ID_1
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcel
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelLookup
import at.orchaldir.gm.core.selector.gm.treasure.canDeleteTreasureParcel
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TreasureParcelTest {

    @Nested
    inner class CanDeleteTest {
        private val encounter = TreasureParcel(TREASURE_PARCEL_ID_0)
        private val state = State(Storage(encounter))

        @Test
        fun `Cannot delete a treasure parcel used by another`() {
            val element = TreasureParcel(TREASURE_PARCEL_ID_1, entry = TreasureParcelLookup(TREASURE_PARCEL_ID_0))
            val newState = state.updateStorage(listOf(encounter, element))

            failCanDelete(newState, TREASURE_PARCEL_ID_1)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(TREASURE_PARCEL_ID_0).addId(blockingId),
                state.canDeleteTreasureParcel(TREASURE_PARCEL_ID_0),
            )
        }
    }

}