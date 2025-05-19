package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.NAME
import at.orchaldir.gm.core.action.DeleteMountain
import at.orchaldir.gm.core.action.UpdateMountain
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.Mountain
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.model.world.town.MountainTerrain
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

private val ID0 = MountainId(0)

class MountainTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing mountain`() {
            val state = State(Storage(Mountain(ID0)))
            val action = DeleteMountain(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getMountainStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteMountain(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a town`() {
            val action = DeleteMountain(ID0)
            val state = State(
                listOf(
                    Storage(Mountain(ID0)),
                    Storage(TownMap(TownMapId(0), map = TileMap2d(TownTile(MountainTerrain(ID0)))))
                )
            )

            assertFailsWith<IllegalArgumentException>("Mountain 0 is used") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateMountain(Mountain(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(Mountain(ID0)))
            val mountain = Mountain(ID0, NAME)
            val action = UpdateMountain(mountain)

            assertEquals(mountain, REDUCER.invoke(state, action).first.getMountainStorage().get(ID0))
        }
    }

}