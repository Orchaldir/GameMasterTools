package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.JOB_ID_0
import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.Statistic
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StatisticTest {

    @Nested
    inner class CanDeleteTest {
        private val statistic = Statistic(STATISTIC_ID_0)
        private val state = State(
            listOf(
                Storage(statistic),
            )
        )

        @Test
        fun `Cannot delete a statistic used by a job`() {
            val element = Job(JOB_ID_0, importantStatistics = setOf(STATISTIC_ID_0))
            val newState = state.updateStorage(Storage(element))

            failCanDelete(newState, JOB_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(STATISTIC_ID_0).addId(blockingId), state.canDeleteStatistic(STATISTIC_ID_0))
        }
    }

}