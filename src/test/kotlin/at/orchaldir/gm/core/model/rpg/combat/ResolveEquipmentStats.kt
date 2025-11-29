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

            assertResolve(effect, simpleAttack, updateAttack)
        }

        @Test
        fun `Modify Modified Base Damage`() {
            val effect = ModifyDamage(SimpleModifiedDice(10, 20))
            val updateAttack = createAttack(StatisticBasedDamage(STATISTIC_ID_0, updatedDice))

            assertResolve(effect, basedAttack, updateAttack)
        }

        @Test
        fun `Test Modify Damage Resistance`() {
            val effect = ModifyDamageResistance(1)

            assertResolve(effect, simpleAttack, simpleAttack)
            assertResolve(effect, basedAttack, basedAttack)
        }

        @Test
        fun `Test Modify Defense Bonus`() {
            val effect = ModifyDefenseBonus(2)

            assertResolve(effect, simpleAttack, simpleAttack)
            assertResolve(effect, basedAttack, basedAttack)
        }

        private fun assertResolve(
            effect: EquipmentModifierEffect,
            input: MeleeAttack,
            output: MeleeAttack,
        ) {
            assertEquals(resolveMeleeAttack(effect, input), output)
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

            assertResolve(effect, damageResistance, damageResistance)
            assertResolve(effect, damageResistances, damageResistances)
            assertResolve(effect, defenseBonus, defenseBonus)
            assertResolve(effect, UndefinedProtection, UndefinedProtection)
        }

        @Test
        fun `Test Modify Damage Resistance`() {
            val effect = ModifyDamageResistance(1)
            val updated = DamageResistances(5, mapOf(DAMAGE_TYPE_ID_0 to 6))

            assertResolve(effect, damageResistance, DamageResistance(5))
            assertResolve(effect, damageResistances, updated)
            assertResolve(effect, defenseBonus, defenseBonus)
            assertResolve(effect, UndefinedProtection, UndefinedProtection)
        }

        @Test
        fun `Test Modify Defense Bonus`() {
            val effect = ModifyDefenseBonus(2)

            assertResolve(effect, damageResistance, damageResistance)
            assertResolve(effect, damageResistances, damageResistances)
            assertResolve(effect, defenseBonus, DefenseBonus(9))
            assertResolve(effect, UndefinedProtection, UndefinedProtection)
        }

        private fun assertResolve(
            effect: EquipmentModifierEffect, input: Protection, output: Protection
        ) {
            assertEquals(resolveProtection(effect, input), output)
        }

    }

}