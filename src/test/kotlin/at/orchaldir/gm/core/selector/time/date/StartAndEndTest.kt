package at.orchaldir.gm.core.selector.time.date

import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.calendar.ComplexMonths
import at.orchaldir.gm.core.model.time.calendar.MonthDefinition
import at.orchaldir.gm.core.model.time.date.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class StartAndEndTest {

    private val month0 = MonthDefinition("a", 2)
    private val month1 = MonthDefinition("b", 3)
    private val calendar0 = Calendar(CalendarId(0), months = ComplexMonths(listOf(month0, month1)))

    @Nested
    inner class MonthTest {

        @Nested
        inner class GetStartOfMonthTest {

            @Test
            fun `Get start of the second month in 1 AD`() {
                assertEquals(Day(2), calendar0.getStartOfMonth(Day(2)))
                assertEquals(Day(2), calendar0.getStartOfMonth(Day(3)))
                assertEquals(Day(2), calendar0.getStartOfMonth(Day(4)))
            }

            @Test
            fun `Get start of the second month in BC 1`() {
                assertEquals(Day(-3), calendar0.getStartOfMonth(Day(-3)))
                assertEquals(Day(-3), calendar0.getStartOfMonth(Day(-2)))
                assertEquals(Day(-3), calendar0.getStartOfMonth(Day(-1)))
            }

            @Test
            fun `Get start of the first month in the second year`() {
                assertEquals(Day(5), calendar0.getStartOfMonth(Day(5)))
                assertEquals(Day(5), calendar0.getStartOfMonth(Day(6)))
            }
        }

        @Nested
        inner class GetEndOfMonthTest {

            @Test
            fun `Get end of the first month in 1 AD`() {
                assertEquals(Day(1), calendar0.getEndOfMonth(Day(0)))
                assertEquals(Day(1), calendar0.getEndOfMonth(Day(1)))
            }

            @Test
            fun `Get end of the second month in BC 1`() {
                assertEquals(Day(-1), calendar0.getEndOfMonth(Day(-3)))
                assertEquals(Day(-1), calendar0.getEndOfMonth(Day(-2)))
                assertEquals(Day(-1), calendar0.getEndOfMonth(Day(-1)))
            }
        }

        @Nested
        inner class GetStartOfNextMonthTest {
            @Test
            fun `Get start of next month in first era`() {
                assertEquals(Day(-3), calendar0.getStartOfNextMonth(Day(-5)))
                assertEquals(Day(-3), calendar0.getStartOfNextMonth(Day(-4)))
            }

            @Test
            fun `Get start of next month across era`() {
                assertEquals(Day(0), calendar0.getStartOfNextMonth(Day(-3)))
                assertEquals(Day(0), calendar0.getStartOfNextMonth(Day(-2)))
                assertEquals(Day(0), calendar0.getStartOfNextMonth(Day(-1)))
            }

            @Test
            fun `Get start of next month`() {
                assertEquals(Day(2), calendar0.getStartOfNextMonth(Day(0)))
                assertEquals(Day(2), calendar0.getStartOfNextMonth(Day(1)))
            }

            @Test
            fun `In the next year`() {
                assertEquals(Day(5), calendar0.getStartOfNextMonth(Day(2)))
                assertEquals(Day(5), calendar0.getStartOfNextMonth(Day(3)))
                assertEquals(Day(5), calendar0.getStartOfNextMonth(Day(4)))
            }
        }

        @Nested
        inner class GetStartOfPreviousMonthTest {
            @Test
            fun `Get start of previous month in first era`() {
                assertEquals(Day(-5), calendar0.getStartOfPreviousMonth(Day(-3)))
                assertEquals(Day(-5), calendar0.getStartOfPreviousMonth(Day(-2)))
                assertEquals(Day(-5), calendar0.getStartOfPreviousMonth(Day(-1)))
            }

            @Test
            fun `Get start of previous month`() {
                assertEquals(Day(-3), calendar0.getStartOfPreviousMonth(Day(0)))
                assertEquals(Day(-3), calendar0.getStartOfPreviousMonth(Day(1)))
            }

            @Test
            fun `In the next year`() {
                assertEquals(Day(0), calendar0.getStartOfPreviousMonth(Day(2)))
                assertEquals(Day(0), calendar0.getStartOfPreviousMonth(Day(3)))
                assertEquals(Day(0), calendar0.getStartOfPreviousMonth(Day(4)))
            }
        }
    }

    @Nested
    inner class YearTest {

        @ParameterizedTest
        @MethodSource("at.orchaldir.gm.core.selector.time.date.StartAndEndTest#provideStartOfYear")
        fun `Get the start of a year`(year: Year, day: Day) {
            assertEquals(day, calendar0.getStartDayOfYear(year))
        }

        @Test
        fun `Get the end of a year`() {
            assertEquals(Day(-1), calendar0.getEndDayOfYear(Year(-1)))
            assertEquals(Day(4), calendar0.getEndDayOfYear(Year(0)))
            assertEquals(Day(9), calendar0.getEndDayOfYear(Year(1)))
            assertEquals(Day(14), calendar0.getEndDayOfYear(Year(2)))
        }

        @Nested
        inner class GetStartYearTest {

            @Test
            fun `Get the year of a day`() {
                assertStartYear(Day(-10), -2)
                assertStartYear(Day(-5), -1)
                assertStartYear(Day(0), 0)
                assertStartYear(Day(5), 1)
                assertStartYear(Day(10), 2)
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

            private fun assertStartYear(input: Date, year: Int) {
                val year = Year(year)
                val display = calendar0.resolveYear(year)

                assertEquals(year, calendar0.getStartYear(input))
                assertEquals(display, calendar0.getStartDisplayYear(input))
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
            fun `Get the decade of a day`() {
                assertStartDecade(Day(-1), -1)
                assertStartDecade(Day(0), 0)
                assertStartDecade(Day(25), 0)
                assertStartDecade(Day(44), 0)
                assertStartDecade(Day(45), 1)
            }

            private fun assertStartDecade(date: Date, result: Int) {
                val decade = Decade(result)
                val display = calendar0.resolveDecade(decade)

                assertEquals(decade, calendar0.getStartDecade(date))
                assertEquals(display, calendar0.getStartDisplayDecade(date))
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
            fun `Get the century of a day`() {
                assertStartCentury(Day(-1), -1)
                assertStartCentury(Day(0), 0)
                assertStartCentury(Day(25), 0)
                assertStartCentury(Day(494), 0)
                assertStartCentury(Day(495), 1)
            }

            private fun assertStartCentury(date: Date, result: Int) {
                val century = Century(result)
                val display = calendar0.resolveCentury(century)

                assertEquals(century, calendar0.getCentury(date))
                assertEquals(display, calendar0.getDisplayCentury(date))
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