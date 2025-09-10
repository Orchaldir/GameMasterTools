package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.JOB_ID_0
import at.orchaldir.gm.TEXT_ID_1
import at.orchaldir.gm.UNIFORM_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UniformTest {

    @Nested
    inner class CanDeleteTest {
        private val uniform = Uniform(UNIFORM_ID_0)
        private val state = State(
            listOf(
                Storage(uniform),
            )
        )

        @Test
        fun `Cannot delete a uniform used by a job`() {
            val job = Job(JOB_ID_0, uniforms = GenderMap(UNIFORM_ID_0))
            val newState = state.updateStorage(Storage(job))

            assertCanDelete(newState, JOB_ID_0)
        }

        private fun <ID : Id<ID>> assertCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(UNIFORM_ID_0).addId(blockingId),
                state.canDeleteUniform(UNIFORM_ID_0)
            )
        }
    }

}