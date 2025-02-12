package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.selector.item.getEquipmentMadeOf
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val ID0 = MaterialId(0)
private val ID1 = MaterialId(1)
private val TEMPLATE0 = EquipmentId(0)
private val TEMPLATE1 = EquipmentId(1)

class MaterialTest {
    @Nested
    inner class CanDeleteTest {

        @Test
        fun `Can delete unused material`() {
            val state = State(Storage(listOf(Material(ID0), Material(ID1))))

            assertTrue(state.canDelete(ID0))
            assertTrue(state.canDelete(ID1))
        }

        @Test
        fun `Cannot delete material used by item template`() {
            val state = State(
                listOf(
                    Storage(Equipment(TEMPLATE0, equipment = Shirt(material = ID0))),
                    Storage(listOf(Material(ID0), Material(ID1))),
                )
            )

            assertFalse(state.canDelete(ID0))
            assertTrue(state.canDelete(ID1))
        }
    }

    @Test
    fun `Get all item templates using a material`() {
        val template0 = Equipment(TEMPLATE0, equipment = Shirt(material = ID0))
        val template1 = Equipment(TEMPLATE1, equipment = Shirt(material = ID0))
        val state = State(
            listOf(
                Storage(listOf(template0, template1)),
                Storage(listOf(Material(ID0), Material(ID1)))
            )
        )

        assertEquals(listOf(template0, template1), state.getEquipmentMadeOf(ID0))
        assertEquals(emptyList(), state.getEquipmentMadeOf(ID1))
    }
}