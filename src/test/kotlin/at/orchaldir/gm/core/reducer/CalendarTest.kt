package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteCalendar
import at.orchaldir.gm.core.action.UpdateCalendar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CalendarId(0)
private val ID1 = CalendarId(1)
private val CULTURE0 = CultureId(1)
private val VALID_MONTHS = listOf(Month("a", 10), Month("b", 10))

class CalendarTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing calendar`() {
            val state = State(Storage(listOf(Calendar(ID0))))
            val action = DeleteCalendar(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getCalendarStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteCalendar(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a calendar with children`() {
            val state = State(Storage(listOf(Calendar(ID0), Calendar(ID1, origin = ImprovedCalendar(ID0)))))
            val action = DeleteCalendar(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a calendar used by a culture`() {
            val culture = Culture(CULTURE0, calendar = ID0)
            val state = State(listOf(Storage(listOf(culture)), Storage(listOf(Calendar(ID0)))))
            val action = DeleteCalendar(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateCalendar(Calendar(ID0, months = VALID_MONTHS))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Parent calendar must exist`() {
            val state = State(Storage(listOf(Calendar(ID0))))
            val action = UpdateCalendar(Calendar(ID0, months = VALID_MONTHS, origin = ImprovedCalendar(ID1)))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A calendar cannot be its own parent`() {
            val state = State(Storage(listOf(Calendar(ID0))))
            val action = UpdateCalendar(Calendar(ID0, months = VALID_MONTHS, origin = ImprovedCalendar(ID0)))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Nested
        inner class WeekdaysTest {

            @Test
            fun `At least 2 months`() {
                val state = State(Storage(listOf(Calendar(ID0))))
                val weekdays = Weekdays(listOf(WeekDay("a")))
                val calendar = Calendar(ID0, days = weekdays, months = VALID_MONTHS)
                val action = UpdateCalendar(calendar)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Months need unique names`() {
                val state = State(Storage(listOf(Calendar(ID0))))
                val weekdays = Weekdays(listOf(WeekDay("a"), WeekDay("a")))
                val calendar = Calendar(ID0, days = weekdays, months = VALID_MONTHS)
                val action = UpdateCalendar(calendar)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Valid weekdays`() {
                val state = State(Storage(listOf(Calendar(ID0))))
                val weekdays = Weekdays(listOf(WeekDay("a"), WeekDay("b")))
                val calendar = Calendar(ID0, days = weekdays, months = VALID_MONTHS)
                val action = UpdateCalendar(calendar)

                assertEquals(calendar, REDUCER.invoke(state, action).first.getCalendarStorage().get(ID0))
            }
        }

        @Nested
        inner class MonthsTest {

            @Test
            fun `At least 2 months`() {
                val state = State(Storage(listOf(Calendar(ID0))))
                val calendar = Calendar(ID0, months = listOf(Month("a", 10)))
                val action = UpdateCalendar(calendar)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `At least 2 days per month`() {
                val state = State(Storage(listOf(Calendar(ID0))))
                val calendar = Calendar(ID0, months = listOf(Month("a", 1), Month("b", 1)))
                val action = UpdateCalendar(calendar)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Months need unique names`() {
                val state = State(Storage(listOf(Calendar(ID0))))
                val calendar = Calendar(ID0, months = listOf(Month("a", 10), Month("a", 10)))
                val action = UpdateCalendar(calendar)

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }
        }

        @Test
        fun `Parent calendar exist`() {
            val state = State(Storage(listOf(Calendar(ID0), Calendar(ID1))))
            val calendar = Calendar(ID0, months = VALID_MONTHS, origin = ImprovedCalendar(ID1))
            val action = UpdateCalendar(calendar)

            assertEquals(calendar, REDUCER.invoke(state, action).first.getCalendarStorage().get(ID0))
        }
    }

}