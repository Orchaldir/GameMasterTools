package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteCalendar
import at.orchaldir.gm.core.action.UpdateCalendar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CalendarId(0)
private val ID1 = CalendarId(1)
private val CULTURE0 = CultureId(1)

class CalendarTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing calendar`() {
            val state = State(calendars = Storage(listOf(Calendar(ID0))))
            val action = DeleteCalendar(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.calendars.getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteCalendar(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a calendar with children`() {
            val state = State(calendars = Storage(listOf(Calendar(ID0), Calendar(ID1, origin = ImprovedCalendar(ID0)))))
            val action = DeleteCalendar(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a calendar used by a culture`() {
            val culture = Culture(CULTURE0, calendar = ID0)
            val state = State(cultures = Storage(listOf(culture)), calendars = Storage(listOf(Calendar(ID0))))
            val action = DeleteCalendar(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateCalendar(Calendar(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Parent calendar must exist`() {
            val state = State(calendars = Storage(listOf(Calendar(ID0))))
            val action = UpdateCalendar(Calendar(ID0, origin = ImprovedCalendar(ID1)))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A calendar cannot be its own parent`() {
            val state = State(calendars = Storage(listOf(Calendar(ID0))))
            val action = UpdateCalendar(Calendar(ID0, origin = ImprovedCalendar(ID0)))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Parent calendar exist`() {
            val state = State(calendars = Storage(listOf(Calendar(ID0), Calendar(ID1))))
            val calendar = Calendar(ID0, origin = ImprovedCalendar(ID1))
            val action = UpdateCalendar(calendar)

            assertEquals(calendar, REDUCER.invoke(state, action).first.calendars.get(ID0))
        }
    }

}