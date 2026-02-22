package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.STREET_ID_0
import at.orchaldir.gm.STREET_TEMPLATE_ID_0
import at.orchaldir.gm.SETTLEMENT_MAP_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.settlement.StreetTile
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.model.world.settlement.SettlementTile
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.map.TileMap2d
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StreetTemplateTest {

    @Nested
    inner class CanDeleteTest {
        private val state = State(
            listOf(
                Storage(StreetTemplate(STREET_TEMPLATE_ID_0)),
            )
        )

        @Test
        fun `Cannot delete, if used by a town`() {
            val tile = StreetTile(STREET_TEMPLATE_ID_0, STREET_ID_0)
            val settlementMap = SettlementMap(SETTLEMENT_MAP_ID_0, map = TileMap2d(SettlementTile(construction = tile)))
            val newState = state.updateStorage(settlementMap)

            failCanDelete(newState, SETTLEMENT_MAP_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(STREET_TEMPLATE_ID_0).addId(blockingId),
                state.canDeleteStreetTemplate(STREET_TEMPLATE_ID_0)
            )
        }
    }

}