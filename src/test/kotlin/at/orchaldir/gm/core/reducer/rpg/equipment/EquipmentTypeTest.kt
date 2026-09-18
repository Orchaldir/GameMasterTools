package at.orchaldir.gm.core.reducer.rpg.equipment

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.MIN_PRICE
import at.orchaldir.gm.core.model.economy.money.UserDefinedPrice
import at.orchaldir.gm.core.model.item.equipment.MAX_EQUIPMENT_PRICE
import at.orchaldir.gm.core.model.item.equipment.MAX_EQUIPMENT_WEIGHT
import at.orchaldir.gm.core.model.item.equipment.MIN_EQUIPMENT_PRICE
import at.orchaldir.gm.core.model.item.equipment.MIN_EQUIPMENT_WEIGHT
import at.orchaldir.gm.core.model.rpg.combat.DamageResistance
import at.orchaldir.gm.core.model.rpg.combat.DamageType
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentType
import at.orchaldir.gm.core.model.rpg.statistic.BaseDamage
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.ONE_PERCENT
import at.orchaldir.gm.utils.math.unit.ONE_GRAM
import at.orchaldir.gm.utils.math.unit.UserDefinedWeight
import org.junit.jupiter.api.Test

class EquipmentTypeTest {

    private val STATE = State(
        listOf(
            Storage(listOf(DamageType(DAMAGE_TYPE_ID_0))),
            Storage(listOf(Statistic(STATISTIC_ID_0, data = BaseDamage()), Statistic(STATISTIC_ID_1))),
        )
    )

    @Test
    fun `Test protection`() {
        val protection = DamageResistance(0)
        val equipmentType = EquipmentType(EQUIPMENT_TYPE_ID_0, protection = protection)

        assertInvalidArmor(equipmentType, "Damage Resistance needs to be >= 1!")
    }

    @Test
    fun `Cannot have a cost factor above the maximum`() {
        val lookup = UserDefinedPrice(MAX_EQUIPMENT_PRICE + MIN_PRICE)
        val equipmentType = EquipmentType(EQUIPMENT_TYPE_ID_0, price = lookup)

        assertInvalidArmor(equipmentType, "The Price is too large!")
    }

    @Test
    fun `Cannot have a weight below the minimum`() {
        val weight = UserDefinedWeight(MIN_EQUIPMENT_WEIGHT - ONE_GRAM)
        val equipmentType = EquipmentType(EQUIPMENT_TYPE_ID_0, weight = weight)

        assertInvalidArmor(equipmentType, "The Weight is too small!")
    }

    @Test
    fun `Cannot have a weight above the maximum`() {
        val weight = UserDefinedWeight(MAX_EQUIPMENT_WEIGHT + ONE_GRAM)
        val equipmentType = EquipmentType(EQUIPMENT_TYPE_ID_0, weight = weight)

        assertInvalidArmor(equipmentType, "The Weight is too large!")
    }

    private fun assertInvalidArmor(equipmentType: EquipmentType, message: String) {
        assertIllegalArgument(message) { equipmentType.validate(STATE) }
    }

}