package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.material.Wood
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.visualization.settlement.createStreetGrammar
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StreetTemplateTest {
    val state = State(
        listOf(
            Storage(Material(MATERIAL_ID_0, properties = MaterialProperties(Wood()))),
            Storage(StreetTemplate(STREET_TEMPLATE_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            fail(StreetTemplate(STREET_TEMPLATE_ID_1), "Requires unknown Street Template 1!")
        }

        @Test
        fun `Grammar is invalid`() {
            val grammar = createStreetGrammar(Color.Gold, UNKNOWN_MATERIAL_ID)

            fail(StreetTemplate(STREET_TEMPLATE_ID_0, NAME, grammar), "Requires unknown Material 99!")
        }

        @Test
        fun `Update is valid`() {
            val grammar = createStreetGrammar(Color.Gold, MATERIAL_ID_0)
            val street = StreetTemplate(STREET_TEMPLATE_ID_0, NAME, grammar)
            val action = UpdateAction(street)

            assertEquals(
                street,
                REDUCER.invoke(state, action).first.getStreetTemplateStorage().get(STREET_TEMPLATE_ID_0)
            )
        }
    }

    private fun fail(template: StreetTemplate, message: String) {
        val action = UpdateAction(template)

        assertFailMessage<IllegalArgumentException>(message) {
            REDUCER.invoke(
                state,
                action
            )
        }
    }
}