package at.orchaldir.gm.core.model.moon

import at.orchaldir.gm.core.model.moon.MoonPhase.*
import at.orchaldir.gm.core.model.time.Day
import org.junit.jupiter.api.Assertions.*
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
        fun `First circle`() {
            assertNext(0, 0)
            assertNext(1, 12)
        }

        private fun assertNext(day: Int, next: Int) {
            assertEquals(Day(next), moon.getNextNewMoon(Day(day)))
        }

    }
}