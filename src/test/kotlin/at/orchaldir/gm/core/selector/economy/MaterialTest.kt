package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.CURRENCY_UNIT_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_1
import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.MATERIAL_ID_1
import at.orchaldir.gm.MOON_ID_0
import at.orchaldir.gm.REGION_ID_0
import at.orchaldir.gm.STREET_TEMPLATE_ID_0
import at.orchaldir.gm.TEXT_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialCost
import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.selector.item.getEquipmentMadeOf
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MaterialTest {

    @Nested
    inner class CanDeleteTest {
        private val material = Material(MATERIAL_ID_0)
        private val state = State(
            listOf(
                Storage(material),
            )
        )

        @Test
        fun `Cannot delete a material used by a currency unit`() {
            val unit = CurrencyUnit(CURRENCY_UNIT_ID_0, format = Coin(MATERIAL_ID_0))
            val newState = state.updateStorage(Storage(unit))

            failCanDelete(newState, CURRENCY_UNIT_ID_0)
        }

        @Test
        fun `Cannot delete a material used by an equipment`() {
            val equipment = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillLookupItemPart(MATERIAL_ID_0)))
            val newState = state.updateStorage(Storage(equipment))

            failCanDelete(newState, EQUIPMENT_ID_0)
        }

        @Test
        fun `Cannot delete a material used by a street template`() {
            val template = StreetTemplate(STREET_TEMPLATE_ID_0, materialCost = MaterialCost(MATERIAL_ID_0))
            val newState = state.updateStorage(Storage(template))

            failCanDelete(newState, STREET_TEMPLATE_ID_0)
        }

        @Test
        fun `Cannot delete a material used by a text`() {
            val text = Text(TEXT_ID_0, format = Book(Hardcover(), 100))
            val newState = state.updateStorage(Storage(text))

            failCanDelete(newState, TEXT_ID_0)
        }

        @Test
        fun `Cannot delete a material contained in a region`() {
            val region = Region(REGION_ID_0, resources = setOf(MATERIAL_ID_0))
            val newState = state.updateStorage(Storage(region))

            failCanDelete(newState, REGION_ID_0)
        }

        @Test
        fun `Cannot delete a material contained in a moon`() {
            val moon = Moon(MOON_ID_0, resources = setOf(MATERIAL_ID_0))
            val newState = state.updateStorage(Storage(moon))

            failCanDelete(newState, MOON_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(MATERIAL_ID_0).addId(blockingId),
                state.canDeleteMaterial(MATERIAL_ID_0)
            )
        }
    }



    @Test
    fun `Get all item templates using a material`() {
        val template0 = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillLookupItemPart(MATERIAL_ID_0)))
        val template1 = Equipment(EQUIPMENT_ID_1, data = Shirt(main = FillLookupItemPart(MATERIAL_ID_0)))
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