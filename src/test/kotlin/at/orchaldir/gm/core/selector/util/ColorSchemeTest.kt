package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.COLOR_SCHEME_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.TITLE_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.selector.character.canDeleteTitle
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ColorSchemeTest {

    @Nested
    inner class CanDeleteTest {
        private val scheme = ColorScheme(COLOR_SCHEME_ID_0)
        private val state = State(
            listOf(
                Storage(scheme),
            )
        )

        @Test
        fun `Cannot delete a scheme used by a character's equipment`() {
            val equipmentMap = EquipmentMap.fromId(EQUIPMENT_ID_0, COLOR_SCHEME_ID_0, BodySlot.Top)
            val character = Character(CHARACTER_ID_0, equipmentMap = equipmentMap)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a scheme used by an equipment`() {
            val equipment = Equipment(EQUIPMENT_ID_0, colorSchemes = setOf(COLOR_SCHEME_ID_0))
            val newState = state.updateStorage(Storage(equipment))

            failCanDelete(newState, EQUIPMENT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(COLOR_SCHEME_ID_0).addId(blockingId), state.canDeleteColorScheme(COLOR_SCHEME_ID_0))
        }
    }

}