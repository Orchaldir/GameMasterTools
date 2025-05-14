package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.quote.Quote
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HasStartDateTest {

    @Nested
    inner class ExistsTest {
        val state = State(
            Storage(CALENDAR0),
            data = Data(time = Time(CALENDAR_ID_0)),
        )
        val elementWithNull = Quote(QUOTE_ID_0)
        val elementWithStartDay = Quote(QUOTE_ID_0, date = DAY1)

        @Test
        fun `Exists element without date at specific day`() {
            assertTrue(state.exists(elementWithNull, DAY0))
            assertTrue(state.exists(elementWithNull, DAY1))
            assertTrue(state.exists(elementWithNull, DAY2))
        }

        @Test
        fun `Exists element without date at unknown day`() {
            assertTrue(state.exists(elementWithNull, null))
        }

        @Test
        fun `Exists element with specific day at specific day`() {
            assertFalse(state.exists(elementWithStartDay, DAY0))
            assertTrue(state.exists(elementWithStartDay, DAY1))
            assertTrue(state.exists(elementWithStartDay, DAY2))
        }

        @Test
        fun `Exists element with specific day at unknown day`() {
            assertTrue(state.exists(elementWithStartDay, null))
        }
    }
}