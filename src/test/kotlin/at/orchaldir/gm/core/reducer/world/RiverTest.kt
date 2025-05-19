package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.NAME
import at.orchaldir.gm.core.action.DeleteRiver
import at.orchaldir.gm.core.action.UpdateRiver
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.RiverTerrain
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = RiverId(0)

class RiverTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing river`() {
            val state = State(Storage(River(ID0)))
            val action = DeleteRiver(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getRiverStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteRiver(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a town`() {
            val action = DeleteRiver(ID0)
            val state = State(
                listOf(
                    Storage(River(ID0)),
                    Storage(TownMap(TownMapId(0), map = TileMap2d(TownTile(RiverTerrain(ID0)))))
                )
            )

            assertFailsWith<IllegalArgumentException>("River 0 is used") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRiver(River(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(River(ID0)))
            val river = River(ID0, NAME)
            val action = UpdateRiver(river)

            assertEquals(river, REDUCER.invoke(state, action).first.getRiverStorage().get(ID0))
        }
    }

}