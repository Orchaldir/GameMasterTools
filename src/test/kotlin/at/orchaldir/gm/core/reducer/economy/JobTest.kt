package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteJob
import at.orchaldir.gm.core.action.UpdateJob
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val STATE = State(
    listOf(
        Storage(CALENDAR0),
        Storage(Business(BUSINESS_ID_0)),
        Storage(Character(CHARACTER_ID_0)),
        Storage(Job(JOB_ID_0)),
    )
)

class JobTest {

    @Nested
    inner class DeleteTest {
        val action = DeleteJob(JOB_ID_0)

        @Test
        fun `Can delete an existing job`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getJobStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Job 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a job used by a character`() {
            val employmentStatus = History<EmploymentStatus>(Employed(BUSINESS_ID_0, JOB_ID_0))
            val state = STATE.updateStorage(Storage(Character(CHARACTER_ID_0, employmentStatus = employmentStatus)))

            assertIllegalArgument("Cannot delete job 0, because it is used by a character!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a job previously used by a character`() {
            val employmentStatus = History(Unemployed, HistoryEntry(Employed(BUSINESS_ID_0, JOB_ID_0), DAY0))
            val state = STATE.updateStorage(Storage(Character(CHARACTER_ID_0, employmentStatus = employmentStatus)))

            assertIllegalArgument("Cannot delete job 0, because it is the former job of a character!") {
                REDUCER.invoke(state, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateJob(Job(JOB_ID_0))
            val state = STATE.removeStorage(JOB_ID_0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Success`() {
            val job = Job(JOB_ID_0, "Test")
            val action = UpdateJob(job)

            assertEquals(job, REDUCER.invoke(STATE, action).first.getJobStorage().get(JOB_ID_0))
        }
    }

}