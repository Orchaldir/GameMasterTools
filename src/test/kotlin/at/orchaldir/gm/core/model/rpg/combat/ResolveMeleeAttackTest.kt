package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.DAMAGE_TYPE_ID_0
import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.dice.StandardDice
import at.orchaldir.gm.core.selector.rpg.statblock.resolveMeleeAttack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ResolveMeleeAttackTest {

    private val simple = SimpleRandomDamage(StandardDice(1, 2))
    private val simpleAttack = createAttack(simple)
    private val based = StatisticBasedDamage(STATISTIC_ID_0, StandardDice(1, 2))
    private val basedAttack = createAttack(based)
    private val updatedDice = StandardDice(11, 22)
    private val state = State()

    @Test
    fun `Modify Simple Random Damage`() {
        val effect = ModifyDamage(StandardDice(10, 20))
        val updateAttack = createAttack(SimpleRandomDamage(updatedDice))

        assertResolve(effect, simpleAttack, updateAttack)
    }

    @Test
    fun `Modify Modified Base Damage`() {
        val effect = ModifyDamage(StandardDice(10, 20))
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
        assertEquals(resolveMeleeAttack(state, effect, input), output)
    }

    fun createAttack(amount: DamageAmount) = MeleeAttack(Damage(amount, DAMAGE_TYPE_ID_0))


}