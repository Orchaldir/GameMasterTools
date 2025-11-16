package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.MELEE_WEAPON_MODIFIER_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.OneHandedAxe
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponModifier
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponStats
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

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(MELEE_WEAPON_MODIFIER_ID_0).addId(blockingId),
                state.canDeleteMeleeWeaponModifier(MELEE_WEAPON_MODIFIER_ID_0)
            )
        }
    }

}