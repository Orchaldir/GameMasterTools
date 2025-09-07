package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.MOON_ID_0
import at.orchaldir.gm.RIVER_ID_0
import at.orchaldir.gm.TOWN_MAP_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.town.RiverTerrain
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RiverTest {

    @Nested
    inner class CanDeleteTest {

        @Test
        fun `Cannot delete, if used by a town`() {
            val state = State(
                listOf(
                    Storage(River(RIVER_ID_0)),
                    Storage(TownMap(TOWN_MAP_ID_0, map = TileMap2d(TownTile(RiverTerrain(RIVER_ID_0)))))
                )
            )

            assertCanDelete(state, TOWN_MAP_ID_0)
        }

        private fun <ID : Id<ID>> assertCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(RIVER_ID_0).addId(blockingId), state.canDeleteRiver(RIVER_ID_0))
        }
    }

}