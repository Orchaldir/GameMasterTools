package at.orchaldir.gm.core.reducer.world.settlement

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.AddAbstractBuilding
import at.orchaldir.gm.core.action.RemoveAbstractBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.settlement.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AbstractBuildingTest {

    private val ABSTRACT_TILE = SettlementTile(construction = AbstractBuildingTile)
    private val BUILDING_TILE = SettlementTile(construction = BuildingTile(BUILDING_ID_0))
    private val STREET_TILE = SettlementTile(construction = StreetTile(STREET_TEMPLATE_ID_0, STREET_ID_0))
    private val EMPTY = SettlementTile()
    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Street(STREET_ID_0)),
            Storage(StreetTemplate(STREET_TEMPLATE_ID_0)),
            Storage(SettlementMap(SETTLEMENT_MAP_ID_0)),
        )
    )

    @Nested
    inner class AddAbstractBuildingTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = AddAbstractBuilding(UNKNOWN_SETTLEMENT_MAP_ID, 0)

            assertIllegalArgument("Requires unknown Town Map 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val action = AddAbstractBuilding(SETTLEMENT_MAP_ID_0, 100)

            assertIllegalArgument("Tile 100 is outside the map!") {
                REDUCER.invoke(STATE, action)
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

        @Test
        fun `Large building is partly outside the map`() {
            val action = AddAbstractBuilding(SETTLEMENT_MAP_ID_0, 9, MapSize2d.square(2))

            assertIllegalState("Lot with index 9 & size 2 x 2 is outside the map!") { REDUCER.invoke(STATE, action) }
        }

        private fun testTileNotEmpty(settlementTile: SettlementTile) {
            val map = TileMap2d(settlementTile)
            val town = SettlementMap(SETTLEMENT_MAP_ID_0, map = map)
            val state = STATE.updateStorage(town)
            val action = AddAbstractBuilding(SETTLEMENT_MAP_ID_0, 0)

            assertIllegalArgument("Tile 0 is not empty!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully set an abstract building`() {
            val map = TileMap2d(EMPTY)
            val town = SettlementMap(SETTLEMENT_MAP_ID_0, map = map)
            val state = STATE.updateStorage(town)
            val action = AddAbstractBuilding(SETTLEMENT_MAP_ID_0, 0)

            assertEquals(
                AbstractBuildingTile,
                REDUCER.invoke(state, action).first.getSettlementMapStorage()
                    .getOrThrow(SETTLEMENT_MAP_ID_0).map.getTile(0)?.construction
            )
        }
    }

    @Nested
    inner class RemoveAbstractBuildingTest {

        @Test
        fun `Cannot update unknown town`() {
            val action = RemoveAbstractBuilding(SETTLEMENT_MAP_ID_0, 0)

            assertIllegalArgument("Requires unknown Town Map 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Tile is outside the map`() {
            val action = RemoveAbstractBuilding(SETTLEMENT_MAP_ID_0, 100)

            assertIllegalArgument("Tile 100 is outside the map!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Tile is already empty`() {
            val action = RemoveAbstractBuilding(SETTLEMENT_MAP_ID_0, 0)

            assertIllegalState("Tile 0 is not an abstract building!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Tile is a building`() {
            testWrongType(BUILDING_TILE)
        }

        @Test
        fun `Tile is a street`() {
            testWrongType(STREET_TILE)
        }

        private fun testWrongType(tile: SettlementTile) {
            val town = SettlementMap(SETTLEMENT_MAP_ID_0, map = TileMap2d(tile))
            val state = State(Storage(town))
            val action = RemoveAbstractBuilding(SETTLEMENT_MAP_ID_0, 0)

            assertIllegalState("Tile 0 is not an abstract building!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successfully removed an abstract building`() {
            val town = SettlementMap(SETTLEMENT_MAP_ID_0, map = TileMap2d(ABSTRACT_TILE))
            val state = State(Storage(town))
            val action = RemoveAbstractBuilding(SETTLEMENT_MAP_ID_0, 0)

            assertEquals(
                EMPTY,
                REDUCER.invoke(state, action).first.getSettlementMapStorage().get(SETTLEMENT_MAP_ID_0)?.map?.getTile(0)
            )
        }

    }

}