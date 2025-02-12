package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteEquipment
import at.orchaldir.gm.core.action.UpdateEquipment
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType.Hat
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.Pants
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = EquipmentId(0)
private val ITEM = Equipment(ID0, "Test")
private val CHARACTER0 = CharacterId(0)
private val STATE = State(Storage(Equipment(ID0)))
private val MATERIAL0 = MaterialId(0)
private val MATERIAL1 = MaterialId(1)
private val EQUIPMENT_MAP = EquipmentMap(mapOf(Hat to ID0))

class ItemTemplateTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing id`() {
            val action = DeleteEquipment(ID0)

            assertEquals(0, REDUCER.invoke(STATE, action).first.getEquipmentStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteEquipment(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if an instanced item exist`() {
            val action = DeleteEquipment(ID0)
            val state =
                STATE.updateStorage(Storage(Character(CHARACTER0, equipmentMap = EQUIPMENT_MAP)))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateEquipment(ITEM)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot change equipment type while equipped`() {
            val oldItem = Equipment(ID0, data = Pants(material = MATERIAL0))
            val newItem = Equipment(ID0, data = Shirt(material = MATERIAL0))
            val state = State(
                listOf(
                    Storage(oldItem),
                    Storage(Character(CHARACTER0, equipmentMap = EQUIPMENT_MAP)),
                    Storage(Material(MATERIAL0)),
                )
            )
            val action = UpdateEquipment(newItem)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Can change equipment details while equipped`() {
            val oldItem = Equipment(ID0, data = Shirt(material = MATERIAL0))
            val newItem = Equipment(ID0, data = Shirt(material = MATERIAL1))
            val state = State(
                listOf(
                    Storage(oldItem),
                    Storage(Character(CHARACTER0, equipmentMap = EQUIPMENT_MAP)),
                    Storage(listOf(Material(MATERIAL0), Material(MATERIAL1))),
                )
            )
            val action = UpdateEquipment(newItem)

            assertEquals(newItem, REDUCER.invoke(state, action).first.getEquipmentStorage().get(ID0))
        }

        @Test
        fun `Material must exist`() {
            val item = Equipment(ID0, data = Shirt(material = MATERIAL0))
            val action = UpdateEquipment(item)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update template`() {
            val action = UpdateEquipment(ITEM)

            assertEquals(ITEM, REDUCER.invoke(STATE, action).first.getEquipmentStorage().get(ID0))
        }

        @Test
        fun `Update template with material`() {
            val item = Equipment(ID0, data = Shirt(material = MATERIAL0))
            val state = State(
                listOf(
                    Storage(ITEM),
                    Storage(Material(MATERIAL0)),
                )
            )
            val action = UpdateEquipment(item)

            assertEquals(item, REDUCER.invoke(state, action).first.getEquipmentStorage().get(ID0))
        }
    }

}