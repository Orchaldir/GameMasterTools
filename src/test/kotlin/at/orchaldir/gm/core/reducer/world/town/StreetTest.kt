package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.AddStreetTile
import at.orchaldir.gm.core.action.RemoveStreetTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StreetTest {

    private val ABSTRACT_TILE = TownTile(construction = AbstractBuildingTile)
    private val BUILDING_TILE = TownTile(construction = BuildingTile(BUILDING_ID_0))
    private val STREET_TILE = TownTile(construction = StreetTile(STREET_TYPE_ID_0, STREET_ID_0))
    private val EMPTY = TownTile()
    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Street(STREET_ID_0)),
            Storage(StreetTemplate(STREET_TYPE_ID_0)),
            Storage(TownMap(TOWN_MAP_ID_0)),
        )
    )

    @Nested
    inner class AddStreetTileTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = AddStreetTile(UNKNOWN_TOWN_MAP_ID, 0, STREET_TYPE_ID_0, STREET_ID_0)

            assertIllegalArgument("Requires unknown Town Map 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot use unknown street`() {
            val action = AddStreetTile(TOWN_MAP_ID_0, 0, STREET_TYPE_ID_0, UNKNOWN_STREET_ID)

            assertIllegalArgument("Requires unknown Street 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot use unknown street template`() {
            val action = AddStreetTile(TOWN_MAP_ID_0, 0, UNKNOWN_STREET_TYPE_ID, STREET_ID_0)

            assertIllegalArgument("Requires unknown Street Template 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val action = AddStreetTile(TOWN_MAP_ID_0, 100, STREET_TYPE_ID_0, STREET_ID_0)

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
            val town = TownMap(TOWN_MAP_ID_0, map = map)
            val state = STATE.updateStorage(Storage(town))
            val action = AddStreetTile(TOWN_MAP_ID_0, 0, STREET_TYPE_ID_0, STREET_ID_0)

            assertIllegalArgument("Tile 0 is not empty!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully set a street`() {
            val map = TileMap2d(EMPTY)
            val town = TownMap(TOWN_MAP_ID_0, map = map)
            val state = STATE.updateStorage(Storage(town))
            val action = AddStreetTile(TOWN_MAP_ID_0, 0, STREET_TYPE_ID_0, STREET_ID_0)

            assertEquals(
                STREET_TILE,
                REDUCER.invoke(state, action).first.getTownMapStorage().get(TOWN_MAP_ID_0)?.map?.getTile(0)
            )
        }
    }

    @Nested
    inner class RemoveStreetTileTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = RemoveStreetTile(TOWN_MAP_ID_0, 0)

            assertIllegalArgument("Requires unknown Town Map 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val action = RemoveStreetTile(TOWN_MAP_ID_0, 100)

            assertIllegalArgument("Tile 100 is outside the map!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Tile is already empty`() {
            val action = RemoveStreetTile(TOWN_MAP_ID_0, 0)

            assertIllegalArgument("Tile 0 is not a street!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Tile is an abstract building`() {
            testWrongType(ABSTRACT_TILE)
        }

        @Test
        fun `Tile is a building`() {
            testWrongType(BUILDING_TILE)
        }

        private fun testWrongType(tile: TownTile) {
            val town = TownMap(TOWN_MAP_ID_0, map = TileMap2d(tile))
            val state = State(Storage(town))
            val action = RemoveStreetTile(TOWN_MAP_ID_0, 0)

            assertIllegalArgument("Tile 0 is not a street!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully removed a street`() {
            val town = TownMap(TOWN_MAP_ID_0, map = TileMap2d(STREET_TILE))
            val state = State(Storage(town))
            val action = RemoveStreetTile(TOWN_MAP_ID_0, 0)

            assertEquals(
                EMPTY,
                REDUCER.invoke(state, action).first.getTownMapStorage().get(TOWN_MAP_ID_0)?.map?.getTile(0)
            )
        }

    }

}