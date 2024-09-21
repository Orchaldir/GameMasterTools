package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = TownId(0)
private val BUILDING0 = BuildingId(0)
private val MOUNTAIN0 = MountainId(0)
private val RIVER0 = RiverId(0)
private val STREET0 = StreetId(0)
private val STREET1 = StreetId(1)
private val BUILDING_TILE = TownTile(construction = BuildingTile(BUILDING0))
private val STREET_TILE = TownTile(construction = StreetTile(STREET0))

class TownTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing Town`() {
            val state = State(Storage(Town(ID0)))
            val action = DeleteTown(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getTownStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteTown(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateTown(Town(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(Town(ID0)))
            val town = Town(ID0, "Test")
            val action = UpdateTown(town)

            assertEquals(town, REDUCER.invoke(state, action).first.getTownStorage().get(ID0))
        }
    }

    @Nested
    inner class AddStreetTileTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = AddStreetTile(ID0, 0, STREET0)

            assertIllegalArgument("Unknown Town 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot use unknown street`() {
            val town = Town(ID0)
            val state = State(Storage(town))
            val action = AddStreetTile(ID0, 0, STREET1)

            assertIllegalArgument("Requires unknown Street 1!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val town = Town(ID0)
            val state = State(listOf(Storage(Street(STREET0)), Storage(town)))
            val action = AddStreetTile(ID0, 100, STREET0)

            assertIllegalArgument("Tile 100 is outside the map!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
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
            val town = Town(ID0, map = map)
            val state = State(listOf(Storage(listOf(Street(STREET0), Street(STREET1))), Storage(town)))
            val action = AddStreetTile(ID0, 0, STREET1)

            assertIllegalArgument("Tile 0 is not empty!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully set a street`() {
            val map = TileMap2d(TownTile())
            val town = Town(ID0, map = map)
            val state = State(listOf(Storage(Street(STREET0)), Storage(town)))
            val action = AddStreetTile(ID0, 0, STREET0)

            assertEquals(STREET_TILE, REDUCER.invoke(state, action).first.getTownStorage().get(ID0)?.map?.getTile(0))
        }

    }

    @Nested
    inner class RemoveStreetTileTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = RemoveStreetTile(ID0, 0)

            assertIllegalArgument("Unknown Town 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val town = Town(ID0)
            val state = State(Storage(town))
            val action = RemoveStreetTile(ID0, 100)

            assertIllegalArgument("Tile 100 is outside the map!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Tile is already empty`() {
            val town = Town(ID0)
            val state = State(Storage(town))
            val action = RemoveStreetTile(ID0, 0)

            assertIllegalArgument("Tile 0 is not a street!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Successfully set a street`() {
            val map = TileMap2d(STREET_TILE)
            val town = Town(ID0, map = map)
            val state = State(Storage(town))
            val action = RemoveStreetTile(ID0, 0)

            assertEquals(TownTile(), REDUCER.invoke(state, action).first.getTownStorage().get(ID0)?.map?.getTile(0))
        }

    }

    @Nested
    inner class SetTerrainTileTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = SetTerrainTile(ID0, TerrainType.Plain, 0, 0)
            assertIllegalArgument("Unknown Town 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Set to Hill`() {
            testSuccess(Mountain(MOUNTAIN0), TerrainType.Hill, HillTerrain(MOUNTAIN0))
        }

        @Test
        fun `Set to mountain`() {
            testSuccess(Mountain(MOUNTAIN0), TerrainType.Mountain, MountainTerrain(MOUNTAIN0))
        }

        @Test
        fun `Set to river`() {
            testSuccess(River(RIVER0), TerrainType.River, RiverTerrain(RIVER0))
        }

        @Test
        fun `Set tile outside the map to hill`() {
            testOutside(Mountain(MOUNTAIN0), TerrainType.Hill)
        }

        @Test
        fun `Set tile outside the map to plain`() {
            testOutside(River(RIVER0), TerrainType.Plain)
        }

        @Test
        fun `Set tile outside the map to mountain`() {
            testOutside(Mountain(MOUNTAIN0), TerrainType.Mountain)
        }

        @Test
        fun `Set tile outside the map to river`() {
            testOutside(River(RIVER0), TerrainType.River)
        }

        @Test
        fun `Set unknown hill`() {
            testUnknown(Mountain(MOUNTAIN0), TerrainType.Hill)
        }

        @Test
        fun `Set unknown mountain`() {
            testUnknown(Mountain(MOUNTAIN0), TerrainType.Mountain)
        }

        @Test
        fun `Set unknown river`() {
            testUnknown(River(RIVER0), TerrainType.River)
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> testSuccess(
            river: ELEMENT,
            type: TerrainType,
            result: Terrain,
        ) {
            val oldMap = TileMap2d(MapSize2d(2, 1), listOf(TownTile(), TownTile()))
            val newMap = TileMap2d(MapSize2d(2, 1), listOf(TownTile(), TownTile(result)))
            val oldTown = Town(ID0, map = oldMap)
            val newTown = Town(ID0, map = newMap)
            val state = State(listOf(Storage(river), Storage(oldTown)))
            val action = SetTerrainTile(ID0, type, 0, 1)

            assertEquals(newTown, REDUCER.invoke(state, action).first.getTownStorage().get(ID0))
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> testOutside(
            element: ELEMENT,
            type: TerrainType,
        ) {
            fail(element, type, 0, 2, "Tile 2 is outside the map!")
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> testUnknown(
            element: ELEMENT,
            type: TerrainType,
        ) {
            fail(element, type, 1, 0, "Requires unknown ${element.id().type()} 1!")
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> fail(
            river: ELEMENT,
            type: TerrainType,
            terrainIndex: Int,
            tileIndex: Int,
            message: String,
        ) {
            val oldMap = TileMap2d(MapSize2d(2, 1), listOf(TownTile(), TownTile()))
            val oldTown = Town(ID0, map = oldMap)
            val state = State(listOf(Storage(river), Storage(oldTown)))
            val action = SetTerrainTile(ID0, type, terrainIndex, tileIndex)

            assertIllegalArgument(message) { REDUCER.invoke(state, action) }
        }
    }

}