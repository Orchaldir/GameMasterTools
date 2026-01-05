package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JobTest {

    @Nested
    inner class CanDeleteTest {
        private val job = Job(JOB_ID_0)
        private val state = State(
            listOf(
                Storage(job),
            )
        )

        @Test
        fun `Cannot delete a job used by a character`() {
            val employmentStatus = History<EmploymentStatus>(Employed(BUSINESS_ID_0, JOB_ID_0))
            val character = Character(CHARACTER_ID_0, employmentStatus = employmentStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a job previously used by a character`() {
            val entry = HistoryEntry<EmploymentStatus>(Employed(BUSINESS_ID_0, JOB_ID_0), DAY0)
            val character = Character(CHARACTER_ID_0, employmentStatus = History(Unemployed, entry))
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a job associated with a domain`() {
            val character = Domain(DOMAIN_ID_0, jobs = setOf(JOB_ID_0))
            val newState = state.updateStorage(character)

            failCanDelete(newState, DOMAIN_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(JOB_ID_0).addId(blockingId),
                state.canDeleteJob(JOB_ID_0)
            )
        }
    }

}