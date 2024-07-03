package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateItem
import at.orchaldir.gm.core.action.DeleteItem
import at.orchaldir.gm.core.action.UpdateItem
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.item.*
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = ItemId(0)
private val ID1 = ItemId(1)
private val TEMPLATE0 = ItemTemplateId(0)
private val TEMPLATE1 = ItemTemplateId(1)
private val CHARACTER0 = CharacterId(0)
private val CHARACTER1 = CharacterId(1)

class ItemTest {

    @Nested
    inner class CreateTest {

        @Test
        fun `Cannot create item with an unknown template`() {
            val action = CreateItem(TEMPLATE0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Create an item from a template`() {
            val action = CreateItem(TEMPLATE1)
            val state = State(itemTemplates = Storage(listOf(ItemTemplate(TEMPLATE1))))

            assertEquals(TEMPLATE1, REDUCER.invoke(state, action).first.items.get(ID0)?.template)
        }
    }

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing item`() {
            val action = DeleteItem(ID0)
            val state = State(items = Storage(listOf(Item(ID0, TEMPLATE0))))

            assertEquals(0, REDUCER.invoke(state, action).first.items.getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteItem(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateItem(Item(ID1, TEMPLATE0))
            val state = State(itemTemplates = Storage(listOf(ItemTemplate(TEMPLATE0))))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot update an item with an unknown template`() {
            val action = UpdateItem(Item(ID0, TEMPLATE1))
            val state = State(
                itemTemplates = Storage(listOf(ItemTemplate(TEMPLATE0))),
                items = Storage(listOf(Item(ID0, TEMPLATE0)))
            )

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Update an item`() {
            val item = Item(ID0, TEMPLATE0, InInventory(CharacterId(0)))
            val action = UpdateItem(item)
            val state = State(
                itemTemplates = Storage(listOf(ItemTemplate(TEMPLATE0))),
                items = Storage(listOf(Item(ID0, TEMPLATE0)))
            )

            assertEquals(item, REDUCER.invoke(state, action).first.items.get(ID0))
        }

        @Nested
        inner class EquipTest {
            @Test
            fun `Equip an item`() {
                val item = Item(ID0, TEMPLATE0, EquippedItem(CHARACTER0))
                val action = UpdateItem(item)
                val state = State(
                    characters = Storage(listOf(Character(CHARACTER0))),
                    itemTemplates = Storage(listOf(ItemTemplate(TEMPLATE0))),
                    items = Storage(listOf(Item(ID0, TEMPLATE0)))
                )

                assertEquals(item, REDUCER.invoke(state, action).first.items.get(ID0))
            }

            @Test
            fun `An unknown character cannot equip an item`() {
                val action = UpdateItem(Item(ID0, TEMPLATE0, EquippedItem(CHARACTER0)))
                val state = State(
                    itemTemplates = Storage(listOf(ItemTemplate(TEMPLATE0))),
                    items = Storage(listOf(Item(ID0, TEMPLATE0)))
                )

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }
        }
    }

}