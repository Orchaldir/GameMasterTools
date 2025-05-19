package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.NAME
import at.orchaldir.gm.core.action.DeleteRegion
import at.orchaldir.gm.core.action.UpdateRegion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.RegionId
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

private val ID0 = RegionId(0)

class MountainTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing mountain`() {
            val state = State(Storage(Region(ID0)))
            val action = DeleteRegion(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getRegionStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteRegion(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a town`() {
            val action = DeleteRegion(ID0)
            val state = State(
                listOf(
                    Storage(Region(ID0)),
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
            val action = UpdateRegion(Region(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(Region(ID0)))
            val mountain = Region(ID0, NAME)
            val action = UpdateRegion(mountain)

            assertEquals(mountain, REDUCER.invoke(state, action).first.getRegionStorage().get(ID0))
        }
    }

}