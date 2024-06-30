package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteItemTemplate
import at.orchaldir.gm.core.action.UpdateItemTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.Item
import at.orchaldir.gm.core.model.item.ItemId
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = ItemTemplateId(0)
private val ITEM = ItemTemplate(ID0, "Test")
private val STATE = State(itemTemplates = Storage(listOf(ItemTemplate(ID0))))

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
        fun `Update template`() {
            val action = UpdateItemTemplate(ITEM)

            assertEquals(ITEM, REDUCER.invoke(STATE, action).first.itemTemplates.get(ID0))
        }
    }

}