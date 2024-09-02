package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteHoliday
import at.orchaldir.gm.core.action.UpdateHoliday
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = HolidayId(0)

class HolidayTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing holiday`() {
            val state = State(Storage(listOf(Holiday(ID0))))
            val action = DeleteHoliday(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getHolidayStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteHoliday(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateHoliday(Holiday(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(listOf(Holiday(ID0))))
            val holiday = Holiday(ID0, "Test")
            val action = UpdateHoliday(holiday)

            assertEquals(holiday, REDUCER.invoke(state, action).first.getHolidayStorage().get(ID0))
        }
    }

}