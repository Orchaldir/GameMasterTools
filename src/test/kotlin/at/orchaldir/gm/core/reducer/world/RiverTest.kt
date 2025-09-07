package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.NAME
import at.orchaldir.gm.RIVER_ID_0
import at.orchaldir.gm.TOWN_MAP_ID_0
import at.orchaldir.gm.assertCannotDelete
import at.orchaldir.gm.core.action.DeleteRiver
import at.orchaldir.gm.core.action.UpdateRiver
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.town.RiverTerrain
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RiverTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing river`() {
            val state = State(Storage(River(RIVER_ID_0)))
            val action = DeleteRiver(RIVER_ID_0)

            assertEquals(0, REDUCER.invoke(state, action).first.getRiverStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteRiver(RIVER_ID_0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a town`() {
            val action = DeleteRiver(RIVER_ID_0)
            val state = State(
                listOf(
                    Storage(River(RIVER_ID_0)),
                    Storage(TownMap(TOWN_MAP_ID_0, map = TileMap2d(TownTile(RiverTerrain(RIVER_ID_0)))))
                )
            )

            assertCannotDelete(DeleteResult(RIVER_ID_0).addId(TOWN_MAP_ID_0)) {
                REDUCER.invoke(state, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRiver(River(RIVER_ID_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(River(RIVER_ID_0)))
            val river = River(RIVER_ID_0, NAME)
            val action = UpdateRiver(river)

            assertEquals(river, REDUCER.invoke(state, action).first.getRiverStorage().get(RIVER_ID_0))
        }
    }

}