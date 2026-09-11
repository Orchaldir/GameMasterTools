package at.orchaldir.gm.core.reducer.rpg.equipment

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.equipment.AmmunitionType
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentType
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamage
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.util.quantity.StandardDice
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.DOUBLE
import at.orchaldir.gm.utils.math.ONE
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RangedAttackTest {

    private val STATE = State(
        listOf(
            Storage(AmmunitionType(AMMUNITION_TYPE_ID_0)),
            Storage(DamageType(DAMAGE_TYPE_ID_0)),
            Storage(listOf(Statistic(STATISTIC_ID_0, data = BaseDamage()), Statistic(STATISTIC_ID_1))),
        )
    )
    private val validDamageAmount = SimpleRandomDamage(StandardDice(1, 0))

    @Test
    fun `Check if damage is validated`() {
        val attack = RangedAttack(effect = Damage(validDamageAmount, UNKNOWN_DAMAGE_TYPE_ID))

        assertInvalid(attack, "Requires unknown Damage Type 99!")
    }

    @Test
    fun `Check if range is validated`() {
        val range = StatisticBasedHalfAndMaxRange(UNKNOWN_STATISTIC_ID, ONE, DOUBLE)
        val attack = RangedAttack(range = range)

        assertInvalid(attack, "Requires unknown Statistic 99!")
    }

    @Test
    fun `Check if used skill is validated`() {
        val skill = ModifiedUsedSkill(UNKNOWN_STATISTIC_ID)
        val attack = RangedAttack(skill = skill)

        assertInvalid(attack, "Requires unknown Statistic 99!")
    }

    @Nested
    inner class ShotsTest {

        @Test
        fun `Validate rounds of reload for Thrown`() {
            val attack = RangedAttack(shots = Thrown(-1))

            assertInvalid(attack, "Rounds of reload must be >= 0!")
        }

        @Test
        fun `Validate rounds of reload for SingleShot`() {
            val attack = RangedAttack(shots = SingleShot(AMMUNITION_TYPE_ID_0, -1))

            assertInvalid(attack, "Rounds of reload must be >= 0!")
        }

        @Test
        fun `Validate an unknown ammunition type`() {
            val attack = RangedAttack(shots = SingleShot(UNKNOWN_AMMUNITION_TYPE, 1))

            assertInvalid(attack, "Requires unknown Ammunition Type 99!")
        }
    }

    @Test
    fun `A valid ranged weapon`() {
        val attack = RangedAttack(
            SimpleAccuracy(2),
            Damage(validDamageAmount, DAMAGE_TYPE_ID_0),
            FixedHalfAndMaxRange(10, 20),
            SingleShot(AMMUNITION_TYPE_ID_0, 2),
        )

        assertValid(attack)
    }

    private fun assertValid(attack: RangedAttack) {
        val type = EquipmentType(EQUIPMENT_TYPE_ID_0, rangedAttacks = listOf(attack))

        type.validate(STATE)
    }

    private fun assertInvalid(attack: RangedAttack, message: String) {
        assertIllegalArgument(message) {
            assertValid(attack)
        }
    }

}