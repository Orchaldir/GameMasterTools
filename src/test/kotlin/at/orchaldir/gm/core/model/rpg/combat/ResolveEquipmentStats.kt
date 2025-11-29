package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.DAMAGE_TYPE_ID_0
import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.selector.rpg.resolveMeleeAttack
import at.orchaldir.gm.core.selector.rpg.resolveProtection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ResolveEquipmentStats {

    @Nested
    inner class MeleeAttackTest {
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

    @Nested
    inner class ProtectionTest {
        val damageResistance = DamageResistance(4)
        val damageResistances = DamageResistances(4, mapOf(DAMAGE_TYPE_ID_0 to 6))
        val defenseBonus = DefenseBonus(7)

        @Test
        fun `Test Modify Damage`() {
            val effect = ModifyDamage(SimpleModifiedDice(1, 2))

            assertEquals(resolveProtection(effect, damageResistance), damageResistance)
            assertEquals(resolveProtection(effect, damageResistances), damageResistances)
            assertEquals(resolveProtection(effect, defenseBonus), defenseBonus)
            assertEquals(resolveProtection(effect, UndefinedProtection), UndefinedProtection)
        }

        @Test
        fun `Test Modify Damage Resistance`() {
            val effect = ModifyDamageResistance(1)
            val updated = DamageResistances(5, mapOf(DAMAGE_TYPE_ID_0 to 6))

            assertEquals(resolveProtection(effect, damageResistance), DamageResistance(5))
            assertEquals(resolveProtection(effect, damageResistances), updated)
            assertEquals(resolveProtection(effect, defenseBonus), defenseBonus)
            assertEquals(resolveProtection(effect, UndefinedProtection), UndefinedProtection)
        }

        @Test
        fun `Test Modify Defense Bonus`() {
            val effect = ModifyDefenseBonus(2)

            assertEquals(resolveProtection(effect, damageResistance), damageResistance)
            assertEquals(resolveProtection(effect, damageResistances), damageResistances)
            assertEquals(resolveProtection(effect, defenseBonus), DefenseBonus(9))
            assertEquals(resolveProtection(effect, UndefinedProtection), UndefinedProtection)
        }

    }

}