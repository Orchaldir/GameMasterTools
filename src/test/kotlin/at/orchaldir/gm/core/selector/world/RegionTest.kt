package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.InRegion
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.town.MountainTerrain
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RegionTest {

    @Nested
    inner class CanDeleteTest {
        private val region = Region(REGION_ID_0)
        private val state = State(
            listOf(
                Storage(region),
            )
        )
        private val inRegion = InRegion(REGION_ID_0)

        @Test
        fun `Cannot delete, if used by a town map`() {
            val map = TileMap2d(TownTile(MountainTerrain(REGION_ID_0)))
            val newState = state.updateStorage(Storage(TownMap(TOWN_MAP_ID_0, map = map)))
            val expected = DeleteResult(REGION_ID_0).addId(TOWN_MAP_ID_0)

            assertCanDelete(newState, expected)
        }

        @Test
        fun `Cannot delete, if having subregions`() {
            val region1 = Region(REGION_ID_1, position = InRegion(REGION_ID_0))
            val newState = state.updateStorage(Storage(listOf(region, region1)))
            val expected = DeleteResult(REGION_ID_0).addId(REGION_ID_1)

            assertCanDelete(newState, expected)
        }

        @Test
        fun `Cannot delete a region used as home`() {
            val housingStatus = History<Position>(inRegion)
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(Storage(character))
            val expected = DeleteResult(REGION_ID_0).addId(CHARACTER_ID_0)

            assertCanDelete(newState, expected)
        }

        @Test
        fun `Cannot delete a region used as a position`() {
            val plane = Business(BUSINESS_ID_0, position = inRegion)
            val newState = state.updateStorage(Storage(plane))
            val expected = DeleteResult(REGION_ID_0).addId(BUSINESS_ID_0)

            assertCanDelete(newState, expected)
        }

        private fun assertCanDelete(state: State, expected: DeleteResult) {
            assertEquals(expected, state.canDeleteRegion(REGION_ID_0))
        }
    }

}