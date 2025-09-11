package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteMaterial
import at.orchaldir.gm.core.action.UpdateMaterial
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
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MaterialTest {

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