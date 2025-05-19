package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.NAME
import at.orchaldir.gm.REGION_ID_0
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
    val state = State(
        listOf(
            Storage(Region(REGION_ID_0)),
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
            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a town`() {
            val map = TileMap2d(TownTile(MountainTerrain(REGION_ID_0)))
            val state = state.updateStorage(Storage(TownMap(TownMapId(0), map = map)))

            assertFailsWith<IllegalArgumentException>("Region 0 is used") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRegion(Region(REGION_ID_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val region = Region(REGION_ID_0, NAME)
            val action = UpdateRegion(region)

            assertEquals(region, REDUCER.invoke(state, action).first.getRegionStorage().get(REGION_ID_0))
        }
    }

}