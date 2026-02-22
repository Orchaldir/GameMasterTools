package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.RIVER_ID_0
import at.orchaldir.gm.SETTLEMENT_MAP_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.settlement.RiverTerrain
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.model.world.settlement.SettlementTile
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
        fun `Cannot delete, if used by a settlement`() {
            val state = State(
                listOf(
                    Storage(River(RIVER_ID_0)),
                    Storage(SettlementMap(SETTLEMENT_MAP_ID_0, map = TileMap2d(SettlementTile(RiverTerrain(RIVER_ID_0)))))
                )
            )

            failCanDelete(state, SETTLEMENT_MAP_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(RIVER_ID_0).addId(blockingId), state.canDeleteRiver(RIVER_ID_0))
        }
    }

}