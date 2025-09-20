package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.CHARACTER_TEMPLATE_ID_0
import at.orchaldir.gm.JOB_ID_0
import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.UNIFORM_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplate
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

            failCanDelete(newState, JOB_ID_0)
        }

        @Test
        fun `Cannot delete a culture used by a character template`() {
            val template = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_ID_0, uniform = UNIFORM_ID_0)
            val newState = state.updateStorage(Storage(template))

            failCanDelete(newState, CHARACTER_TEMPLATE_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(UNIFORM_ID_0).addId(blockingId),
                state.canDeleteUniform(UNIFORM_ID_0)
            )
        }
    }

}