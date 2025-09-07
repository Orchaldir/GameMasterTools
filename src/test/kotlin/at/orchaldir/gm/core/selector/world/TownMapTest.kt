package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.InTownMap
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TownMapTest {

    @Nested
    inner class CanDeleteTest {
        private val townMap = TownMap(TOWN_MAP_ID_0)
        private val state = State(
            listOf(
                Storage(townMap),
            )
        )
        private val position = InTownMap(TOWN_MAP_ID_0, 0)

        @Test
        fun `Cannot delete an element used as a position`() {
            val building = Building(BUILDING_ID_0, position = position)
            val newState = state.updateStorage(Storage(building))

            assertCanDelete(newState, BUILDING_ID_0)
        }

        private fun <ID : Id<ID>> assertCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(TOWN_MAP_ID_0).addId(blockingId), state.canDeleteTownMap(TOWN_MAP_ID_0))
        }
    }

}