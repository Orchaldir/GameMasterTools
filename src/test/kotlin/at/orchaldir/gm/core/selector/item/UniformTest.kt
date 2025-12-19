package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.UseUniform
import at.orchaldir.gm.core.model.character.ModifyUniform
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.equipment.EquipmentMapUpdate
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
        fun `Cannot delete a uniform used by a character`() {
            val equipped = UseUniform(UNIFORM_ID_0)
            val template = Character(CHARACTER_ID_0, equipped = equipped)
            val newState = state.updateStorage(Storage(template))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a uniform used by a character template`() {
            val equipped = UseUniform(UNIFORM_ID_0)
            val template = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_ID_0, equipped = equipped)
            val newState = state.updateStorage(Storage(template))

            failCanDelete(newState, CHARACTER_TEMPLATE_ID_0)
        }

        @Test
        fun `Cannot delete a uniform another uniform is based on`() {
            val equipped = ModifyUniform(UNIFORM_ID_0, EquipmentMapUpdate())
            val uniform1 = Uniform(UNIFORM_ID_1, equipped = equipped)
            val newState = state.updateStorage(Storage(listOf(uniform, uniform1)))

            failCanDelete(newState, UNIFORM_ID_1)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(UNIFORM_ID_0).addId(blockingId),
                state.canDeleteUniform(UNIFORM_ID_0)
            )
        }
    }

}