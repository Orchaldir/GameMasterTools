package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.EQUIPMENT_MODIFIER_ID_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.math.ONE_PERCENT
import org.junit.jupiter.api.Test

class EquipmentModifierTest {

    private val state = State()
    private val modifyDamage = ModifyDamage(SimpleModifiedDice(10, 20))
    private val modifyDR = ModifyDamageResistance(1)
    private val modifyDB = ModifyDefenseBonus(2)

    @Test
    fun `Cannot have ModifyDamage twice`() {
        assertInvalidModifier(listOf(modifyDamage, modifyDamage))
    }

    @Test
    fun `Cannot have ModifyDamageResistance twice`() {
        assertInvalidModifier(listOf(modifyDR, modifyDR))
    }

    @Test
    fun `Cannot have ModifyDefenseBonus twice`() {
        assertInvalidModifier(listOf(modifyDB, modifyDB))
    }

    @Test
    fun `Cannot have a cost factor below the minimum`() {
        val modifier = EquipmentModifier(EQUIPMENT_MODIFIER_ID_0, cost = MIN_COST_FACTOR - ONE_PERCENT)

        assertInvalidModifier(modifier, "Cost Factor -101% is below the minimum!")
    }

    @Test
    fun `Cannot have a cost factor above the maximum`() {
        val modifier = EquipmentModifier(EQUIPMENT_MODIFIER_ID_0, cost = MAX_COST_FACTOR + ONE_PERCENT)

        assertInvalidModifier(modifier, "Cost Factor 10001% is above the maximum!")
    }

    private fun assertInvalidModifier(effects: List<EquipmentModifierEffect>) {
        val modifier = EquipmentModifier(EQUIPMENT_MODIFIER_ID_0, effects = effects)

        assertInvalidModifier(modifier, "Contains a type of effects more than once!")
    }

    private fun assertInvalidModifier(modifier: EquipmentModifier, message: String) {
        assertIllegalArgument(message) { modifier.validate(state) }
    }

}