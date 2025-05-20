package at.orchaldir.gm.core.selector

import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_1
import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.MATERIAL_ID_1
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.selector.economy.canDeleteMaterial
import at.orchaldir.gm.core.selector.item.getEquipmentMadeOf
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MaterialTest {
    @Nested
    inner class CanDeleteTest {

        @Test
        fun `Can delete unused material`() {
            val state = State(Storage(listOf(Material(MATERIAL_ID_0), Material(MATERIAL_ID_1))))

            assertTrue(state.canDeleteMaterial(MATERIAL_ID_0))
            assertTrue(state.canDeleteMaterial(MATERIAL_ID_1))
        }

        @Test
        fun `Cannot delete material used by item template`() {
            val state = State(
                listOf(
                    Storage(Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillItemPart(MATERIAL_ID_0)))),
                    Storage(listOf(Material(MATERIAL_ID_0), Material(MATERIAL_ID_1))),
                )
            )

            assertFalse(state.canDeleteMaterial(MATERIAL_ID_0))
            assertTrue(state.canDeleteMaterial(MATERIAL_ID_1))
        }
    }

    @Test
    fun `Get all item templates using a material`() {
        val template0 = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillItemPart(MATERIAL_ID_0)))
        val template1 = Equipment(EQUIPMENT_ID_1, data = Shirt(main = FillItemPart(MATERIAL_ID_0)))
        val state = State(
            listOf(
                Storage(listOf(template0, template1)),
                Storage(listOf(Material(MATERIAL_ID_0), Material(MATERIAL_ID_1)))
            )
        )

        assertEquals(listOf(template0, template1), state.getEquipmentMadeOf(MATERIAL_ID_0))
        assertEquals(emptyList(), state.getEquipmentMadeOf(MATERIAL_ID_1))
    }
}