package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentModifier
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentStats
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentType
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.UserDefinedWeight
import at.orchaldir.gm.utils.math.unit.WEIGHTLESS
import at.orchaldir.gm.utils.math.unit.Weight
import at.orchaldir.gm.utils.math.unit.WeightBasedOnType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CalculateWeightTest {

    private val type0 = EquipmentType(
        EQUIPMENT_TYPE_ID_0,
        weight = UserDefinedWeight(Weight.fromGrams(1000))
    )
    private val type1 = EquipmentType(EQUIPMENT_TYPE_ID_1)
    private val modifier0 = EquipmentModifier(
        EQUIPMENT_MODIFIER_ID_0,
        weight = Factor.fromPercentage(20)
    )
    private val modifier1 = EquipmentModifier(
        EQUIPMENT_MODIFIER_ID_1,
        weight = Factor.fromPercentage(30)
    )
    private val state = State(
        listOf(
            Storage(listOf(type0, type1)),
            Storage(listOf(modifier0, modifier1)),
        )
    )

    @Test
    fun `Calculate the weight based on the type`() {
        val stats = EquipmentStats(EQUIPMENT_TYPE_ID_0)
        val equipment = Equipment(EQUIPMENT_ID_0, stats = stats, weight = WeightBasedOnType)

        assertWeight(equipment, Weight.fromGrams(1000))
    }

    @Test
    fun `Calculate the weight based on an unknown type`() {
        val equipment = Equipment(EQUIPMENT_ID_0, weight = WeightBasedOnType)

        assertWeight(equipment, WEIGHTLESS)
    }

    @Test
    fun `Calculate the weight based on a type with undefined weight`() {
        val stats = EquipmentStats(EQUIPMENT_TYPE_ID_1)
        val equipment = Equipment(EQUIPMENT_ID_0, stats = stats, weight = WeightBasedOnType)

        assertWeight(equipment, WEIGHTLESS)
    }

    @Test
    fun `Calculate the weight based on the type & modifiers`() {
        val stats = EquipmentStats(EQUIPMENT_TYPE_ID_0, setOf(EQUIPMENT_MODIFIER_ID_0, EQUIPMENT_MODIFIER_ID_1))
        val equipment = Equipment(EQUIPMENT_ID_0, stats = stats, weight = WeightBasedOnType)

        assertWeight(equipment, Weight.fromGrams(1500))
    }

    @Test
    fun `User defined weight`() {
        val weight = Weight.fromGrams(2000)
        val equipment = Equipment(EQUIPMENT_ID_0, weight = UserDefinedWeight(weight))

        assertWeight(equipment, weight)
    }

    @Test
    fun `Undefined weight`() {
        val equipment = Equipment(EQUIPMENT_ID_0)

        assertWeight(equipment, WEIGHTLESS)
    }

    private fun assertWeight(equipment: Equipment, weight: Weight) {

        assertEquals(weight, calculateWeight(state, VOLUME_CONFIG, equipment))
    }
}