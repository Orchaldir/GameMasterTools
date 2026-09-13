package at.orchaldir.gm.core.reducer.rpg.equipment

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentType
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamage
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.util.quantity.StandardDice
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MeleeAttackTest {

    private val STATE = State(
        listOf(
            Storage(listOf(DamageType(DAMAGE_TYPE_ID_0))),
            Storage(listOf(Statistic(STATISTIC_ID_0, data = BaseDamage()), Statistic(STATISTIC_ID_1))),
        )
    )
    private val validDamageAmount = SimpleRandomDamage(StandardDice(1, 0))

    @Nested
    inner class DamageTest {

        @Test
        fun `Cannot use an unknown damage type`() {
            val attack = MeleeAttack(Damage(validDamageAmount, UNKNOWN_DAMAGE_TYPE_ID))

            assertInvalid(attack, "Requires unknown Damage Type 99!")
        }

        @Test
        fun `Cannot use an unknown statistic`() {
            val attack = MeleeAttack(Damage(StatisticBasedDamage(UNKNOWN_STATISTIC_ID), DAMAGE_TYPE_ID_0))

            assertInvalid(attack, "Requires unknown Statistic 99!")
        }

        @Test
        fun `A valid amount of dice for statistic based damage`() {
            STATE.config.rpg.damage.dice.toIntRange().forEach {
                val amount = StatisticBasedDamage(STATISTIC_ID_0, StandardDice(it))
                val attack = MeleeAttack(Damage(amount, DAMAGE_TYPE_ID_0))

                assertValid(attack)
            }
        }

        @Test
        fun `Too many dice for statistic based damage`() {
            val modifiedDice = StandardDice(STATE.config.rpg.damage.dice.max + 1)
            val amount = StatisticBasedDamage(STATISTIC_ID_0, modifiedDice)
            val attack = MeleeAttack(Damage(amount, DAMAGE_TYPE_ID_0))

            assertInvalid(attack, "StatisticBasedDamage's dice needs to be <= 20!")
        }

        @Test
        fun `Too few dice for statistic based damage`() {
            val modifiedDice = StandardDice(STATE.config.rpg.damage.dice.min - 1)
            val amount = StatisticBasedDamage(STATISTIC_ID_0, modifiedDice)
            val attack = MeleeAttack(Damage(amount, DAMAGE_TYPE_ID_0))

            assertInvalid(attack, "StatisticBasedDamage's dice needs to be >= 0!")
        }
    }

    @Nested
    inner class ReachTest {

        @Test
        fun `Simple reach cannot be negative`() {
            val attack = MeleeAttack(reach = SimpleReach(-1))

            assertInvalid(attack, "The simple reach reach must be >= 0!")
        }

        @Test
        fun `The range's minimum cannot be negative`() {
            val attack = MeleeAttack(reach = ReachRange(-1, 2))

            assertInvalid(attack, "The minimum reach must be >= 0!")
        }

        @Test
        fun `The range's minimum must be smaller than the maximum`() {
            val attack = MeleeAttack(reach = ReachRange(2, 2))

            assertInvalid(attack, "The minimum reach must be < than its maximum!")
        }
    }

    @Test
    fun `Check if used skill is validated`() {
        val skill = ModifiedUsedSkill(UNKNOWN_STATISTIC_ID)
        val attack = MeleeAttack(skill = skill)

        assertInvalid(attack, "Requires unknown Statistic 99!")
    }

    @Test
    fun `A valid melee weapon`() {
        val attack = MeleeAttack(Damage(validDamageAmount, DAMAGE_TYPE_ID_0), ReachRange(1, 2))

        assertValid(attack)
    }

    private fun assertValid(attack: MeleeAttack) {
        val type = EquipmentType(EQUIPMENT_TYPE_ID_0, meleeAttacks = listOf(attack))

        type.validate(STATE)
    }

    private fun assertInvalid(attack: MeleeAttack, message: String) {
        assertIllegalArgument(message) {
            assertValid(attack)
        }
    }

}