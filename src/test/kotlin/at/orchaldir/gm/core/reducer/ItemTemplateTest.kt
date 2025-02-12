package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteItemTemplate
import at.orchaldir.gm.core.action.UpdateItemTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentType.Hat
import at.orchaldir.gm.core.model.item.equipment.ItemTemplate
import at.orchaldir.gm.core.model.item.equipment.ItemTemplateId
import at.orchaldir.gm.core.model.item.equipment.Pants
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = ItemTemplateId(0)
private val ITEM = ItemTemplate(ID0, "Test")
private val CHARACTER0 = CharacterId(0)
private val STATE = State(Storage(ItemTemplate(ID0)))
private val MATERIAL0 = MaterialId(0)
private val MATERIAL1 = MaterialId(1)
private val EQUIPMENT_MAP = EquipmentMap(mapOf(Hat to ID0))

class ItemTemplateTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing id`() {
            val action = DeleteItemTemplate(ID0)

            assertEquals(0, REDUCER.invoke(STATE, action).first.getItemTemplateStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteItemTemplate(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if an instanced item exist`() {
            val action = DeleteItemTemplate(ID0)
            val state =
                STATE.updateStorage(Storage(Character(CHARACTER0, equipmentMap = EQUIPMENT_MAP)))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateItemTemplate(ITEM)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot change equipment type while equipped`() {
            val oldItem = ItemTemplate(ID0, equipment = Pants(material = MATERIAL0))
            val newItem = ItemTemplate(ID0, equipment = Shirt(material = MATERIAL0))
            val state = State(
                listOf(
                    Storage(oldItem),
                    Storage(Character(CHARACTER0, equipmentMap = EQUIPMENT_MAP)),
                    Storage(Material(MATERIAL0)),
                )
            )
            val action = UpdateItemTemplate(newItem)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Can change equipment details while equipped`() {
            val oldItem = ItemTemplate(ID0, equipment = Shirt(material = MATERIAL0))
            val newItem = ItemTemplate(ID0, equipment = Shirt(material = MATERIAL1))
            val state = State(
                listOf(
                    Storage(oldItem),
                    Storage(Character(CHARACTER0, equipmentMap = EQUIPMENT_MAP)),
                    Storage(listOf(Material(MATERIAL0), Material(MATERIAL1))),
                )
            )
            val action = UpdateItemTemplate(newItem)

            assertEquals(newItem, REDUCER.invoke(state, action).first.getItemTemplateStorage().get(ID0))
        }

        @Test
        fun `Material must exist`() {
            val item = ItemTemplate(ID0, equipment = Shirt(material = MATERIAL0))
            val action = UpdateItemTemplate(item)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update template`() {
            val action = UpdateItemTemplate(ITEM)

            assertEquals(ITEM, REDUCER.invoke(STATE, action).first.getItemTemplateStorage().get(ID0))
        }

        @Test
        fun `Update template with material`() {
            val item = ItemTemplate(ID0, equipment = Shirt(material = MATERIAL0))
            val state = State(
                listOf(
                    Storage(ITEM),
                    Storage(Material(MATERIAL0)),
                )
            )
            val action = UpdateItemTemplate(item)

            assertEquals(item, REDUCER.invoke(state, action).first.getItemTemplateStorage().get(ID0))
        }
    }

}