package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.DAMAGE_TYPE_ID_0
import at.orchaldir.gm.MELEE_WEAPON_ID_0
import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.STATISTIC_ID_1
import at.orchaldir.gm.UNKNOWN_DAMAGE_TYPE_ID
import at.orchaldir.gm.UNKNOWN_STATISTIC_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.model.rpg.combat.Damage
import at.orchaldir.gm.core.model.rpg.combat.DamageType
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeapon
import at.orchaldir.gm.core.model.rpg.combat.SimpleRandomDamage
import at.orchaldir.gm.core.model.rpg.statistic.Attribute
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamage
import at.orchaldir.gm.core.model.rpg.statistic.BasedOnStatistic
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MeleeWeaponTest {

    private val STATE = State(
        listOf(
            Storage(listOf(DamageType(DAMAGE_TYPE_ID_0))),
            Storage(listOf(Statistic(STATISTIC_ID_0, data = BaseDamage()), Statistic(STATISTIC_ID_1))),
        )
    )
    private val validDamageAmount = SimpleRandomDamage(SimpleModifiedDice(1, 0))

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot use an unknown damage type`() {
            val attack = MeleeAttack(Damage(validDamageAmount, UNKNOWN_DAMAGE_TYPE_ID))
            val weapon = MeleeWeapon(MELEE_WEAPON_ID_0, attacks = listOf(attack))

            assertIllegalArgument("Requires unknown Damage Type 99!") { weapon.validate(STATE) }
        }
    }

}