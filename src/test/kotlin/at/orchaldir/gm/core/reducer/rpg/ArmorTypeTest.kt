package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamage
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.ONE_PERCENT
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ArmorTypeTest {

    private val STATE = State(
        listOf(
            Storage(listOf(DamageType(DAMAGE_TYPE_ID_0))),
            Storage(listOf(Statistic(STATISTIC_ID_0, data = BaseDamage()), Statistic(STATISTIC_ID_1))),
        )
    )

    @Test
    fun `Test protection`() {
        val protection = DamageResistance(0)
        val armorType = ArmorType(ARMOR_TYPE_ID_0, protection = protection)

        assertInvalidArmor(armorType, "Damage Resistance needs to be >= 1!")
    }

    @Test
    fun `Cannot have a cost factor below the minimum`() {
        val armorType = ArmorType(ARMOR_TYPE_ID_0, cost = MIN_COST_FACTOR - ONE_PERCENT)

        assertInvalidArmor(armorType, "Cost Factor -101% is below the minimum!")
    }

    @Test
    fun `Cannot have a cost factor above the maximum`() {
        val armorType = ArmorType(ARMOR_TYPE_ID_0, cost = MAX_COST_FACTOR + ONE_PERCENT)

        assertInvalidArmor(armorType, "Cost Factor 10001% is above the maximum!")
    }

    private fun assertInvalidArmor(armorType: ArmorType, message: String) {
        assertIllegalArgument(message) { armorType.validate(STATE) }
    }

}