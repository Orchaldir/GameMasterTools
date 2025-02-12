package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteMaterial
import at.orchaldir.gm.core.action.UpdateMaterial
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialCost
import at.orchaldir.gm.core.model.world.street.StreetTemplate
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
        fun `Cannot delete a material used by an item template`() {
            val template = Equipment(ITEM_TEMPLATE_ID_0, equipment = Shirt(material = MATERIAL_ID_0))
            val state = State(
                listOf(
                    Storage(template),
                    Storage(Material(MATERIAL_ID_0)),
                )
            )
            val action = DeleteMaterial(MATERIAL_ID_0)

            assertIllegalArgument("Material 0 is used") { REDUCER.invoke(state, action) }
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

            assertIllegalArgument("Material 0 is used") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a material used by a book`() {
            val book = Text(TEXT_ID_0, format = Book(100, Hardcover()))
            val state = State(
                listOf(
                    Storage(book),
                    Storage(Material(MATERIAL_ID_0)),
                )
            )
            val action = DeleteMaterial(MATERIAL_ID_0)

            assertIllegalArgument("Material 0 is used") { REDUCER.invoke(state, action) }
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
            val material = Material(MATERIAL_ID_0, "Test")
            val action = UpdateMaterial(material)

            assertEquals(material, REDUCER.invoke(state, action).first.getMaterialStorage().get(MATERIAL_ID_0))
        }
    }

}