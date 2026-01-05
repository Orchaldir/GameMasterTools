package at.orchaldir.gm.core.selector.time

import at.orchaldir.gm.CULTURE_ID_0
import at.orchaldir.gm.HOLIDAY_ID_0
import at.orchaldir.gm.ORGANIZATION_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HolidayTest {

    @Nested
    inner class CanDeleteTest {
        private val holiday = Holiday(HOLIDAY_ID_0)
        private val state = State(
            listOf(
                Storage(holiday),
            )
        )

        @Test
        fun `Cannot delete a holiday used by a culture`() {
            val culture = Culture(CULTURE_ID_0, holidays = setOf(HOLIDAY_ID_0))
            val newState = state.updateStorage(culture)

            failCanDelete(newState, CULTURE_ID_0)
        }

        @Test
        fun `Cannot delete a holiday used by a organization`() {
            val organization = Organization(ORGANIZATION_ID_0, holidays = setOf(HOLIDAY_ID_0))
            val newState = state.updateStorage(organization)

            failCanDelete(newState, ORGANIZATION_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(HOLIDAY_ID_0).addId(blockingId), state.canDeleteHoliday(HOLIDAY_ID_0))
        }
    }
}