package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.item.*
import at.orchaldir.gm.core.model.item.PantsStyle.Shorts
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val ID0 = ItemId(0)
private val TEMPLATE0 = ItemTemplateId(0)
private val TEMPLATE1 = ItemTemplateId(1)
private val CHARACTER0 = CharacterId(0)
private val CHARACTER1 = CharacterId(1)

class ItemTest {

    @Nested
    inner class GetEquipmentTest {
        @Test
        fun `Get equipment in a character`() {
            val equipment = Pants(Shorts)
            val item = Item(ID0, TEMPLATE0, location = EquippedItem(CHARACTER0))
            val state = State(
                items = Storage(listOf(item)),
                itemTemplates = Storage(listOf(ItemTemplate(TEMPLATE0, equipment = equipment)))
            )

            assertEquals(listOf(equipment), state.getEquipment(CHARACTER0))
        }

        @Test
        fun `Do not get the equipment of another character`() {
            val state = State(items = Storage(listOf(Item(ID0, location = EquippedItem(CHARACTER0)))))

            assertTrue(state.getEquipment(CHARACTER1).isEmpty())
        }

        @Test
        fun `Do not get the items in an inventory`() {
            val equipment = Pants(Shorts)
            val item = Item(ID0, TEMPLATE0, location = InInventory(CHARACTER0))
            val state = State(
                items = Storage(listOf(item)),
                itemTemplates = Storage(listOf(ItemTemplate(TEMPLATE0, equipment = equipment)))
            )

            assertTrue(state.getEquipment(CHARACTER0).isEmpty())
            assertTrue(state.getEquipment(CHARACTER1).isEmpty())
        }
    }

    @Nested
    inner class GetInventoryTest {

        @Test
        fun `Get items in a character's inventory`() {
            val item = Item(ID0, location = InInventory(CHARACTER0))
            val state = State(items = Storage(listOf(item)))

            assertEquals(listOf(item), state.getInventory(CHARACTER0))
        }

        @Test
        fun `Do not get the items in another character's inventory`() {
            val state = State(items = Storage(listOf(Item(ID0, location = InInventory(CHARACTER0)))))

            assertTrue(state.getInventory(CHARACTER1).isEmpty())
        }

        @Test
        fun `Do not get the items not in an inventory`() {
            val state = State(items = Storage(listOf(Item(ID0))))

            assertTrue(state.getInventory(CHARACTER0).isEmpty())
            assertTrue(state.getInventory(CHARACTER1).isEmpty())
        }
    }

    @Nested
    inner class GetItemsOfTemplateTest {

        @Test
        fun `Get items of a template`() {
            val item = Item(ID0, TEMPLATE0)
            val state = State(items = Storage(listOf(item)))

            assertEquals(listOf(item), state.getItems(TEMPLATE0))
        }

        @Test
        fun `Do not get the items in another template`() {
            val state = State(items = Storage(listOf(Item(ID0, TEMPLATE0))))

            assertTrue(state.getItems(TEMPLATE1).isEmpty())
        }
    }

    @Test
    fun `Get name of item`() {
        val state = State(
            itemTemplates = Storage(listOf(ItemTemplate(TEMPLATE0, "Test"))),
            items = Storage(listOf(Item(ID0, TEMPLATE0)))
        )

        assertEquals("Test", state.getName(ID0))
    }

}