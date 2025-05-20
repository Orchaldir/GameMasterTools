package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StateTest {

    @Nested
    inner class GetElementNameTest {
        val id = CalendarId(0)
        val state = State(Storage(listOf(Calendar(id, Name.init("Gregorian")))))

        @Test
        fun `Get name of valid element`() {
            assertEquals("Gregorian", state.getElementName(id))
        }

        @Test
        fun `Get name of unknown element`() {
            assertEquals("Unknown", state.getElementName(CalendarId(1)))
        }

        @Test
        fun `Get name of element of unavailable type`() {
            assertEquals("Unknown", state.getElementName(LanguageId(1)))
        }
    }

}