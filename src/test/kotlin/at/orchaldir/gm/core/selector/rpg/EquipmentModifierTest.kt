package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.AMMUNITION_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_MODIFIER_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ammunition.Ammunition
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentModifier
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentStats
import at.orchaldir.gm.core.selector.rpg.equipment.canDeleteEquipmentModifier
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EquipmentModifierTest {

    @Nested
    inner class CanDeleteTest {
        private val type = EquipmentModifier(EQUIPMENT_MODIFIER_ID_0)
        private val state = State(
            listOf(
                Storage(type),
            )
        )

        @Test
        fun `Cannot delete an equipment modifier used by an ammunition`() {
            val element = Ammunition(AMMUNITION_ID_0, modifiers = setOf(EQUIPMENT_MODIFIER_ID_0))
            val newState = state.updateStorage(element)

            failCanDelete(newState, AMMUNITION_ID_0)
        }

        @Test
        fun `Cannot delete an equipment modifier used by an equipment`() {
            val stats = EquipmentStats(modifiers = setOf(EQUIPMENT_MODIFIER_ID_0))
            val element = Equipment(EQUIPMENT_ID_0, stats = stats)
            val newState = state.updateStorage(element)

            failCanDelete(newState, EQUIPMENT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(EQUIPMENT_MODIFIER_ID_0).addId(blockingId),
                state.canDeleteEquipmentModifier(EQUIPMENT_MODIFIER_ID_0)
            )
        }
    }

}