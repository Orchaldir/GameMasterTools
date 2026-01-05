package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.ARMOR_TYPE_ID_0
import at.orchaldir.gm.DAMAGE_TYPE_ID_0
import at.orchaldir.gm.MELEE_WEAPON_TYPE_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DamageTypeTest {

    @Nested
    inner class CanDeleteTest {
        private val damageType = DamageType(DAMAGE_TYPE_ID_0)
        private val state = State(
            listOf(
                Storage(damageType),
            )
        )

        @Test
        fun `Cannot delete a damage type used by an armor`() {
            val protection = DamageResistances(2, mapOf(DAMAGE_TYPE_ID_0 to 2))
            val element = ArmorType(ARMOR_TYPE_ID_0, protection = protection)
            val newState = state.updateStorage(element)

            failCanDelete(newState, ARMOR_TYPE_ID_0)
        }

        @Test
        fun `Cannot delete a damage type used by a melee weapon`() {
            val attack = MeleeAttack(Damage(SimpleRandomDamage(), DAMAGE_TYPE_ID_0))
            val element = MeleeWeaponType(MELEE_WEAPON_TYPE_ID_0, attacks = listOf(attack))
            val newState = state.updateStorage(element)

            failCanDelete(newState, MELEE_WEAPON_TYPE_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(DAMAGE_TYPE_ID_0).addId(blockingId), state.canDeleteDamageType(DAMAGE_TYPE_ID_0))
        }
    }

}