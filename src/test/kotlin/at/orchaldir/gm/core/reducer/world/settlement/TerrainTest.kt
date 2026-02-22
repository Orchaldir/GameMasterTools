package at.orchaldir.gm.core.reducer.world.settlement

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.ResizeTerrain
import at.orchaldir.gm.core.action.SetTerrainTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.InSettlementMap
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.settlement.*
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

class TerrainTest {

    private val BUILDING_TILE = SettlementTile(construction = BuildingTile(BUILDING_ID_0))
    private val RIVER_TILE = SettlementTile(RiverTerrain(RIVER_ID_0))
    private val EMPTY = SettlementTile()

    @Nested
    inner class SetTerrainTileTest {

        @Test
        fun `Cannot update unknown settlement`() {
            val action = SetTerrainTile(SETTLEMENT_MAP_ID_0, TerrainType.Plain, 0, 0)
            assertIllegalArgument("Requires unknown Settlement Map 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Set to Hill`() {
            testSuccess(Region(REGION_ID_0), TerrainType.Hill, HillTerrain(REGION_ID_0))
        }

        @Test
        fun `Set to mountain`() {
            testSuccess(Region(REGION_ID_0), TerrainType.Mountain, MountainTerrain(REGION_ID_0))
        }

        @Test
        fun `Set to river`() {
            testSuccess(River(RIVER_ID_0), TerrainType.River, RiverTerrain(RIVER_ID_0))
        }

        @Test
        fun `Set tile outside the map to hill`() {
            testOutside(Region(REGION_ID_0), TerrainType.Hill)
        }

        @Test
        fun `Set tile outside the map to plain`() {
            testOutside(River(RIVER_ID_0), TerrainType.Plain)
        }

        @Test
        fun `Set tile outside the map to mountain`() {
            testOutside(Region(REGION_ID_0), TerrainType.Mountain)
        }

        @Test
        fun `Set tile outside the map to river`() {
            testOutside(River(RIVER_ID_0), TerrainType.River)
        }

        @Test
        fun `Set unknown hill`() {
            testUnknown(Region(REGION_ID_0), TerrainType.Hill)
        }

        @Test
        fun `Set unknown mountain`() {
            testUnknown(Region(REGION_ID_0), TerrainType.Mountain)
        }

        @Test
        fun `Set unknown river`() {
            testUnknown(River(RIVER_ID_0), TerrainType.River)
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> testSuccess(
            element: ELEMENT,
            type: TerrainType,
            result: Terrain,
        ) {
            val oldMap = TileMap2d(MapSize2d(2, 1), listOf(EMPTY, EMPTY))
            val newMap = TileMap2d(MapSize2d(2, 1), listOf(EMPTY, SettlementTile(result)))
            val oldSettlement = SettlementMap(SETTLEMENT_MAP_ID_0, map = oldMap)
            val newSettlement = SettlementMap(SETTLEMENT_MAP_ID_0, map = newMap)
            val state = State(listOf(Storage(element), Storage(oldSettlement)))
            val action = SetTerrainTile(SETTLEMENT_MAP_ID_0, type, 0, 1)

            assertEquals(newSettlement, REDUCER.invoke(state, action).first.getSettlementMapStorage().get(SETTLEMENT_MAP_ID_0))
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
            val oldSettlement = SettlementMap(SETTLEMENT_MAP_ID_0, map = oldMap)
            val state = State(listOf(Storage(river), Storage(oldSettlement)))
            val action = SetTerrainTile(SETTLEMENT_MAP_ID_0, type, terrainIndex, tileIndex)

            assertIllegalArgument(message) { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class ResizeSettlementTest {

        @Test
        fun `Cannot resize unknown settlement`() {
            val action = ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(1))

            assertIllegalArgument("Requires unknown Settlement Map 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Resize would reduce width to 0`() {
            val oldMap = TileMap2d(MapSize2d(2, 1), EMPTY)
            val oldSettlement = SettlementMap(SETTLEMENT_MAP_ID_0, map = oldMap)
            val state = State(listOf(Storage(oldSettlement)))
            val action = ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(-2), TerrainType.Plain, 0)

            assertIllegalArgument("Width must be greater or equal 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Resize would reduce height to 0`() {
            val oldMap = TileMap2d(MapSize2d(1, 2), EMPTY)
            val oldSettlement = SettlementMap(SETTLEMENT_MAP_ID_0, map = oldMap)
            val state = State(listOf(Storage(oldSettlement)))
            val action = ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(heightEnd = -2), TerrainType.Plain, 0)

            assertIllegalArgument("Height must be greater or equal 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Add column at start`() {
            testResize(
                MapSize2d(2, 1),
                listOf(EMPTY, EMPTY),
                ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(1), TerrainType.River, 0),
                MapSize2d(3, 1),
                listOf(RIVER_TILE, EMPTY, EMPTY),
            )
        }

        @Test
        fun `Remove column at start`() {
            testResize(
                MapSize2d(2, 1),
                listOf(EMPTY, RIVER_TILE),
                ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(-1), TerrainType.Mountain, 1),
                MapSize2d(1, 1),
                listOf(RIVER_TILE),
            )
        }

        @Test
        fun `Add column at end`() {
            testResize(
                MapSize2d(2, 1),
                listOf(EMPTY, EMPTY),
                ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(widthEnd = 1), TerrainType.River, 0),
                MapSize2d(3, 1),
                listOf(EMPTY, EMPTY, RIVER_TILE),
            )
        }

        @Test
        fun `Remove column at end`() {
            testResize(
                MapSize2d(2, 1),
                listOf(RIVER_TILE, EMPTY),
                ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(widthEnd = -1), TerrainType.Mountain, 1),
                MapSize2d(1, 1),
                listOf(RIVER_TILE),
            )
        }

        @Test
        fun `Add row at start`() {
            testResize(
                MapSize2d(2, 1),
                listOf(EMPTY, EMPTY),
                ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(heightStart = 1), TerrainType.River, 0),
                MapSize2d(2, 2),
                listOf(RIVER_TILE, RIVER_TILE, EMPTY, EMPTY),
            )
        }

        @Test
        fun `Remove row at start`() {
            testResize(
                MapSize2d(1, 2),
                listOf(EMPTY, RIVER_TILE),
                ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(heightStart = -1), TerrainType.Mountain, 1),
                MapSize2d(1, 1),
                listOf(RIVER_TILE),
            )
        }

        @Test
        fun `Add row at end`() {
            testResize(
                MapSize2d(2, 1),
                listOf(EMPTY, EMPTY),
                ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(heightEnd = 1), TerrainType.River, 0),
                MapSize2d(2, 2),
                listOf(EMPTY, EMPTY, RIVER_TILE, RIVER_TILE),
            )
        }

        @Test
        fun `Remove row at end`() {
            testResize(
                MapSize2d(1, 2),
                listOf(RIVER_TILE, EMPTY),
                ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(heightEnd = -1), TerrainType.Mountain, 1),
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
            val oldSettlement = SettlementMap(SETTLEMENT_MAP_ID_0, map = oldMap)
            val oldBuilding = Building(BUILDING_ID_0, position = InSettlementMap(SETTLEMENT_MAP_ID_0, 1))
            val newBuilding = Building(BUILDING_ID_0, position = InSettlementMap(SETTLEMENT_MAP_ID_0, 7))
            val state = State(listOf(Storage(oldBuilding), Storage(oldSettlement)))
            val action = ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(2, 0, 1, 0), TerrainType.Plain, 0)

            val newState = REDUCER.invoke(state, action).first

            assertEquals(newBuilding, newState.getBuildingStorage().getOrThrow(BUILDING_ID_0))
            assertEquals(newMap, newState.getSettlementMapStorage().getOrThrow(SETTLEMENT_MAP_ID_0).map)
        }

        @Test
        fun `Resize would remove a building`() {
            val oldMap = TileMap2d(MapSize2d(2, 1), listOf(EMPTY, BUILDING_TILE))
            val oldSettlement = SettlementMap(SETTLEMENT_MAP_ID_0, map = oldMap)
            val oldBuilding = Building(BUILDING_ID_0, position = InSettlementMap(SETTLEMENT_MAP_ID_0, 1))
            val state = State(listOf(Storage(oldBuilding), Storage(oldSettlement)))
            val action = ResizeTerrain(SETTLEMENT_MAP_ID_0, Resize(0, -1, 0, 0), TerrainType.Plain, 0)

            assertIllegalState("Resize would remove building 0!") { REDUCER.invoke(state, action) }
        }

        private fun testResize(
            oldSize: MapSize2d,
            oldTiles: List<SettlementTile>,
            action: ResizeTerrain,
            newSize: MapSize2d,
            newTiles: List<SettlementTile>,
        ) {
            val oldMap = TileMap2d(oldSize, oldTiles)
            val newMap = TileMap2d(newSize, newTiles)
            val oldSettlement = SettlementMap(SETTLEMENT_MAP_ID_0, map = oldMap)
            val state = State(listOf(Storage(River(RIVER_ID_0)), Storage(Region(REGION_ID_1)), Storage(oldSettlement)))

            assertEquals(newMap, REDUCER.invoke(state, action).first.getSettlementMapStorage().getOrThrow(SETTLEMENT_MAP_ID_0).map)
        }

    }

}