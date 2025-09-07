package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteRiver
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.town.RiverTerrain
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeleteTest {
    val action = DeleteRiver(RIVER_ID_0)
    val state = State(Storage(River(RIVER_ID_0)))

    @Test
    fun `Can delete an existing element`() {
        assertEquals(0, REDUCER.invoke(state, action).first.getRiverStorage().getSize())
    }

    @Test
    fun `Cannot delete unknown id`() {
        val action = DeleteRiver(UNKNOWN_RIVER_ID)

        assertIllegalArgument("Requires unknown River 99!") { REDUCER.invoke(state, action) }
    }

    @Test
    fun `Cannot delete element, if it is used`() {
        val storage = Storage(TownMap(TOWN_MAP_ID_0, map = TileMap2d(TownTile(RiverTerrain(RIVER_ID_0)))))
        val newState = state.updateStorage(storage)

        assertCannotDelete(DeleteResult(RIVER_ID_0).addId(TOWN_MAP_ID_0)) {
            REDUCER.invoke(newState, action)
        }
    }

}