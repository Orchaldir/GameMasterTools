package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.date.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertNull

class MoveTest {

    private val month0 = MonthDefinition(NAME0, 2)
    private val month1 = MonthDefinition(NAME1, 3)
    private val days = Weekdays(listOf(WeekDay(DAY_NAME0), WeekDay(DAY_NAME1)))
    private val calendar0 = Calendar(CalendarId(0), months = ComplexMonths(listOf(month0, month1)))
    private val calendar1 = calendar0.copy(days = days)
    private val startMonth1Bc1 = Day(-5)
    private val startMonth2Bc1 = Day(-3)
    private val startMonth1Ad1 = Day(0)
    private val startMonth2Ad1 = Day(2)
    private val startMonth1Ad2 = Day(5)
    private val endMonth2Bc1 = Day(-1)
    private val endMonth1Ad1 = Day(1)

    @Nested
    inner class MoveUpTest {

        @Test
        fun `Move day up to month`() {
            assertEquals(Month(2), calendar0.moveUp(Day(5)))
        }

        @Test
        fun `Move day up to week`() {
            assertToWeek(-5, -3)
            assertToWeek(-4, -2)
            assertToWeek(-3, -2)
            assertToWeek(-2, -1)
            assertToWeek(-1, -1)
            assertToWeek(0, 0)
            assertToWeek(1, 0)
            assertToWeek(2, 1)
            assertToWeek(3, 1)
            assertToWeek(4, 2)
            assertToWeek(5, 2)
            assertToWeek(6, 3)
        }

        private fun assertToWeek(day: Int, week: Int) {
            val input = Day(day)
            val result = Week(week)

            assertEquals(result, calendar1.moveUp(input))
            assertEquals(result, calendar1.moveUpDayToWeek(input))
        }

        @Test
        fun `Fail to move week up, because calendar doesn't have weeks`() {
            assertIllegalArgument("Calendar 0 doesn't support weeks!") {
                calendar0.moveUp(Week(3))
            }
        }

        @Test
        fun `Move week up`() {
            assertEquals(Month(2), calendar1.moveUp(Week(3)))
        }

        @Test
        fun `Move month up`() {
            assertEquals(Year(1), calendar0.moveUp(Month(3)))
        }

        @Test
        fun `Move year up`() {
            assertEquals(Decade(3), calendar0.moveUp(Year(33)))
        }

        @Test
        fun `Move decade up`() {
            assertEquals(Century(1), calendar0.moveUp(Decade(12)))
        }

        @Test
        fun `Move century up`() {
            assertEquals(Millennium(1), calendar0.moveUp(Century(12)))
        }

        @Test
        fun `Move millennium up`() {
            assertNull(calendar0.moveUp(Millennium(12)))
        }
    }

    @Nested
    inner class GetDayRangeTest {

        @Test
        fun `Test day`() {
            assertEquals(
                DayRange(Day(7), Day(7)),
                calendar1.getDayRange(Day(7)),
            )
        }

        @Test
        fun `Test week`() {
            assertEquals(
                DayRange(Day(0), Day(1)),
                calendar1.getDayRange(Week(0)),
            )
        }

        @Test
        fun `Test year`() {
            assertEquals(
                DayRange(Day(5), Day(9)),
                calendar1.getDayRange(Year(1)),
            )
        }

    }

    @Nested
    inner class WeekTest {

        @Nested
        inner class GetStartOfWeekTest {

            @Test
            fun `Get start of a week`() {
                assertEquals(Day(-2), calendar1.getStartDayOfWeek(Week(-1)))
                assertEquals(Day(0), calendar1.getStartDayOfWeek(Week(0)))
                assertEquals(Day(2), calendar1.getStartDayOfWeek(Week(1)))
            }

            @Test
            fun `Calendar doesn't have weeks`() {
                assertIllegalArgument("Calendar 0 doesn't support weeks!") {
                    calendar0.getStartDayOfWeek(Week(-1))
                }
            }
        }

        @Nested
        inner class GetEndOfWeekTest {

            @Test
            fun `Get end of a week`() {
                assertEquals(Day(-1), calendar1.getEndDayOfWeek(Week(-1)))
                assertEquals(Day(1), calendar1.getEndDayOfWeek(Week(0)))
                assertEquals(Day(3), calendar1.getEndDayOfWeek(Week(1)))
            }

            @Test
            fun `Calendar doesn't have weeks`() {
                assertIllegalArgument("Calendar 0 doesn't support weeks!") {
                    calendar0.getEndDayOfWeek(Week(-1))
                }
            }
        }

        @Test
        fun `Get start week of a year`() {
            assertEquals(Week(-3), calendar1.getStartWeekOfYear(Year(-1)))
            assertEquals(Week(0), calendar1.getStartWeekOfYear(Year(0)))
            assertEquals(Week(2), calendar1.getStartWeekOfYear(Year(1)))
        }

        @Test
        fun `Get end week of a year`() {
            assertEquals(Week(-1), calendar1.getEndWeekOfYear(Year(-1)))
            assertEquals(Week(2), calendar1.getEndWeekOfYear(Year(0)))
            assertEquals(Week(4), calendar1.getEndWeekOfYear(Year(1)))
        }
    }

    @Nested
    inner class MonthTest {

        @Nested
        inner class GetStartOfMonthTest {

            @Test
            fun `Get start of the second month in 1 AD`() {
                assertEquals(startMonth2Ad1, calendar0.getStartDayOfMonth(Month(1)))
            }

            @Test
            fun `Get start of the second month in BC 1`() {
                assertEquals(startMonth2Bc1, calendar0.getStartDayOfMonth(Month(-1)))
            }

            @Test
            fun `Get start of the first month in the second year`() {
                assertEquals(startMonth1Ad2, calendar0.getStartDayOfMonth(Month(2)))
            }
        }

        @Nested
        inner class GetEndOfMonthTest {

            @Test
            fun `Get end of the first month in 1 AD`() {
                assertEquals(endMonth1Ad1, calendar0.getEndDayOfMonth(Month(0)))
            }

            @Test
            fun `Get end of the second month in BC 1`() {
                assertEquals(endMonth2Bc1, calendar0.getEndDayOfMonth(Month(-1)))
            }
        }
    }

    @Nested
    inner class MonthWithDayTest {

        @Nested
        inner class GetStartOfMonthTest {

            @Test
            fun `Get start of the second month in 1 AD`() {
                assertEquals(startMonth2Ad1, calendar0.getStartOfMonth(startMonth2Ad1))
                assertEquals(startMonth2Ad1, calendar0.getStartOfMonth(Day(3)))
                assertEquals(startMonth2Ad1, calendar0.getStartOfMonth(Day(4)))
            }

            @Test
            fun `Get start of the second month in BC 1`() {
                assertEquals(startMonth2Bc1, calendar0.getStartOfMonth(startMonth2Bc1))
                assertEquals(startMonth2Bc1, calendar0.getStartOfMonth(Day(-2)))
                assertEquals(startMonth2Bc1, calendar0.getStartOfMonth(endMonth2Bc1))
            }

            @Test
            fun `Get start of the first month in the second year`() {
                assertEquals(startMonth1Ad2, calendar0.getStartOfMonth(startMonth1Ad2))
                assertEquals(startMonth1Ad2, calendar0.getStartOfMonth(Day(6)))
            }
        }

        @Nested
        inner class GetEndOfMonthTest {

            @Test
            fun `Get end of the first month in 1 AD`() {
                assertEquals(endMonth1Ad1, calendar0.getEndOfMonth(startMonth1Ad1))
                assertEquals(endMonth1Ad1, calendar0.getEndOfMonth(endMonth1Ad1))
            }

            @Test
            fun `Get end of the second month in BC 1`() {
                assertEquals(endMonth2Bc1, calendar0.getEndOfMonth(startMonth2Bc1))
                assertEquals(endMonth2Bc1, calendar0.getEndOfMonth(Day(-2)))
                assertEquals(endMonth2Bc1, calendar0.getEndOfMonth(endMonth2Bc1))
            }
        }

        @Nested
        inner class GetStartOfNextMonthTest {
            @Test
            fun `Get start of next month in first era`() {
                assertEquals(startMonth2Bc1, calendar0.getStartOfNextMonth(startMonth1Bc1))
                assertEquals(startMonth2Bc1, calendar0.getStartOfNextMonth(Day(-4)))
            }

            @Test
            fun `Get start of next month across era`() {
                assertEquals(startMonth1Ad1, calendar0.getStartOfNextMonth(startMonth2Bc1))
                assertEquals(startMonth1Ad1, calendar0.getStartOfNextMonth(Day(-2)))
                assertEquals(startMonth1Ad1, calendar0.getStartOfNextMonth(endMonth2Bc1))
            }

            @Test
            fun `Get start of next month`() {
                assertEquals(startMonth2Ad1, calendar0.getStartOfNextMonth(startMonth1Ad1))
                assertEquals(startMonth2Ad1, calendar0.getStartOfNextMonth(endMonth1Ad1))
            }

            @Test
            fun `In the next year`() {
                assertEquals(startMonth1Ad2, calendar0.getStartOfNextMonth(startMonth2Ad1))
                assertEquals(startMonth1Ad2, calendar0.getStartOfNextMonth(Day(3)))
                assertEquals(startMonth1Ad2, calendar0.getStartOfNextMonth(Day(4)))
            }
        }

        @Nested
        inner class GetStartOfPreviousMonthTest {
            @Test
            fun `Get start of previous month in first era`() {
                assertEquals(startMonth1Bc1, calendar0.getStartOfPreviousMonth(startMonth2Bc1))
                assertEquals(startMonth1Bc1, calendar0.getStartOfPreviousMonth(Day(-2)))
                assertEquals(startMonth1Bc1, calendar0.getStartOfPreviousMonth(endMonth2Bc1))
            }

            @Test
            fun `Get start of previous month`() {
                assertEquals(startMonth2Bc1, calendar0.getStartOfPreviousMonth(startMonth1Ad1))
                assertEquals(startMonth2Bc1, calendar0.getStartOfPreviousMonth(endMonth1Ad1))
            }

            @Test
            fun `In the next year`() {
                assertEquals(startMonth1Ad1, calendar0.getStartOfPreviousMonth(startMonth2Ad1))
                assertEquals(startMonth1Ad1, calendar0.getStartOfPreviousMonth(Day(3)))
                assertEquals(startMonth1Ad1, calendar0.getStartOfPreviousMonth(Day(4)))
            }
        }
    }

    @Nested
    inner class YearTest {

        @ParameterizedTest
        @MethodSource("at.orchaldir.gm.core.selector.time.date.MoveTest#provideStartOfYear")
        fun `Get the start of a year`(year: Year, day: Day) {
            assertEquals(day, calendar0.getStartDayOfYear(year))
        }

        @Test
        fun `Get the end of a year`() {
            assertEquals(endMonth2Bc1, calendar0.getEndDayOfYear(Year(-1)))
            assertEquals(Day(4), calendar0.getEndDayOfYear(Year(0)))
            assertEquals(Day(9), calendar0.getEndDayOfYear(Year(1)))
            assertEquals(Day(14), calendar0.getEndDayOfYear(Year(2)))
        }

        @Nested
        inner class GetStartYearTest {

            @Test
            fun `Get the year of a day`() {
                assertStartYear(Day(-10), -2)
                assertStartYear(startMonth1Bc1, -1)
                assertStartYear(startMonth1Ad1, 0)
                assertStartYear(startMonth1Ad2, 1)
                assertStartYear(Day(10), 2)
            }

            @Test
            fun `Get the year of a week`() {
                assertStartYear(Week(-1), -1)
                assertStartYear(Week(0), 0)
                assertStartYear(Week(1), 0)
                assertStartYear(Week(2), 0)
                assertStartYear(Week(3), 1)
            }

            @Test
            fun `Get the year of a month`() {
                assertStartYear(Month(-1), -1)
                assertStartYear(Month(0), 0)
                assertStartYear(Month(1), 0)
                assertStartYear(Month(2), 1)
            }

            @Test
            fun `Get the year of a year`() {
                assertStartYear(Year(2), 2)
            }

            @Test
            fun `Get the start year of a decade`() {
                assertStartYear(Decade(-2), -19)
                assertStartYear(Decade(-1), -9)
                assertStartYear(Decade(0), 0)
                assertStartYear(Decade(1), 9)
                assertStartYear(Decade(2), 19)
            }

            @Test
            fun `Get the start year of a century`() {
                assertStartYear(Century(-2), -199)
                assertStartYear(Century(-1), -99)
                assertStartYear(Century(0), 0)
                assertStartYear(Century(1), 99)
                assertStartYear(Century(2), 199)
            }

            @Test
            fun `Get the start year of a millennium`() {
                assertStartYear(Millennium(-1), -999)
                assertStartYear(Millennium(0), 0)
                assertStartYear(Millennium(1), 999)
            }

            private fun assertStartYear(input: Date, year: Int) {
                val year = Year(year)
                val display = resolveYear(year)

                assertEquals(year, calendar1.getStartYear(input))
                assertEquals(display, calendar1.getStartDisplayYear(input))
            }
        }
    }

    @Nested
    inner class DecadeTest {

        @Test
        fun `Display the start & end of a positive decade`() {
            val decade = Decade(186)

            assertDisplay(calendar0.getStartDayOfDecade(decade), "1.1.1860 AD")
            assertDisplay(calendar0.getEndDayOfDecade(decade), "3.2.1869 AD")
        }

        @Test
        fun `The AD 0s only have 9 years`() {
            val decade = Decade(0)

            assertDisplay(calendar0.getStartDayOfDecade(decade), "1.1.1 AD")
            assertDisplay(calendar0.getEndDayOfDecade(decade), "3.2.9 AD")
        }

        @Test
        fun `The 0s BC only have 9 years`() {
            val decade = Decade(-1)

            assertDisplay(calendar0.getStartDayOfDecade(decade), "BC 1.1.9")
            assertDisplay(calendar0.getEndDayOfDecade(decade), "BC 3.2.1")
        }

        @Test
        fun `Display the start & end of a negative decade`() {
            val decade = Decade(-6)

            assertDisplay(calendar0.getStartDayOfDecade(decade), "BC 1.1.59")
            assertDisplay(calendar0.getEndDayOfDecade(decade), "BC 3.2.50")
        }

        private fun assertDisplay(day: Day, display: String) {
            assertEquals(display, display(calendar0, day))
        }

        @Nested
        inner class GetStartDecadeTest {

            @Test
            fun `Get the start century of a millennium`() {
                assertStartDecade(Millennium(-1), -100)
                assertStartDecade(Millennium(0), 0)
                assertStartDecade(Millennium(1), 100)
            }

            @Test
            fun `Get the start decade of a century`() {
                assertStartDecade(Century(-1), -10)
                assertStartDecade(Century(0), 0)
                assertStartDecade(Century(1), 10)
            }

            @Test
            fun `Get the decade of a decade`() {
                assertStartDecade(Decade(-1), -1)
                assertStartDecade(Decade(0), 0)
                assertStartDecade(Decade(1), 1)
            }

            @Test
            fun `Get the decade of a year`() {
                assertStartDecade(Year(-1), -1)
                assertStartDecade(Year(0), 0)
                assertStartDecade(Year(5), 0)
                assertStartDecade(Year(8), 0)
                assertStartDecade(Year(9), 1)
            }

            @Test
            fun `Get the decade of a month`() {
                assertStartDecade(Month(-1), -1)
                assertStartDecade(Month(0), 0)
                assertStartDecade(Month(17), 0)
                assertStartDecade(Month(18), 1)
            }

            @Test
            fun `Get the decade of a week`() {
                assertStartDecade(Week(-1), -1)
                assertStartDecade(Week(0), 0)
                assertStartDecade(Week(22), 0)
                assertStartDecade(Week(23), 1)
            }

            @Test
            fun `Get the decade of a day`() {
                assertStartDecade(endMonth2Bc1, -1)
                assertStartDecade(startMonth1Ad1, 0)
                assertStartDecade(Day(25), 0)
                assertStartDecade(Day(44), 0)
                assertStartDecade(Day(45), 1)
            }

            private fun assertStartDecade(date: Date, result: Int) {
                val decade = Decade(result)
                val display = resolveDecade(decade)

                assertEquals(decade, calendar1.getStartDecade(date))
                assertEquals(display, calendar1.getStartDisplayDecade(date))
            }
        }
    }

    @Nested
    inner class CenturyTest {

        @Test
        fun `Display the start & end of a positive century`() {
            val century = Century(20)

            assertDisplay(calendar0.getStartDayOfCentury(century), "1.1.2000 AD")
            assertDisplay(calendar0.getEndDayOfCentury(century), "3.2.2099 AD")
        }

        @Test
        fun `The first century in AD only has 99 years`() {
            val century = Century(0)

            assertDisplay(calendar0.getStartDayOfCentury(century), "1.1.1 AD")
            assertDisplay(calendar0.getEndDayOfCentury(century), "3.2.99 AD")
        }

        @Test
        fun `The first century in BC only has 99 years`() {
            val century = Century(-1)

            assertDisplay(calendar0.getStartDayOfCentury(century), "BC 1.1.99")
            assertDisplay(calendar0.getEndDayOfCentury(century), "BC 3.2.1")
        }

        @Test
        fun `Display the start & end of a negative century`() {
            val century = Century(-6)

            assertDisplay(calendar0.getStartDayOfCentury(century), "BC 1.1.599")
            assertDisplay(calendar0.getEndDayOfCentury(century), "BC 3.2.500")
        }

        private fun assertDisplay(day: Day, display: String) {
            assertEquals(display, display(calendar0, day))
        }


        @Nested
        inner class GetCenturyTest {

            @Test
            fun `Get the start century of a millennium`() {
                assertStartCentury(Millennium(-1), -10)
                assertStartCentury(Millennium(0), 0)
                assertStartCentury(Millennium(1), 10)
            }

            @Test
            fun `Get the century of a century`() {
                assertStartCentury(Century(-1), -1)
                assertStartCentury(Century(0), 0)
                assertStartCentury(Century(1), 1)
            }

            @Test
            fun `Get the century of a decade`() {
                assertStartCentury(Decade(-1), -1)
                assertStartCentury(Decade(0), 0)
                assertStartCentury(Decade(9), 0)
                assertStartCentury(Decade(10), 1)
            }

            @Test
            fun `Get the century of a year`() {
                assertStartCentury(Year(-1), -1)
                assertStartCentury(Year(0), 0)
                assertStartCentury(Year(5), 0)
                assertStartCentury(Year(98), 0)
                assertStartCentury(Year(99), 1)
            }

            @Test
            fun `Get the century of a month`() {
                assertStartCentury(Month(-1), -1)
                assertStartCentury(Month(0), 0)
                assertStartCentury(Month(5), 0)
                assertStartCentury(Month(197), 0)
                assertStartCentury(Month(198), 1)
            }

            @Test
            fun `Get the century of a week`() {
                assertStartCentury(Week(-1), -1)
                assertStartCentury(Week(0), 0)
                assertStartCentury(Week(247), 0)
                assertStartCentury(Week(248), 1)
            }

            @Test
            fun `Get the century of a day`() {
                assertStartCentury(endMonth2Bc1, -1)
                assertStartCentury(startMonth1Ad1, 0)
                assertStartCentury(Day(25), 0)
                assertStartCentury(Day(494), 0)
                assertStartCentury(Day(495), 1)
            }

            private fun assertStartCentury(date: Date, result: Int) {
                val century = Century(result)
                val display = resolveCentury(century)

                assertEquals(century, calendar1.getCentury(date))
                assertEquals(display, calendar1.getDisplayCentury(date))
            }
        }
    }

    @Nested
    inner class MillenniumTest {

        @Test
        fun `Display the start & end of a positive millennium`() {
            val millennium = Millennium(2)

            assertDisplay(calendar0.getStartDayOfMillennium(millennium), "1.1.2000 AD")
            assertDisplay(calendar0.getEndDayOfMillennium(millennium), "3.2.2999 AD")
        }

        @Test
        fun `The first millennium in AD only has 99 years`() {
            val century = Millennium(0)

            assertDisplay(calendar0.getStartDayOfMillennium(century), "1.1.1 AD")
            assertDisplay(calendar0.getEndDayOfMillennium(century), "3.2.999 AD")
        }

        @Test
        fun `The first millennium in BC only has 99 years`() {
            val century = Millennium(-1)

            assertDisplay(calendar0.getStartDayOfMillennium(century), "BC 1.1.999")
            assertDisplay(calendar0.getEndDayOfMillennium(century), "BC 3.2.1")
        }

        @Test
        fun `Display the start & end of a negative millennium`() {
            val century = Millennium(-6)

            assertDisplay(calendar0.getStartDayOfMillennium(century), "BC 1.1.5999")
            assertDisplay(calendar0.getEndDayOfMillennium(century), "BC 3.2.5000")
        }

        private fun assertDisplay(day: Day, display: String) {
            assertEquals(display, display(calendar0, day))
        }


        @Nested
        inner class GetMillenniumTest {

            @Test
            fun `Get the millennium of a millennium`() {
                assertStartMillennium(Millennium(-1), -1)
                assertStartMillennium(Millennium(0), 0)
                assertStartMillennium(Millennium(1), 1)
            }

            @Test
            fun `Get the millennium of a century`() {
                assertStartMillennium(Century(-1), -1)
                assertStartMillennium(Century(0), 0)
                assertStartMillennium(Century(9), 0)
                assertStartMillennium(Century(10), 1)
            }

            @Test
            fun `Get the millennium of a decade`() {
                assertStartMillennium(Decade(-1), -1)
                assertStartMillennium(Decade(0), 0)
                assertStartMillennium(Decade(99), 0)
                assertStartMillennium(Decade(100), 1)
            }

            @Test
            fun `Get the millennium of a year`() {
                assertStartMillennium(Year(-1), -1)
                assertStartMillennium(Year(0), 0)
                assertStartMillennium(Year(998), 0)
                assertStartMillennium(Year(999), 1)
            }

            @Test
            fun `Get the millennium of a month`() {
                assertStartMillennium(Month(-1), -1)
                assertStartMillennium(Month(0), 0)
                assertStartMillennium(Month(5), 0)
                assertStartMillennium(Month(1997), 0)
                assertStartMillennium(Month(1998), 1)
            }

            @Test
            fun `Get the millennium of a week`() {
                assertStartMillennium(Week(-1), -1)
                assertStartMillennium(Week(0), 0)
                assertStartMillennium(Week(2497), 0)
                assertStartMillennium(Week(2498), 1)
            }

            @Test
            fun `Get the millennium of a day`() {
                assertStartMillennium(endMonth2Bc1, -1)
                assertStartMillennium(startMonth1Ad1, 0)
                assertStartMillennium(Day(25), 0)
                assertStartMillennium(Day(4994), 0)
                assertStartMillennium(Day(4995), 1)
            }

            private fun assertStartMillennium(date: Date, result: Int) {
                val century = Millennium(result)
                val display = resolveMillennium(century)

                assertEquals(century, calendar1.getMillennium(date))
                assertEquals(display, calendar1.getDisplayMillennium(date))
            }
        }
    }

    companion object {
        @JvmStatic
        fun provideStartOfYear(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Year(-2), Day(-10)),
                Arguments.of(Year(-1), Day(-5)),
                Arguments.of(Year(0), Day(0)),
                Arguments.of(Year(1), Day(5)),
                Arguments.of(Year(2), Day(10)),
            )
        }
    }
}