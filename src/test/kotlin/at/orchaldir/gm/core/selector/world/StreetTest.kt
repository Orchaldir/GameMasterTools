package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.BUILDING_ID_0
import at.orchaldir.gm.STREET_ID_0
import at.orchaldir.gm.STREET_TEMPLATE_ID_0
import at.orchaldir.gm.TOWN_MAP_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.StreetAddress
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.settlement.StreetTile
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.model.world.settlement.SettlementTile
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StreetTest {

    @Nested
    inner class CanDeleteTest {
        private val state = State(
            listOf(
                Storage(Street(STREET_ID_0)),
            )
        )

        @Test
        fun `Cannot delete, if used by a town`() {
            val tile = StreetTile(STREET_TEMPLATE_ID_0, STREET_ID_0)
            val settlementMap = SettlementMap(TOWN_MAP_ID_0, map = TileMap2d(SettlementTile(construction = tile)))
            val newState = state.updateStorage(settlementMap)

            failCanDelete(newState, TOWN_MAP_ID_0)
        }

        @Test
        fun `Cannot delete, because it is used by the address of a building`() {
            val building = Building(BUILDING_ID_0, address = StreetAddress(STREET_ID_0, 4))
            val newState = state.updateStorage(building)

            failCanDelete(newState, BUILDING_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(STREET_ID_0).addId(blockingId), state.canDeleteStreet(STREET_ID_0))
        }
    }

}