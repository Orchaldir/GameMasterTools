package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteJob
import at.orchaldir.gm.core.action.UpdateJob
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.Economy
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.standard.StandardOfLiving
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JobTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Business(BUSINESS_ID_0)),
            Storage(Character(CHARACTER_ID_0)),
            Storage(Job(JOB_ID_0)),
            Storage(Spell(SPELL_ID_0)),
            Storage(Uniform(UNIFORM_ID_0)),
        ),
        data = Data(Economy(standardsOfLiving = listOf(StandardOfLiving(STANDARD_ID_0)))),
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateJob(Job(UNKNOWN_JOB_ID))

            assertIllegalArgument("Requires unknown Job 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update job with unknown uniform`() {
            val action = UpdateJob(Job(JOB_ID_0, uniforms = GenderMap(UNKNOWN_UNIFORM_ID)))

            assertIllegalArgument("Requires unknown Uniform 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update job with unknown spell`() {
            val action = UpdateJob(Job(JOB_ID_0, spells = SomeOf(UNKNOWN_SPELL_ID)))

            assertIllegalArgument("Requires unknown Spell 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update job with unknown standard of living`() {
            val action = UpdateJob(Job(JOB_ID_0, income = AffordableStandardOfLiving(UNKNOWN_STANDARD_ID)))

            assertIllegalArgument("Requires unknown Standard Of Living 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Success`() {
            val job = Job(
                JOB_ID_0,
                income = AffordableStandardOfLiving(STANDARD_ID_0),
                uniforms = GenderMap(UNIFORM_ID_0),
                spells = SomeOf(SPELL_ID_0),
            )
            val action = UpdateJob(job)

            assertEquals(job, REDUCER.invoke(STATE, action).first.getJobStorage().get(JOB_ID_0))
        }
    }

}