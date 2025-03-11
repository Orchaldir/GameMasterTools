package at.orchaldir.gm.core.model.world.moon

import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.world.moon.MoonPhase.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MoonTest {
    val moon = Moon(MoonId(0), daysPerQuarter = 3)

    @Test
    fun `Get full cycle`() {
        assertEquals(12, moon.getCycle())
    }

    @Nested
    inner class GetPhaseTest {

        @Test
        fun `Negative days`() {
            assertCircle(-12)
        }

        @Test
        fun `First Circle`() {
            assertCircle(0)
        }

        @Test
        fun `Second Circle`() {
            assertCircle(12)
        }

        private fun assertCircle(start: Int) {
            assertPhase(start + 0, NewMoon)
            assertPhase(start + 1, WaxingCrescent)
            assertPhase(start + 2, WaxingCrescent)
            assertPhase(start + 3, FirstQuarter)
            assertPhase(start + 4, WaxingGibbous)
            assertPhase(start + 5, WaxingGibbous)
            assertPhase(start + 6, FullMoon)
            assertPhase(start + 7, WaningGibbous)
            assertPhase(start + 8, WaningGibbous)
            assertPhase(start + 9, LastQuarter)
            assertPhase(start + 10, WaningCrescent)
            assertPhase(start + 11, WaningCrescent)
        }

        private fun assertPhase(day: Int, phase: MoonPhase) {
            assertEquals(phase, moon.getPhase(Day(day)))
        }
    }

    @Nested
    inner class GetNextNewMoonTest {

        @Test
        fun `New moon on a negative day`() {
            assertNext(-12, -12)
        }

        @Test
        fun `Any other negative day`() {
            (-11..-1).forEach {
                assertNext(it, 0)
            }
        }

        @Test
        fun `Today is new moon`() {
            assertNext(0, 0)
            assertNext(12, 12)
        }

        @Test
        fun `Any other day`() {
            (1..11).forEach {
                assertNext(it, 12)
            }
        }

        private fun assertNext(day: Int, next: Int) {
            assertEquals(Day(next), moon.getNextNewMoon(Day(day)))
        }
    }

    @Nested
    inner class GetNextFullMoonTest {

        @Test
        fun `New moon on a negative day`() {
            assertNext(-6, -6)
        }

        @Test
        fun `Today is new moon`() {
            assertNext(6, 6)
            assertNext(18, 18)
        }

        @Test
        fun `Any other day`() {
            (-5..5).forEach {
                assertNext(it, 6)
            }
        }

        @Test
        fun `Second Full moon`() {
            (7..17).forEach {
                assertNext(it, 18)
            }
        }

        private fun assertNext(day: Int, next: Int) {
            assertEquals(Day(next), moon.getNextFullMoon(Day(day)))
        }
    }
}