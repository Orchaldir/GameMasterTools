package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.AddAbstractBuilding
import at.orchaldir.gm.core.action.RemoveAbstractBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.town.AbstractBuildingTile
import at.orchaldir.gm.core.model.world.town.BuildingTile
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AbstractBuildingTest {

    private val ABSTRACT_TILE = TownTile(construction = AbstractBuildingTile)
    private val BUILDING_TILE = TownTile(construction = BuildingTile(BUILDING_ID_0))
    private val STREET_TILE = TownTile(construction = StreetTile(STREET_TYPE_ID_0, STREET_ID_0))
    private val EMPTY = TownTile()
    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Street(STREET_ID_0)),
            Storage(StreetTemplate(STREET_TYPE_ID_0)),
            Storage(Town(TOWN_ID_0)),
        )
    )

    @Nested
    inner class AddAbstractBuildingTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = AddAbstractBuilding(UNKNOWN_TOWN_ID, 0)

            assertIllegalArgument("Requires unknown Town 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val action = AddAbstractBuilding(TOWN_ID_0, 100)

            assertIllegalArgument("Tile 100 is outside the map!") {
                REDUCER.invoke(
                    STATE,
                    action
                )
            }
        }

        @Test
        fun `Tile is already an abstract building`() {
            testTileNotEmpty(ABSTRACT_TILE)
        }

        @Test
        fun `Tile is already a building`() {
            testTileNotEmpty(BUILDING_TILE)
        }

        @Test
        fun `Tile is already a street`() {
            testTileNotEmpty(STREET_TILE)
        }

        private fun testTileNotEmpty(townTile: TownTile) {
            val map = TileMap2d(townTile)
            val town = Town(TOWN_ID_0, map = map)
            val state = STATE.updateStorage(Storage(town))
            val action = AddAbstractBuilding(TOWN_ID_0, 0)

            assertIllegalArgument("Tile 0 is not empty!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully set a street`() {
            val map = TileMap2d(EMPTY)
            val town = Town(TOWN_ID_0, map = map)
            val state = STATE.updateStorage(Storage(town))
            val action = AddAbstractBuilding(TOWN_ID_0, 0)

            assertEquals(
                AbstractBuildingTile,
                REDUCER.invoke(state, action).first.getTownStorage().getOrThrow(TOWN_ID_0).map.getTile(0)?.construction
            )
        }
    }

    @Nested
    inner class RemoveAbstractBuildingTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = RemoveAbstractBuilding(TOWN_ID_0, 0)

            assertIllegalArgument("Requires unknown Town 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val action = RemoveAbstractBuilding(TOWN_ID_0, 100)

            assertIllegalArgument("Tile 100 is outside the map!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Tile is already empty`() {
            val action = RemoveAbstractBuilding(TOWN_ID_0, 0)

            assertIllegalArgument("Tile 0 is not an abstract building!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Tile is a building`() {
            testWrongType(BUILDING_TILE)
        }

        @Test
        fun `Tile is a street`() {
            testWrongType(STREET_TILE)
        }

        private fun testWrongType(tile: TownTile) {
            val town = Town(TOWN_ID_0, map = TileMap2d(tile))
            val state = State(Storage(town))
            val action = RemoveAbstractBuilding(TOWN_ID_0, 0)

            assertIllegalArgument("Tile 0 is not an abstract building!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully removed an abstract building`() {
            val town = Town(TOWN_ID_0, map = TileMap2d(ABSTRACT_TILE))
            val state = State(Storage(town))
            val action = RemoveAbstractBuilding(TOWN_ID_0, 0)

            assertEquals(EMPTY, REDUCER.invoke(state, action).first.getTownStorage().get(TOWN_ID_0)?.map?.getTile(0))
        }

    }

}