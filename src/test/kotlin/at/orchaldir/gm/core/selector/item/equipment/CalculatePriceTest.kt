package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_MODIFIER_ID_0
import at.orchaldir.gm.EQUIPMENT_MODIFIER_ID_1
import at.orchaldir.gm.EQUIPMENT_TYPE_ID_0
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.FREE
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.model.economy.money.PriceBasedOnType
import at.orchaldir.gm.core.model.economy.money.UserDefinedPrice
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentModifier
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentStats
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentType
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CalculatePriceTest {

    private val type = EquipmentType(
        EQUIPMENT_TYPE_ID_0,
        price = UserDefinedPrice(Price(100))
    )
    private val modifier0 = EquipmentModifier(
        EQUIPMENT_MODIFIER_ID_0,
        price = Factor.fromPercentage(20)
    )
    private val modifier1 = EquipmentModifier(
        EQUIPMENT_MODIFIER_ID_1,
        price = Factor.fromPercentage(30)
    )
    private val state = State(
        listOf(
            Storage(type),
            Storage(listOf(modifier0, modifier1)),
        )
    )

    @Test
    fun `Calculate the price based on the type`() {
        val stats = EquipmentStats(EQUIPMENT_TYPE_ID_0)
        val equipment = Equipment(EQUIPMENT_ID_0, stats = stats, price = PriceBasedOnType)

        assertPrice(equipment, Price(100))
    }

    @Test
    fun `Calculate the price based on the type & modifiers`() {
        val stats = EquipmentStats(EQUIPMENT_TYPE_ID_0, setOf(EQUIPMENT_MODIFIER_ID_0, EQUIPMENT_MODIFIER_ID_1))
        val equipment = Equipment(EQUIPMENT_ID_0, stats = stats, price = PriceBasedOnType)

        assertPrice(equipment, Price(150))
    }

    @Test
    fun `User defined price`() {
        val equipment = Equipment(EQUIPMENT_ID_0, price = UserDefinedPrice(Price(200)))

        assertPrice(equipment, Price(200))
    }

    @Test
    fun `Undefined price`() {
        val equipment = Equipment(EQUIPMENT_ID_0)

        assertPrice(equipment, FREE)
    }

    private fun assertPrice(equipment: Equipment, price: Price) {

        assertEquals(price, calculatePrice(state, VOLUME_CONFIG, equipment))
    }
}