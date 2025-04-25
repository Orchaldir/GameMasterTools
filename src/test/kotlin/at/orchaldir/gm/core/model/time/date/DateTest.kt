package at.orchaldir.gm.core.model.time.date

import at.orchaldir.gm.CALENDAR0
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val DAY0 = Day(-10)
private val DAY1 = Day(10)
private val DAY2 = Day(15)

class DateTest {

    @Nested
    inner class IsBetweenTest {

        @Test
        fun `Decade VS year`() {
            val decade = Decade(190)
            val year = Year(1907)


            assertTrue(decade.isBetween(CALENDAR0, year))
        }

    }
}