package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.BUSINESS_TEMPLATE_ID_0
import at.orchaldir.gm.CALENDAR0
import at.orchaldir.gm.UNKNOWN_BUSINESS_TEMPLATE_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BusinessTemplate
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class BusinessTemplateTest {

    @Nested
    inner class UpdateTest {

        private val STATE = State(
            listOf(
                Storage(BusinessTemplate(BUSINESS_TEMPLATE_ID_0)),
                Storage(CALENDAR0),
            )
        )

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(BusinessTemplate(UNKNOWN_BUSINESS_TEMPLATE_ID))

            assertIllegalArgument("Requires unknown Business Template 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Test Success`() {
            val business = BusinessTemplate(BUSINESS_TEMPLATE_ID_0, Name.init("Test"))
            val action = UpdateAction(business)

            val newState = REDUCER.invoke(STATE, action)

            assertEquals(business, newState.first.getBusinessTemplateStorage().get(BUSINESS_TEMPLATE_ID_0))
        }
    }

}