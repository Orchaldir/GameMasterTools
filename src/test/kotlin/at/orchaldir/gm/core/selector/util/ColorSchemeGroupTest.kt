package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.COLOR_SCHEME_GROUP_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.util.render.ColorSchemeGroup
import at.orchaldir.gm.core.model.util.render.UseColorSchemeGroup
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ColorSchemeGroupTest {

    @Nested
    inner class CanDeleteTest {
        private val group = ColorSchemeGroup(COLOR_SCHEME_GROUP_ID_0)
        private val state = State(
            listOf(
                Storage(group),
            )
        )

        @Test
        fun `Cannot delete a group used by an equipment`() {
            val equipment = Equipment(EQUIPMENT_ID_0, colorSchemes = UseColorSchemeGroup(COLOR_SCHEME_GROUP_ID_0))
            val newState = state.updateStorage(equipment)

            failCanDelete(newState, EQUIPMENT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(COLOR_SCHEME_GROUP_ID_0).addId(blockingId),
                state.canDeleteColorSchemeGroup(COLOR_SCHEME_GROUP_ID_0)
            )
        }
    }

}