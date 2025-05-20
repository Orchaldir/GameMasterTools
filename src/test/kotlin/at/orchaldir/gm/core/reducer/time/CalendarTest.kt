package at.orchaldir.gm.core.reducer.time

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteCalendar
import at.orchaldir.gm.core.action.UpdateCalendar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.holiday.DayInYear
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CalendarTest {

    private val validMonths = ComplexMonths(listOf(MonthDefinition(NAME0, 10), MonthDefinition(NAME1, 10)))

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing calendar`() {
            val state = State(Storage(Calendar(CALENDAR_ID_0)))
            val action = DeleteCalendar(CALENDAR_ID_0)

            assertEquals(0, REDUCER.invoke(state, action).first.getCalendarStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteCalendar(CALENDAR_ID_0)

            assertIllegalArgument("Requires unknown Calendar 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a calendar with children`() {
            val state = State(
                Storage(
                    listOf(
                        Calendar(CALENDAR_ID_0),
                        Calendar(CALENDAR_ID_1, origin = ImprovedCalendar(CALENDAR_ID_0))
                    )
                )
            )
            val action = DeleteCalendar(CALENDAR_ID_0)

            assertIllegalArgument("Cannot delete Calendar 0, because it is used!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a calendar used by a culture`() {
            val culture = Culture(CULTURE_ID_0, calendar = CALENDAR_ID_0)
            val state = State(listOf(Storage(culture), Storage(Calendar(CALENDAR_ID_0))))
            val action = DeleteCalendar(CALENDAR_ID_0)

            assertIllegalArgument("Cannot delete Calendar 0, because it is used!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a calendar used by a holiday`() {
            val holiday = Holiday(HOLIDAY_ID_0, calendar = CALENDAR_ID_0)
            val state = State(listOf(Storage(holiday), Storage(Calendar(CALENDAR_ID_0))))
            val action = DeleteCalendar(CALENDAR_ID_0)

            assertIllegalArgument("Cannot delete Calendar 0, because it is used!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a calendar used by a periodical`() {
            val periodical = Periodical(PERIODICAL_ID_0, language = LANGUAGE_ID_1)
            val state = State(listOf(Storage(periodical), Storage(Calendar(CALENDAR_ID_0))))
            val action = DeleteCalendar(CALENDAR_ID_0)

            assertIllegalArgument("Cannot delete Calendar 0, because it is used!") {
                REDUCER.invoke(state, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateCalendar(Calendar(CALENDAR_ID_0, months = validMonths))

            assertIllegalArgument("Requires unknown Calendar 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Parent calendar must exist`() {
            val state = State(Storage(Calendar(CALENDAR_ID_0)))
            val action =
                UpdateCalendar(Calendar(CALENDAR_ID_0, months = validMonths, origin = ImprovedCalendar(CALENDAR_ID_1)))

            assertIllegalArgument("Parent calendar must exist!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A calendar cannot be its own parent`() {
            val state = State(Storage(Calendar(CALENDAR_ID_0)))
            val action =
                UpdateCalendar(Calendar(CALENDAR_ID_0, months = validMonths, origin = ImprovedCalendar(CALENDAR_ID_0)))

            assertIllegalArgument("Calendar cannot be its own parent!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Default calendar must not have an offset`() {
            val state = State(Storage(Calendar(CALENDAR_ID_0)))
            val action = UpdateCalendar(Calendar(CALENDAR_ID_0, eras = CalendarEras(Day(5)), months = validMonths))

            assertIllegalArgument("Default Calendar must not have an offset!") { REDUCER.invoke(state, action) }
        }

        @Nested
        inner class WeekdaysTest {

            @Test
            fun `At least 2 weekdays`() {
                val state = State(Storage(Calendar(CALENDAR_ID_0)))
                val weekdays = Weekdays(listOf(WeekDay(NAME0)))
                val calendar = Calendar(CALENDAR_ID_0, days = weekdays, months = validMonths)
                val action = UpdateCalendar(calendar)

                assertIllegalArgument("Requires at least 2 weekdays") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Weekdays need unique names`() {
                val state = State(Storage(Calendar(CALENDAR_ID_0)))
                val weekdays = Weekdays(listOf(WeekDay(NAME0), WeekDay(NAME0)))
                val calendar = Calendar(CALENDAR_ID_0, days = weekdays, months = validMonths)
                val action = UpdateCalendar(calendar)

                assertIllegalArgument("The names of the weekdays need to be unique!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Valid weekdays`() {
                val state = State(Storage(Calendar(CALENDAR_ID_0)))
                val weekdays = Weekdays(listOf(WeekDay(NAME0), WeekDay(NAME1)))
                val calendar = Calendar(CALENDAR_ID_0, days = weekdays, months = validMonths)
                val action = UpdateCalendar(calendar)

                assertEquals(calendar, REDUCER.invoke(state, action).first.getCalendarStorage().get(CALENDAR_ID_0))
            }
        }

        @Nested
        inner class MonthsTest {

            @Test
            fun `At least 2 months`() {
                val state = State(Storage(Calendar(CALENDAR_ID_0)))
                val months = ComplexMonths(listOf(MonthDefinition(NAME0, 10)))
                val calendar = Calendar(CALENDAR_ID_0, months = months)
                val action = UpdateCalendar(calendar)

                assertIllegalArgument("Requires at least 2 months") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `At least 2 days per month`() {
                val state = State(Storage(Calendar(CALENDAR_ID_0)))
                val months = ComplexMonths(listOf(MonthDefinition(NAME0, 1), MonthDefinition(NAME1, 1)))
                val calendar = Calendar(CALENDAR_ID_0, months = months)
                val action = UpdateCalendar(calendar)

                assertIllegalArgument("Requires at least 2 days per month") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Months need unique names`() {
                val state = State(Storage(Calendar(CALENDAR_ID_0)))
                val months = ComplexMonths(listOf(MonthDefinition(NAME0, 10), MonthDefinition(NAME0, 10)))
                val calendar = Calendar(CALENDAR_ID_0, months = months)
                val action = UpdateCalendar(calendar)

                assertIllegalArgument("The names of the months need to be unique!") { REDUCER.invoke(state, action) }
            }
        }

        @Test
        fun `Update would make holiday invalid`() {
            val holiday = Holiday(HOLIDAY_ID_0, calendar = CALENDAR_ID_0, relativeDate = DayInYear(0, 2))
            val state = State(listOf(Storage(holiday), Storage(Calendar(CALENDAR_ID_0))))
            val calendar = Calendar(CALENDAR_ID_0, months = validMonths)
            val action = UpdateCalendar(calendar)

            assertIllegalArgument("Holiday is in an unknown month!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Successful update with a holiday`() {
            val holiday = Holiday(HOLIDAY_ID_0, calendar = CALENDAR_ID_0, relativeDate = DayInYear(0, 0))
            val state = State(listOf(Storage(holiday), Storage(Calendar(CALENDAR_ID_0))))
            val calendar = Calendar(CALENDAR_ID_0, months = validMonths)
            val action = UpdateCalendar(calendar)

            assertEquals(calendar, REDUCER.invoke(state, action).first.getCalendarStorage().get(CALENDAR_ID_0))
        }

        @Test
        fun `Successful update`() {
            val state = State(Storage(listOf(Calendar(CALENDAR_ID_0), Calendar(CALENDAR_ID_1))))
            val calendar = Calendar(CALENDAR_ID_0, months = validMonths, origin = ImprovedCalendar(CALENDAR_ID_1))
            val action = UpdateCalendar(calendar)

            assertEquals(calendar, REDUCER.invoke(state, action).first.getCalendarStorage().get(CALENDAR_ID_0))
        }
    }

}