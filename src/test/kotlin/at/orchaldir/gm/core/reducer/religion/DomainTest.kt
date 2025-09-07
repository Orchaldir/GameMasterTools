package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateDomain
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DomainTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Domain(DOMAIN_ID_0)),
            Storage(Spell(SPELL_ID_0)),
            Storage(Job(JOB_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateDomain(Domain(UNKNOWN_DOMAIN_ID))

            assertIllegalArgument("Requires unknown Domain 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot use an unknown job`() {
            val action = UpdateDomain(Domain(DOMAIN_ID_0, jobs = setOf(UNKNOWN_JOB_ID)))

            assertIllegalArgument("Requires unknown Job 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot use an unknown spell`() {
            val action = UpdateDomain(Domain(DOMAIN_ID_0, spells = SomeOf(UNKNOWN_SPELL_ID)))

            assertIllegalArgument("Requires unknown Spell 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a domain`() {
            val domain = Domain(
                DOMAIN_ID_0,
                NAME,
                SomeOf(SPELL_ID_0),
                setOf(JOB_ID_0),
            )
            val action = UpdateDomain(domain)

            assertEquals(domain, REDUCER.invoke(STATE, action).first.getDomainStorage().get(DOMAIN_ID_0))
        }
    }

}