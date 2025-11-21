package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.DAMAGE_TYPE_ID_0
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProtectionTest {

    @Nested
    inner class ApplyEffectTest {
        val damageResistance = DamageResistance(4)
        val damageResistances = DamageResistances(4, mapOf(DAMAGE_TYPE_ID_0 to 6))
        val defenseBonus = DefenseBonus(7)

        @Test
        fun `Test Modify Damage`() {
            val effect = ModifyDamage(SimpleModifiedDice(1, 2))

            assertEquals(damageResistance.apply(effect), damageResistance)
            assertEquals(damageResistances.apply(effect), damageResistances)
            assertEquals(defenseBonus.apply(effect), defenseBonus)
            assertEquals(UndefinedProtection.apply(effect), UndefinedProtection)
        }

        @Test
        fun `Test Modify Damage Resistance`() {
            val effect = ModifyDamageResistance(1)
            val updated = DamageResistances(5, mapOf(DAMAGE_TYPE_ID_0 to 6))

            assertEquals(damageResistance.apply(effect), DamageResistance(5))
            assertEquals(damageResistances.apply(effect), updated)
            assertEquals(defenseBonus.apply(effect), defenseBonus)
            assertEquals(UndefinedProtection.apply(effect), UndefinedProtection)
        }

        @Test
        fun `Test Modify Defense Bonus`() {
            val effect = ModifyDefenseBonus(2)

            assertEquals(damageResistance.apply(effect), damageResistance)
            assertEquals(damageResistances.apply(effect), damageResistances)
            assertEquals(defenseBonus.apply(effect), DefenseBonus(9))
            assertEquals(UndefinedProtection.apply(effect), UndefinedProtection)
        }

    }

}