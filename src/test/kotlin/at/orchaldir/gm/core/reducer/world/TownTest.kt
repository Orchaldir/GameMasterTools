package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.assertIllegalState
import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.util.OwnedByTown
import at.orchaldir.gm.core.model.util.Ownership
import at.orchaldir.gm.core.model.util.PreviousOwner
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.building.BuildingLot
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.Resize
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = TownId(0)
private val BUILDING0 = BuildingId(0)
private val BUSINESS0 = BusinessId(0)
private val MOUNTAIN0 = MountainId(0)
private val MOUNTAIN1 = MountainId(1)
private val RIVER0 = RiverId(0)
private val STREET0 = StreetId(0)
private val STREET1 = StreetId(1)
private val BUILDING_TILE = TownTile(construction = BuildingTile(BUILDING0))
private val RIVER_TILE = TownTile(RiverTerrain(RIVER0))
private val STREET_TILE = TownTile(construction = StreetTile(STREET0))
private val EMPTY = TownTile()
private val OWNER = Ownership(OwnedByTown(ID0))
private val PREVIOUS_OWNER = Ownership(previousOwners = listOf(PreviousOwner(OwnedByTown(ID0), Day(0))))

class TownTest {

    @Nested
    inner class DeleteTest {

        private val action = DeleteTown(ID0)

        @Test
        fun `Can delete an existing Town`() {
            val state = State(Storage(Town(ID0)))

            assertEquals(0, REDUCER.invoke(state, action).first.getTownStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Nested
        inner class BuildingOwnerTest {

            @Test
            fun `Cannot delete a building owner`() {
                val state = createState(Building(BUILDING0, ownership = OWNER))

                assertIllegalArgument("Cannot delete town 0, because it owns buildings!") {
                    REDUCER.invoke(state, action)
                }
            }

            @Test
            fun `Cannot delete a previous building owner`() {
                val state = createState(Building(BUILDING0, ownership = PREVIOUS_OWNER))

                assertIllegalArgument("Cannot delete town 0, because it previously owned buildings!") {
                    REDUCER.invoke(state, action)
                }
            }
        }

        @Nested
        inner class BusinessOwnerTest {

            @Test
            fun `Cannot delete a business owner`() {
                val state = createState(Business(BUSINESS0, ownership = OWNER))

                assertIllegalArgument("Cannot delete town 0, because it owns businesses!") {
                    REDUCER.invoke(state, action)
                }
            }

            @Test
            fun `Cannot delete a previous business owner`() {
                val state = createState(Business(BUSINESS0, ownership = PREVIOUS_OWNER))

                assertIllegalArgument("Cannot delete town 0, because it previously owned businesses!") {
                    REDUCER.invoke(state, action)
                }
            }
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> createState(element: ELEMENT): State {
            val state = State(
                listOf(
                    Storage(listOf(Town(ID0))),
                    Storage(listOf(element))
                )
            )
            return state
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
            val state = State(listOf(Storage(Street(STREET0))))
            val action = AddStreetTile(ID0, 0, STREET0)

            assertIllegalArgument("Requires unknown Town 0!") { REDUCER.invoke(state, action) }
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
            val map = TileMap2d(EMPTY)
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

            assertIllegalArgument("Requires unknown Town 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val town = Town(ID0)
            val state = State(Storage(town))
            val action = RemoveStreetTile(ID0, 100)

            assertIllegalArgument("Tile 100 is outside the map!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Tile is already empty`() {
            val town = Town(ID0)
            val state = State(Storage(town))
            val action = RemoveStreetTile(ID0, 0)

            assertIllegalArgument("Tile 0 is not a street!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Tile is a building`() {
            val town = Town(ID0, map = TileMap2d(BUILDING_TILE))
            val state = State(Storage(town))
            val action = RemoveStreetTile(ID0, 0)

            assertIllegalArgument("Tile 0 is not a street!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully removed a street`() {
            val town = Town(ID0, map = TileMap2d(STREET_TILE))
            val state = State(Storage(town))
            val action = RemoveStreetTile(ID0, 0)

            assertEquals(EMPTY, REDUCER.invoke(state, action).first.getTownStorage().get(ID0)?.map?.getTile(0))
        }

    }

    @Nested
    inner class SetTerrainTileTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = SetTerrainTile(ID0, TerrainType.Plain, 0, 0)
            assertIllegalArgument("Requires unknown Town 0!") { REDUCER.invoke(State(), action) }
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
            element: ELEMENT,
            type: TerrainType,
            result: Terrain,
        ) {
            val oldMap = TileMap2d(MapSize2d(2, 1), listOf(EMPTY, EMPTY))
            val newMap = TileMap2d(MapSize2d(2, 1), listOf(EMPTY, TownTile(result)))
            val oldTown = Town(ID0, map = oldMap)
            val newTown = Town(ID0, map = newMap)
            val state = State(listOf(Storage(element), Storage(oldTown)))
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
            val oldMap = TileMap2d(MapSize2d(2, 1), listOf(EMPTY, EMPTY))
            val oldTown = Town(ID0, map = oldMap)
            val state = State(listOf(Storage(river), Storage(oldTown)))
            val action = SetTerrainTile(ID0, type, terrainIndex, tileIndex)

            assertIllegalArgument(message) { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class ResizeTownTest {

        @Test
        fun `Cannot resize unknown town`() {
            val action = ResizeTown(ID0, Resize(1))

            assertIllegalArgument("Requires unknown Town 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Resize would reduce width to 0`() {
            val oldMap = TileMap2d(MapSize2d(2, 1), EMPTY)
            val oldTown = Town(ID0, map = oldMap)
            val state = State(listOf(Storage(oldTown)))
            val action = ResizeTown(ID0, Resize(-2), TerrainType.Plain, 0)

            assertIllegalArgument("Width must be greater or equal 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Resize would reduce height to 0`() {
            val oldMap = TileMap2d(MapSize2d(1, 2), EMPTY)
            val oldTown = Town(ID0, map = oldMap)
            val state = State(listOf(Storage(oldTown)))
            val action = ResizeTown(ID0, Resize(heightEnd = -2), TerrainType.Plain, 0)

            assertIllegalArgument("Height must be greater or equal 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Add column at start`() {
            testResize(
                MapSize2d(2, 1),
                listOf(EMPTY, EMPTY),
                ResizeTown(ID0, Resize(1), TerrainType.River, 0),
                MapSize2d(3, 1),
                listOf(RIVER_TILE, EMPTY, EMPTY),
            )
        }

        @Test
        fun `Remove column at start`() {
            testResize(
                MapSize2d(2, 1),
                listOf(EMPTY, RIVER_TILE),
                ResizeTown(ID0, Resize(-1), TerrainType.Mountain, 1),
                MapSize2d(1, 1),
                listOf(RIVER_TILE),
            )
        }

        @Test
        fun `Add column at end`() {
            testResize(
                MapSize2d(2, 1),
                listOf(EMPTY, EMPTY),
                ResizeTown(ID0, Resize(widthEnd = 1), TerrainType.River, 0),
                MapSize2d(3, 1),
                listOf(EMPTY, EMPTY, RIVER_TILE),
            )
        }

        @Test
        fun `Remove column at end`() {
            testResize(
                MapSize2d(2, 1),
                listOf(RIVER_TILE, EMPTY),
                ResizeTown(ID0, Resize(widthEnd = -1), TerrainType.Mountain, 1),
                MapSize2d(1, 1),
                listOf(RIVER_TILE),
            )
        }

        @Test
        fun `Add row at start`() {
            testResize(
                MapSize2d(2, 1),
                listOf(EMPTY, EMPTY),
                ResizeTown(ID0, Resize(heightStart = 1), TerrainType.River, 0),
                MapSize2d(2, 2),
                listOf(RIVER_TILE, RIVER_TILE, EMPTY, EMPTY),
            )
        }

        @Test
        fun `Remove row at start`() {
            testResize(
                MapSize2d(1, 2),
                listOf(EMPTY, RIVER_TILE),
                ResizeTown(ID0, Resize(heightStart = -1), TerrainType.Mountain, 1),
                MapSize2d(1, 1),
                listOf(RIVER_TILE),
            )
        }

        @Test
        fun `Add row at end`() {
            testResize(
                MapSize2d(2, 1),
                listOf(EMPTY, EMPTY),
                ResizeTown(ID0, Resize(heightEnd = 1), TerrainType.River, 0),
                MapSize2d(2, 2),
                listOf(EMPTY, EMPTY, RIVER_TILE, RIVER_TILE),
            )
        }

        @Test
        fun `Remove row at end`() {
            testResize(
                MapSize2d(1, 2),
                listOf(RIVER_TILE, EMPTY),
                ResizeTown(ID0, Resize(heightEnd = -1), TerrainType.Mountain, 1),
                MapSize2d(1, 1),
                listOf(RIVER_TILE),
            )
        }

        @Test
        fun `Resize with a building`() {
            val oldMap = TileMap2d(MapSize2d(2, 1), listOf(EMPTY, BUILDING_TILE))
            val newMap = TileMap2d(
                MapSize2d(4, 2), listOf(
                    EMPTY, EMPTY, EMPTY, EMPTY,
                    EMPTY, EMPTY, EMPTY, BUILDING_TILE
                )
            )
            val oldTown = Town(ID0, map = oldMap)
            val oldBuilding = Building(BUILDING0, lot = BuildingLot(ID0, 1, MapSize2d.square(1)))
            val newBuilding = Building(BUILDING0, lot = BuildingLot(ID0, 7, MapSize2d.square(1)))
            val state = State(listOf(Storage(oldBuilding), Storage(oldTown)))
            val action = ResizeTown(ID0, Resize(2, 0, 1, 0), TerrainType.Plain, 0)

            val newState = REDUCER.invoke(state, action).first

            assertEquals(newBuilding, newState.getBuildingStorage().getOrThrow(BUILDING0))
            assertEquals(newMap, newState.getTownStorage().getOrThrow(ID0).map)
        }

        @Test
        fun `Resize would remove a building`() {
            val oldMap = TileMap2d(MapSize2d(2, 1), listOf(EMPTY, BUILDING_TILE))
            val oldTown = Town(ID0, map = oldMap)
            val oldBuilding = Building(BUILDING0, lot = BuildingLot(ID0, 1, MapSize2d.square(1)))
            val state = State(listOf(Storage(oldBuilding), Storage(oldTown)))
            val action = ResizeTown(ID0, Resize(0, -1, 0, 0), TerrainType.Plain, 0)

            assertIllegalState("Resize would remove building 0!") { REDUCER.invoke(state, action) }
        }

        private fun testResize(
            oldSize: MapSize2d,
            oldTiles: List<TownTile>,
            action: ResizeTown,
            newSize: MapSize2d,
            newTiles: List<TownTile>,
        ) {
            val oldMap = TileMap2d(oldSize, oldTiles)
            val newMap = TileMap2d(newSize, newTiles)
            val oldTown = Town(ID0, map = oldMap)
            val state = State(listOf(Storage(River(RIVER0)), Storage(Mountain(MOUNTAIN1)), Storage(oldTown)))

            assertEquals(newMap, REDUCER.invoke(state, action).first.getTownStorage().getOrThrow(ID0).map)
        }

    }

}