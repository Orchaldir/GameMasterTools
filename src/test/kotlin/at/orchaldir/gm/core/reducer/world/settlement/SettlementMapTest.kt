package at.orchaldir.gm.core.reducer.world.settlement

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Settlement
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.settlement.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SettlementMapTest {

    val settlementMap = SettlementMap(SETTLEMENT_MAP_ID_0)
    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Region(REGION_ID_0)),
            Storage(Street(STREET_ID_0)),
            Storage(StreetTemplate(STREET_TEMPLATE_ID_0)),
            Storage(Settlement(SETTLEMENT_ID_0)),
            Storage(settlementMap),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(settlementMap)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Nested
        inner class MapTest {

            @Test
            fun `Building must exist`() {
                testValid("Building", SettlementTile(construction = BuildingTile(UNKNOWN_BUILDING_ID)))
            }

            @Test
            fun `Hill must exist`() {
                val terrain = HillTerrain(UNKNOWN_REGION_ID)
                testValid("Region", SettlementTile(terrain))
            }

            @Test
            fun `Hill's region must be a mountain'`() {
                val terrain = HillTerrain(REGION_ID_0)
                testValid(SettlementTile(terrain), "Region 0 must be a mountain!")
            }

            @Test
            fun `Mountain must exist`() {
                val terrain = MountainTerrain(UNKNOWN_REGION_ID)
                testValid("Region", SettlementTile(terrain))
            }

            @Test
            fun `Mountain's region must be a mountain'`() {
                val terrain = MountainTerrain(REGION_ID_0)
                testValid(SettlementTile(terrain), "Region 0 must be a mountain!")
            }

            @Test
            fun `River must exist`() {
                val terrain = RiverTerrain(UNKNOWN_RIVER_ID)
                testValid("River", SettlementTile(terrain))
            }

            @Test
            fun `Street template must exist`() {
                testValid("Street Template", SettlementTile(construction = StreetTile(UNKNOWN_STREET_TEMPLATE_ID)))
            }

            @Test
            fun `Street must exist`() {
                val construction = StreetTile(STREET_TEMPLATE_ID_0, UNKNOWN_STREET_ID)
                testValid("Street", SettlementTile(construction = construction))
            }

            private fun testValid(noun: String, tile: SettlementTile) =
                testValid(tile, "Requires unknown $noun 99!")

            private fun testValid(tile: SettlementTile, message: String) {
                val map = TileMap2d(tile)
                val action = UpdateAction(SettlementMap(SETTLEMENT_MAP_ID_0, map = map))

                assertIllegalArgument(message) { REDUCER.invoke(STATE, action) }
            }
        }

        @Test
        fun `Settlement must exist`() {
            val settlement = SettlementMap(SETTLEMENT_MAP_ID_0, settlement = UNKNOWN_SETTLEMENT_ID)
            val action = UpdateAction(settlement)

            assertIllegalArgument("Requires unknown Settlement 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Settlement and date combination must be unique`() {
            val map0 = SettlementMap(SETTLEMENT_MAP_ID_0, settlement = SETTLEMENT_ID_0, date = DAY0)
            val map1 = SettlementMap(SETTLEMENT_MAP_ID_1, settlement = SETTLEMENT_ID_0, date = DAY0)
            val state = STATE.updateStorage(Storage(listOf(settlementMap, map1)))
            val action = UpdateAction(map0)

            assertIllegalArgument("Multiple maps have the same settlement & date combination!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Update is valid`() {
            val settlement = SettlementMap(SETTLEMENT_MAP_ID_0, date = DAY0)
            val action = UpdateAction(settlement)

            assertEquals(settlement, REDUCER.invoke(STATE, action).first.getSettlementMapStorage().get(SETTLEMENT_MAP_ID_0))
        }
    }

}