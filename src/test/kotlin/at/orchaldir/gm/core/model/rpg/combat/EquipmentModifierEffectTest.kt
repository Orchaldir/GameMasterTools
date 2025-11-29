package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.DAMAGE_TYPE_ID_0
import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.selector.rpg.resolveMeleeAttack
import at.orchaldir.gm.core.selector.rpg.resolveMeleeAttacks
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EquipmentModifierEffectTest {

    @Nested
    inner class ApplyEffectToMeleeAttackTest {
        val simple = SimpleRandomDamage(SimpleModifiedDice(1, 2))
        val simpleAttack = createAttack(simple)
        val based = StatisticBasedDamage(STATISTIC_ID_0, SimpleModifiedDice(1, 2))
        val basedAttack = createAttack(based)
        val updatedDice = SimpleModifiedDice(11, 22)

        @Test
        fun `Modify Simple Random Damage`() {
            val effect = ModifyDamage(SimpleModifiedDice(10, 20))
            val updateAttack = createAttack(SimpleRandomDamage(updatedDice))

            assertEquals(resolveMeleeAttack(effect, simpleAttack), updateAttack)
        }

        @Test
        fun `Modify Modified Base Damage`() {
            val effect = ModifyDamage(SimpleModifiedDice(10, 20))
            val updateAttack = createAttack(StatisticBasedDamage(STATISTIC_ID_0, updatedDice))

            assertEquals(resolveMeleeAttack(effect, basedAttack), updateAttack)
        }

        @Test
        fun `Test Modify Damage Resistance`() {
            val effect = ModifyDamageResistance(1)

            assertEquals(resolveMeleeAttack(effect, simpleAttack), simpleAttack)
            assertEquals(resolveMeleeAttack(effect, basedAttack), basedAttack)
        }

        @Test
        fun `Test Modify Defense Bonus`() {
            val effect = ModifyDefenseBonus(2)

            assertEquals(resolveMeleeAttack(effect, simpleAttack), simpleAttack)
            assertEquals(resolveMeleeAttack(effect, basedAttack), basedAttack)
        }

        fun createAttack(amount: DamageAmount) = MeleeAttack(Damage(amount, DAMAGE_TYPE_ID_0))

    }

}