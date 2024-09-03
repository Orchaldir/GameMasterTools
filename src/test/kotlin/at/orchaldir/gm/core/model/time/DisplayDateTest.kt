package at.orchaldir.gm.core.model.time

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class DisplayDateTest {
    @Test
    fun `Compare 2 days`() {
        assertEquals(DisplayDay(1, 2, 3, 0), DisplayDay(1, 2, 3, 4).getStartOfMonth())
    }
}