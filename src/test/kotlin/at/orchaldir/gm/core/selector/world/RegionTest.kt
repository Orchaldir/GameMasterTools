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
import at.orchaldir.gm.utils.Id
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
        private val position = InRegion(REGION_ID_0)

        @Test
        fun `Cannot delete, if used by a town map`() {
            val map = TileMap2d(TownTile(MountainTerrain(REGION_ID_0)))
            val newState = state.updateStorage(Storage(TownMap(TOWN_MAP_ID_0, map = map)))

            failCanDelete(newState, TOWN_MAP_ID_0)
        }

        @Test
        fun `Cannot delete, if having subregions`() {
            val region1 = Region(REGION_ID_1, position = InRegion(REGION_ID_0))
            val newState = state.updateStorage(Storage(listOf(region, region1)))

            failCanDelete(newState, REGION_ID_1)
        }

        @Test
        fun `Cannot delete an element used as home`() {
            val housingStatus = History<Position>(position)
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete an element used as a position`() {
            val business = Business(BUSINESS_ID_0, position = position)
            val newState = state.updateStorage(Storage(business))

            failCanDelete(newState, BUSINESS_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(REGION_ID_0).addId(blockingId), state.canDeleteRegion(REGION_ID_0))
        }
    }

}