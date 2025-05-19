package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.NAME
import at.orchaldir.gm.REGION_ID_0
import at.orchaldir.gm.REGION_ID_1
import at.orchaldir.gm.UNKNOWN_REGION_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteRegion
import at.orchaldir.gm.core.action.UpdateRegion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.Region
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

class RegionTest {
    val region0 = Region(REGION_ID_0)
    val state = State(
        listOf(
            Storage(region0),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteRegion(REGION_ID_0)

        @Test
        fun `Can delete an existing region`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getRegionStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Region 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a town`() {
            val map = TileMap2d(TownTile(MountainTerrain(REGION_ID_0)))
            val state = state.updateStorage(Storage(TownMap(TownMapId(0), map = map)))

            assertIllegalArgument("Cannot delete Region 0, because it is used!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete, if having subregions`() {
            val region1 = Region(REGION_ID_1, parent = REGION_ID_0)
            val state = state.updateStorage(Storage(listOf(region0, region1)))

            assertIllegalArgument("Cannot delete Region 0, because it is used!") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRegion(region0)

            assertIllegalArgument("Requires unknown Region 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot use unknown parent`() {
            val region = Region(REGION_ID_0, parent = UNKNOWN_REGION_ID)
            val action = UpdateRegion(region)

            assertIllegalArgument("Requires unknown parent!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Update is valid`() {
            val region = Region(REGION_ID_0, NAME)
            val action = UpdateRegion(region)

            assertEquals(region, REDUCER.invoke(state, action).first.getRegionStorage().get(REGION_ID_0))
        }
    }

}