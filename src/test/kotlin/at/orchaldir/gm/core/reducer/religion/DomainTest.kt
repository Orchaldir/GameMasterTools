package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteDomain
import at.orchaldir.gm.core.action.UpdateDomain
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.God
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
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteDomain(DOMAIN_ID_0)

        @Test
        fun `Can delete an existing domain`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getDomainStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteDomain(UNKNOWN_DOMAIN_ID)

            assertIllegalArgument("Requires unknown Domain 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot delete a domain used by a god`() {
            val state = STATE.updateStorage(Storage(God(GOD_ID_0, domains = setOf(DOMAIN_ID_0))))

            assertIllegalArgument("The domain 0 is used!") { REDUCER.invoke(state, action) }
        }
    }

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
            val domain = Domain(DOMAIN_ID_0, NAME)
            val action = UpdateDomain(domain)

            assertEquals(domain, REDUCER.invoke(STATE, action).first.getDomainStorage().get(DOMAIN_ID_0))
        }
    }

}