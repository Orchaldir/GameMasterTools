package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.item.InInventory
import at.orchaldir.gm.core.model.item.Item
import at.orchaldir.gm.core.model.item.ItemId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val ID0 = ItemId(0)
private val CHARACTER0 = CharacterId(0)
private val CHARACTER1 = CharacterId(1)

class ItemTest {

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

}