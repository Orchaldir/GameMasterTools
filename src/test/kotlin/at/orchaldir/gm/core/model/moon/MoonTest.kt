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
    inner class GetPhaseNameTest {

        @Test
        fun `First Circle`() {
            assertPhase(0, NewMoon)
            assertPhase(1, WaxingCrescent)
            assertPhase(2, WaxingCrescent)
            assertPhase(3, FirstQuarter)
            assertPhase(4, WaxingGibbous)
        }

        private fun assertPhase(day: Int, phase: MoonPhase) {
            assertEquals(phase, moon.getPhase(Day(day)))
        }
    }
}