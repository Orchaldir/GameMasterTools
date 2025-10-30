package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamage
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test

class MeleeWeaponTest {

    private val STATE = State(
        listOf(
            Storage(listOf(DamageType(DAMAGE_TYPE_ID_0))),
            Storage(listOf(Statistic(STATISTIC_ID_0, data = BaseDamage()), Statistic(STATISTIC_ID_1))),
        )
    )
    private val validDamageAmount = SimpleRandomDamage(SimpleModifiedDice(1, 0))


    @Test
    fun `Cannot use an unknown damage type`() {
        val attack = MeleeAttack(Damage(validDamageAmount, UNKNOWN_DAMAGE_TYPE_ID))

        assertInvalidWeapon(attack, "Requires unknown Damage Type 99!")
    }

    @Test
    fun `Cannot use an unknown base damage`() {
        val attack = MeleeAttack(Damage(ModifiedBaseDamage(UNKNOWN_STATISTIC_ID), DAMAGE_TYPE_ID_0))

        assertInvalidWeapon(attack, "Requires unknown Statistic 99!")
    }

    private fun assertInvalidWeapon(attack: MeleeAttack, message: String) {
        val weapon = MeleeWeapon(MELEE_WEAPON_ID_0, attacks = listOf(attack))

        assertIllegalArgument(message) { weapon.validate(STATE) }
    }

}