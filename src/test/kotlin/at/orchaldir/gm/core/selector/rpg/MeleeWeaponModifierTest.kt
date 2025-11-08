package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.MELEE_WEAPON_MODIFIER_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.OneHandedAxe
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponStats
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponModifier
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MeleeWeaponModifierTest {

    @Nested
    inner class CanDeleteTest {
        private val type = MeleeWeaponModifier(MELEE_WEAPON_MODIFIER_ID_0)
        private val state = State(
            listOf(
                Storage(type),
            )
        )

        @Test
        fun `Cannot delete a melee weapon modifier used by an equipment`() {
            val data = OneHandedAxe(stats = MeleeWeaponStats(modifiers = setOf(MELEE_WEAPON_MODIFIER_ID_0)))
            val element = Equipment(EQUIPMENT_ID_0, data = data)
            val newState = state.updateStorage(Storage(element))

            failCanDelete(newState, EQUIPMENT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(MELEE_WEAPON_MODIFIER_ID_0).addId(blockingId),
                state.canDeleteMeleeWeaponModifier(MELEE_WEAPON_MODIFIER_ID_0)
            )
        }
    }

}