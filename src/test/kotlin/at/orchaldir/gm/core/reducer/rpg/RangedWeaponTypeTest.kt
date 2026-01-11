package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamage
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.DOUBLE
import at.orchaldir.gm.utils.math.ONE
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RangedWeaponTypeTest {

    private val STATE = State(
        listOf(
            Storage(listOf(DamageType(DAMAGE_TYPE_ID_0))),
            Storage(listOf(Statistic(STATISTIC_ID_0, data = BaseDamage()), Statistic(STATISTIC_ID_1))),
        )
    )
    private val validDamageAmount = SimpleRandomDamage(SimpleModifiedDice(1, 0))

    @Test
    fun `Check if damage is validated`() {
        val attack = RangedAttack(effect = Damage(validDamageAmount, UNKNOWN_DAMAGE_TYPE_ID))

        assertInvalidWeapon(attack, "Requires unknown Damage Type 99!")
    }

    @Test
    fun `Check if range is validated`() {
        val range = StatisticBasedHalfAndMaxRange(UNKNOWN_STATISTIC_ID, ONE, DOUBLE)
        val attack = RangedAttack(range = range)

        assertInvalidWeapon(attack, "Requires unknown Statistic 99!")
    }

    @Nested
    inner class ShotsTest {

        @Test
        fun `Validate rounds of reload for Thrown`() {
            val attack = RangedAttack(shots = Thrown(-1))

            assertInvalidWeapon(attack, "Rounds of reload must be >= 0!")
        }

        @Test
        fun `Validate rounds of reload for SingleShot`() {
            val attack = RangedAttack(shots = SingleShot(-1))

            assertInvalidWeapon(attack, "Rounds of reload must be >= 0!")
        }
    }

    @Test
    fun `A valid ranged weapon`() {
        val attack = RangedAttack(
            SimpleAccuracy(2),
            Damage(validDamageAmount, DAMAGE_TYPE_ID_0),
            FixedHalfAndMaxRange(10, 20),
            SingleShot(2),
        )

        assertValidWeapon(attack)
    }

    private fun assertValidWeapon(attack: RangedAttack) {
        val weapon = RangedWeaponType(RANGED_WEAPON_TYPE_ID_0, attacks = listOf(attack))

        weapon.validate(STATE)
    }

    private fun assertInvalidWeapon(attack: RangedAttack, message: String) {
        val weapon = RangedWeaponType(RANGED_WEAPON_TYPE_ID_0, attacks = listOf(attack))

        assertIllegalArgument(message) { weapon.validate(STATE) }
    }

}