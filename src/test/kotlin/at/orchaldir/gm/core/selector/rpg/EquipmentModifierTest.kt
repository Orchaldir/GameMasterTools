package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.AMMUNITION_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_MODIFIER_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ammunition.Ammunition
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.OneHandedAxe
import at.orchaldir.gm.core.model.item.equipment.Shield
import at.orchaldir.gm.core.model.item.equipment.style.ScaleArmour
import at.orchaldir.gm.core.model.rpg.combat.ArmorStats
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifier
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponStats
import at.orchaldir.gm.core.model.rpg.combat.ShieldStats
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
        fun `Cannot delete an equipment modifier used by an armor`() {
            val data = BodyArmour(ScaleArmour(), stats = ArmorStats(modifiers = setOf(EQUIPMENT_MODIFIER_ID_0)))
            val element = Equipment(EQUIPMENT_ID_0, data = data)
            val newState = state.updateStorage(element)

            failCanDelete(newState, EQUIPMENT_ID_0)
        }

        @Test
        fun `Cannot delete an equipment modifier used by a shield`() {
            val data = Shield(stats = ShieldStats(modifiers = setOf(EQUIPMENT_MODIFIER_ID_0)))
            val element = Equipment(EQUIPMENT_ID_0, data = data)
            val newState = state.updateStorage(element)

            failCanDelete(newState, EQUIPMENT_ID_0)
        }

        @Test
        fun `Cannot delete an equipment modifier used by a weapon`() {
            val data = OneHandedAxe(stats = MeleeWeaponStats(modifiers = setOf(EQUIPMENT_MODIFIER_ID_0)))
            val element = Equipment(EQUIPMENT_ID_0, data = data)
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