package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.DAMAGE_TYPE_ID_0
import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.dice.StandardDice
import at.orchaldir.gm.core.selector.rpg.statblock.resolveRangedAttack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ResolveRangedAttackTest {

    val simple = SimpleRandomDamage(StandardDice(1, 2))
    val simpleAttack = createAttack(simple)
    val based = StatisticBasedDamage(STATISTIC_ID_0, StandardDice(1, 2))
    val basedAttack = createAttack(based)
    val updatedDice = StandardDice(11, 22)
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
        input: RangedAttack,
        output: RangedAttack,
    ) {
        assertEquals(resolveRangedAttack(state, effect, input), output)
    }

    fun createAttack(amount: DamageAmount) = RangedAttack(effect = Damage(amount, DAMAGE_TYPE_ID_0))


}