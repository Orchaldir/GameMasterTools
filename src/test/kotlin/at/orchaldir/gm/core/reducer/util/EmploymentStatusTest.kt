package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test

class EmploymentStatusTest {

    private val employed = Employed(BUSINESS_ID_0, JOB_ID_0)
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Business(BUSINESS_ID_0)),
            Storage(Job(JOB_ID_0)),
        )
    )

    @Test
    fun `Cannot use unknown business`() {
        val newState = state.removeStorage(BUSINESS_ID_0)

        assertIllegalArgument("The employment's business doesn't exist!") {
            checkEmploymentStatusHistory(newState, History(employed), DAY0)
        }
    }

    @Test
    fun `Cannot use unknown job`() {
        val newState = state.removeStorage(JOB_ID_0)

        assertIllegalArgument("The employment's job doesn't exist!") {
            checkEmploymentStatusHistory(newState, History(employed), DAY0)
        }
    }

    @Test
    fun `Character has a valid job`() {
        checkEmploymentStatusHistory(state, History(employed), DAY0)
    }

    @Test
    fun `Character employed by a business before its founding`() {
        val newState = state.updateStorage(Storage(Business(BUSINESS_ID_0, startDate = DAY1)))

        assertIllegalArgument("The employment's business is not in operation!") {
            checkEmploymentStatusHistory(newState, History(employed), DAY0)
        }
    }

}