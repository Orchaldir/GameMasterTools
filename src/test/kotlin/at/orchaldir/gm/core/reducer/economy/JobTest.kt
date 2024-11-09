package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteJob
import at.orchaldir.gm.core.action.UpdateJob
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.MonthDefinition
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JOB
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = JobId(0)
private val BUSINESS0 = BusinessId(0)
private val BUILDING0 = BuildingId(0)
private val CHARACTER0 = CharacterId(0)

class JobTest {

    @Nested
    inner class DeleteTest {
        val action = DeleteJob(ID0)

        @Test
        fun `Can delete an existing job`() {
            val state = State(Storage(Job(ID0)))

            assertEquals(0, REDUCER.invoke(state, action).first.getJobStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Job 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a job used by a character`() {
            val state = State(
                listOf(
                    Storage(Character(CHARACTER0, employmentStatus = History(Employed(BUSINESS0, ID0)))),
                    Storage(Job(ID0)),
                )
            )

            assertIllegalArgument("Cannot delete job 0, because it is used by a character!") {
                REDUCER.invoke(state, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        private val CALENDAR = Calendar(CalendarId(0), months = listOf(MonthDefinition("a")))
        private val STATE = State(
            listOf(
                Storage(Job(ID0)),
                Storage(CALENDAR),
                Storage(Character(CHARACTER0)),
            )
        )

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateJob(Job(ID0))
            val state = STATE.removeStorage(JOB)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Success`() {
            val job = Job(ID0, "Test")
            val action = UpdateJob(job)

            assertEquals(job, REDUCER.invoke(STATE, action).first.getJobStorage().get(ID0))
        }
    }

}