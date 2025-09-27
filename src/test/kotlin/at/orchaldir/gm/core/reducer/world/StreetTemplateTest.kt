package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.NAME
import at.orchaldir.gm.STREET_TEMPLATE_ID_0
import at.orchaldir.gm.assertFailMessage
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StreetTemplateTest {

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(StreetTemplate(STREET_TEMPLATE_ID_0))

            assertFailMessage<IllegalArgumentException>("Requires unknown Street Template 0!") {
                REDUCER.invoke(
                    State(),
                    action
                )
            }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(StreetTemplate(STREET_TEMPLATE_ID_0)))
            val street = StreetTemplate(STREET_TEMPLATE_ID_0, NAME, Color.Gold)
            val action = UpdateAction(street)

            assertEquals(
                street,
                REDUCER.invoke(state, action).first.getStreetTemplateStorage().get(STREET_TEMPLATE_ID_0)
            )
        }
    }

}