package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteAction
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.settlement.RiverTerrain
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.model.world.settlement.SettlementTile
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeleteTest {
    val action = DeleteAction(RIVER_ID_0)
    val state = State(Storage(River(RIVER_ID_0)))

    @Test
    fun `Can delete an existing element`() {
        assertEquals(0, REDUCER.invoke(state, action).first.getRiverStorage().getSize())
    }

    @Test
    fun `Cannot delete unknown id`() {
        val action = DeleteAction(UNKNOWN_RIVER_ID)

        assertIllegalArgument("Requires unknown River 99!") { REDUCER.invoke(state, action) }
    }

    @Test
    fun `Cannot delete element, if it is used`() {
        val storage = Storage(SettlementMap(SETTLEMENT_MAP_ID_0, map = TileMap2d(SettlementTile(RiverTerrain(RIVER_ID_0)))))
        val newState = state.updateStorage(storage)

        assertCannotDelete(DeleteResult(RIVER_ID_0).addId(SETTLEMENT_MAP_ID_0)) {
            REDUCER.invoke(newState, action)
        }
    }

}