package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.CALENDAR_ID_0
import at.orchaldir.gm.CALENDAR_ID_1
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.date.Day
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ConvertTest {

    private val days = Weekdays(listOf(WeekDay("d0"), WeekDay("d1")))
    private val month0 = MonthDefinition("First", 2)
    private val month1 = MonthDefinition("Second", 3)
    private val month2 = MonthDefinition("Third", 4)
    private val months = ComplexMonths(listOf(month0, month1, month2))
    private val defaultCalendar = Calendar(CALENDAR_ID_0, days = days, months = months)
    private val otherCalendar = Calendar(CALENDAR_ID_1, days = days, months = months, eras = CalendarEras(Day(100)))

    @ParameterizedTest
    @MethodSource("at.orchaldir.gm.core.selector.time.date.ConvertTest#provideDays")
    fun `Convert day to default calendar`(a: Day, b: Day) {
        assertEquals(b, convertDate(otherCalendar, defaultCalendar, a))
    }

    @ParameterizedTest
    @MethodSource("at.orchaldir.gm.core.selector.time.date.ConvertTest#provideDays")
    fun `Convert day from default calendar`(a: Day, b: Day) {
        assertEquals(a, convertDate(defaultCalendar, otherCalendar, b))
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