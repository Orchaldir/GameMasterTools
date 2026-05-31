package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.NAME
import at.orchaldir.gm.STREET_TEMPLATE_ID_0
import at.orchaldir.gm.STREET_TEMPLATE_ID_1
import at.orchaldir.gm.UNKNOWN_MATERIAL_ID
import at.orchaldir.gm.assertFailMessage
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.visualization.settlement.createStreetGrammar
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StreetTemplateTest {
    val state = State(listOf(
        Storage(Material(MATERIAL_ID_0)),
        Storage(StreetTemplate(STREET_TEMPLATE_ID_0)),
    ))

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            fail(StreetTemplate(STREET_TEMPLATE_ID_1))
        }

        @Test
        fun `Grammar is invalid`() {
            val grammar = createStreetGrammar(Color.Gold, UNKNOWN_MATERIAL_ID)

            fail(StreetTemplate(STREET_TEMPLATE_ID_0, NAME, grammar))
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

    private fun fail(template: StreetTemplate) {
        val action = UpdateAction(template)

        assertFailMessage<IllegalArgumentException>("Requires unknown Street Template 1!") {
            REDUCER.invoke(
                state,
                action
            )
        }
    }
}