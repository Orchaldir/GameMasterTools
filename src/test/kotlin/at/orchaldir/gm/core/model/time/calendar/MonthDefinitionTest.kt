package at.orchaldir.gm.core.model.time.calendar

import at.orchaldir.gm.NAME0
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MonthDefinitionTest {

    @Test
    fun `Test is inside`() {
        val month = MonthDefinition(NAME0, 2)

        assertFalse(month.isInside(-1))
        assertTrue(month.isInside(0))
        assertTrue(month.isInside(1))
        assertFalse(month.isInside(2))
    }

}