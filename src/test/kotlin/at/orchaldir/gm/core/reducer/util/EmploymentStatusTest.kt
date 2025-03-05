package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test

class EmploymentStatusTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Business(BUSINESS_ID_0)),
            Storage(Job(JOB_ID_0)),
        )
    )

    @Test
    fun `Cannot use unknown business`() {
        assertIllegalArgument("The employment's business doesn't exist!") {
            checkEmploymentStatusHistory(state, History(Employed(UNKNOWN_BUSINESS_ID, JOB_ID_0)), DAY0)
        }
    }

    @Test
    fun `Cannot use unknown job`() {
        assertIllegalArgument("The employment's job doesn't exist!") {
            checkEmploymentStatusHistory(state, History(Employed(BUSINESS_ID_0, UNKNOWN_JOB_ID)), DAY0)
        }
    }

    @Test
    fun `Character has a valid job`() {
        checkEmploymentStatusHistory(state, History(Employed(BUSINESS_ID_0, JOB_ID_0)), DAY0)
    }

    @Test
    fun `Character employed by a business before its founding`() {
        val newState = state.updateStorage(Storage(Business(BUSINESS_ID_0, startDate = DAY1)))

        assertIllegalArgument("The employment's business is not in operation!") {
            checkEmploymentStatusHistory(newState, History(Employed(BUSINESS_ID_0, JOB_ID_0)), DAY0)
        }
    }

}