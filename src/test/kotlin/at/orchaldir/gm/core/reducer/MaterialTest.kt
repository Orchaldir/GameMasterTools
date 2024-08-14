package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteMaterial
import at.orchaldir.gm.core.action.UpdateMaterial
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.item.Shirt
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = MaterialId(0)
private val TEMPLATE0 = ItemTemplateId(0)

class MaterialTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing material`() {
            val state = State(Storage(listOf(Material(ID0))))
            val action = DeleteMaterial(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getMaterialStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteMaterial(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a material used by an item template`() {
            val template = ItemTemplate(TEMPLATE0, equipment = Shirt(material = ID0))
            val state = State(
                listOf(
                    Storage(listOf(template)),
                    Storage(listOf(Material(ID0)))
                )
            )
            val action = DeleteMaterial(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateMaterial(Material(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Material exists`() {
            val state = State(Storage(listOf(Material(ID0))))
            val material = Material(ID0, "Test")
            val action = UpdateMaterial(material)

            assertEquals(material, REDUCER.invoke(state, action).first.getMaterialStorage().get(ID0))
        }
    }

}