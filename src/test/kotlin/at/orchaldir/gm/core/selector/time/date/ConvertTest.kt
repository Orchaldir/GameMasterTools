package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.DayRange
import at.orchaldir.gm.core.model.time.date.Year
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ConvertTest {

    private val days = Weekdays(listOf(WeekDay(DAY_NAME0), WeekDay(DAY_NAME1)))
    private val month0 = MonthDefinition(NAME0, 2)
    private val month1 = MonthDefinition(NAME1, 3)
    private val month2 = MonthDefinition(NAME2, 4)
    private val months = ComplexMonths(listOf(month0, month1, month2))
    private val defaultCalendar = Calendar(CALENDAR_ID_0, days = days, months = months)
    private val otherCalendar = Calendar(CALENDAR_ID_1, days = days, months = months, eras = CalendarEras(Day(100)))

    @ParameterizedTest
    @MethodSource("at.orchaldir.gm.core.selector.time.date.ConvertTest#provideDays")
    fun `Convert day to the default calendar`(a: Day, b: Day) {
        assertEquals(b, convertDate(otherCalendar, defaultCalendar, a))
    }

    @ParameterizedTest
    @MethodSource("at.orchaldir.gm.core.selector.time.date.ConvertTest#provideDays")
    fun `Convert day from the default calendar`(a: Day, b: Day) {
        assertEquals(a, convertDate(defaultCalendar, otherCalendar, b))
    }

    @Test
    fun `Convert year to the default calendar`() {
        assertEquals(DayRange(109, 117), convertDate(otherCalendar, defaultCalendar, Year(1)))
    }

    @Test
    fun `Convert year to from the default calendar`() {
        assertEquals(DayRange(-91, -83), convertDate(defaultCalendar, otherCalendar, Year(1)))
    }

    @Test
    fun `Convert year to the same calendar`() {
        assertEquals(Year(1), convertDate(otherCalendar, otherCalendar, Year(1)))
    }

    companion object {
        @JvmStatic
        fun provideDays(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Day(-1), Day(99)),
                Arguments.of(Day(0), Day(100)),
                Arguments.of(Day(1), Day(101)),
            )
        }
    }
}