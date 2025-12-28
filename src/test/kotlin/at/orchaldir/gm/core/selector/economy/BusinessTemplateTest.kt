package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.BUSINESS_ID_0
import at.orchaldir.gm.BUSINESS_TEMPLATE_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessTemplate
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BusinessTemplateTest {

    @Nested
    inner class CanDeleteTest {
        private val template = BusinessTemplate(BUSINESS_TEMPLATE_ID_0)
        private val state = State(
            listOf(
                Storage(template),
            )
        )

        @Test
        fun `Cannot delete a business template used by a business`() {
            val business = Business(BUSINESS_ID_0, templates = setOf(BUSINESS_TEMPLATE_ID_0))
            val newState = state.updateStorage(Storage(business))

            failCanDelete(newState, BUSINESS_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(BUSINESS_TEMPLATE_ID_0).addId(blockingId),
                state.canDeleteBusinessTemplate(BUSINESS_TEMPLATE_ID_0)
            )
        }
    }

}