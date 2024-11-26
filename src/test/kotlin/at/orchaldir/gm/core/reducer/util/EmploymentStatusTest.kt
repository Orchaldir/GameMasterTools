package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test

private val EMPLOYED = Employed(BUSINESS_ID_0, JOB_ID_0)
private val STATE = State(
    listOf(
        Storage(CALENDAR0),
        Storage(Business(BUSINESS_ID_0)),
        Storage(Job(JOB_ID_0)),
    )
)

class EmploymentStatusTest {

    @Test
    fun `Cannot use unknown business`() {
        val state = STATE.removeStorage(BUSINESS_ID_0)

        assertIllegalArgument("The employment's business doesn't exist!") {
            checkEmploymentStatusHistory(state, History(EMPLOYED), DAY0)
        }
    }

    @Test
    fun `Cannot use unknown job`() {
        val state = STATE.removeStorage(JOB_ID_0)

        assertIllegalArgument("The employment's job doesn't exist!") {
            checkEmploymentStatusHistory(state, History(EMPLOYED), DAY0)
        }
    }

    @Test
    fun `Character has a valid job`() {
        checkEmploymentStatusHistory(STATE, History(EMPLOYED), DAY0)
    }

    @Test
    fun `Character employed by a business before its founding`() {
        val state = STATE.updateStorage(Storage(Business(BUSINESS_ID_0, startDate = DAY1)))

        assertIllegalArgument("The employment's business is not in operation!") {
            checkEmploymentStatusHistory(state, History(EMPLOYED), DAY0)
        }
    }

}