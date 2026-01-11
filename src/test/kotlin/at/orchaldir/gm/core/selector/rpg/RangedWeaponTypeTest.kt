package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.MELEE_WEAPON_TYPE_ID_0
import at.orchaldir.gm.RANGED_WEAPON_TYPE_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Bow
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.OneHandedAxe
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponStats
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponType
import at.orchaldir.gm.core.model.rpg.combat.RangedWeaponStats
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RangedWeaponTypeTest {

    @Nested
    inner class CanDeleteTest {
        private val type = MeleeWeaponType(MELEE_WEAPON_TYPE_ID_0)
        private val state = State(
            listOf(
                Storage(type),
            )
        )

        @Test
        fun `Cannot delete a ranged weapon type used by an equipment`() {
            val data = Bow(stats = RangedWeaponStats(RANGED_WEAPON_TYPE_ID_0))
            val element = Equipment(EQUIPMENT_ID_0, data = data)
            val newState = state.updateStorage(element)

            failCanDelete(newState, EQUIPMENT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(RANGED_WEAPON_TYPE_ID_0).addId(blockingId),
                state.canDeleteRangedWeaponType(RANGED_WEAPON_TYPE_ID_0)
            )
        }
    }

}