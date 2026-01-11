package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.DAMAGE_TYPE_ID_0
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.selector.rpg.statblock.resolveProtection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ResolveProtectionTest {

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
        effect: EquipmentModifierEffect, input: Protection, output: Protection,
    ) {
        assertEquals(resolveProtection(effect, input), output)
    }

}