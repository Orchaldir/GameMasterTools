package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteMaterial
import at.orchaldir.gm.core.action.UpdateMaterial
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialCost
import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.item.FillLookupItemPart
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MaterialTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing material`() {
            val state = State(Storage(Material(MATERIAL_ID_0)))
            val action = DeleteMaterial(MATERIAL_ID_0)

            assertEquals(0, REDUCER.invoke(state, action).first.getMaterialStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteMaterial(MATERIAL_ID_0)

            assertIllegalArgument("Requires unknown Material 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a material used by a currency unit`() {
            val unit = CurrencyUnit(CURRENCY_UNIT_ID_0, format = Coin())
            val state = State(
                listOf(
                    Storage(unit),
                    Storage(Material(MATERIAL_ID_0)),
                )
            )
            val action = DeleteMaterial(MATERIAL_ID_0)

            assertIllegalArgument("Cannot delete Material 0, because it is used!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a material used by an equipment`() {
            val equipment = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillLookupItemPart(MATERIAL_ID_0)))
            val state = State(
                listOf(
                    Storage(equipment),
                    Storage(Material(MATERIAL_ID_0)),
                )
            )
            val action = DeleteMaterial(MATERIAL_ID_0)

            assertIllegalArgument("Cannot delete Material 0, because it is used!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a material used by a street template`() {
            val template = StreetTemplate(STREET_TYPE_ID_0, materialCost = MaterialCost(MATERIAL_ID_0))
            val state = State(
                listOf(
                    Storage(template),
                    Storage(Material(MATERIAL_ID_0)),
                )
            )
            val action = DeleteMaterial(MATERIAL_ID_0)

            assertIllegalArgument("Cannot delete Material 0, because it is used!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a material used by a book`() {
            val book = Text(TEXT_ID_0, format = Book(Hardcover(), 100))
            val state = State(
                listOf(
                    Storage(book),
                    Storage(Material(MATERIAL_ID_0)),
                )
            )
            val action = DeleteMaterial(MATERIAL_ID_0)

            assertIllegalArgument("Cannot delete Material 0, because it is used!") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateMaterial(Material(MATERIAL_ID_0))

            assertIllegalArgument("Requires unknown Material 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Material exists`() {
            val state = State(Storage(Material(MATERIAL_ID_0)))
            val material = Material(MATERIAL_ID_0, color = Color.Green)
            val action = UpdateMaterial(material)

            assertEquals(material, REDUCER.invoke(state, action).first.getMaterialStorage().get(MATERIAL_ID_0))
        }
    }

}