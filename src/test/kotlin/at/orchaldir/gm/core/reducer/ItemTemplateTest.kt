package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteItemTemplate
import at.orchaldir.gm.core.action.UpdateItemTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.item.*
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val CHARACTER0 = CharacterId(0)
private val ID0 = ItemTemplateId(0)
private val ITEM = ItemTemplate(ID0, "Test")
private val ITEM_ID0 = ItemId(0)
private val STATE = State(itemTemplates = Storage(listOf(ItemTemplate(ID0))))
private val MATERIAL0 = MaterialId(0)

class ItemTemplateTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing id`() {
            val action = DeleteItemTemplate(ID0)

            assertEquals(0, REDUCER.invoke(STATE, action).first.itemTemplates.getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteItemTemplate(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if an instanced item exist`() {
            val action = DeleteItemTemplate(ID0)
            val state = STATE.copy(items = Storage(listOf(Item(ItemId(0), ID0))))

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
        fun `Cannot update equipment while equipped`() {
            val oldItem = ItemTemplate(ID0, equipment = Pants(material = MATERIAL0))
            val newItem = ItemTemplate(ID0, equipment = Shirt(material = MATERIAL0))
            val state = State(
                itemTemplates = Storage(listOf(oldItem)),
                items = Storage(listOf(Item(ITEM_ID0, location = EquippedItem(CHARACTER0)))),
                materials = Storage(listOf(Material(MATERIAL0)))
            )
            val action = UpdateItemTemplate(newItem)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
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

            assertEquals(ITEM, REDUCER.invoke(STATE, action).first.itemTemplates.get(ID0))
        }

        @Test
        fun `Update template with material`() {
            val item = ItemTemplate(ID0, equipment = Shirt(material = MATERIAL0))
            val state = State(
                itemTemplates = Storage(listOf(ITEM)),
                materials = Storage(listOf(Material(MATERIAL0)))
            )
            val action = UpdateItemTemplate(item)

            assertEquals(item, REDUCER.invoke(state, action).first.itemTemplates.get(ID0))
        }
    }

}