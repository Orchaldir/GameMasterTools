package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.STREET_ID_0
import at.orchaldir.gm.STREET_TEMPLATE_ID_0
import at.orchaldir.gm.TOWN_MAP_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownTile
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
            val townMap = TownMap(TOWN_MAP_ID_0, map = TileMap2d(TownTile(construction = tile)))
            val newState = state.updateStorage(Storage(townMap))

            failCanDelete(newState, TOWN_MAP_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(STREET_TEMPLATE_ID_0).addId(blockingId),
                state.canDeleteStreetTemplate(STREET_TEMPLATE_ID_0)
            )
        }
    }

}