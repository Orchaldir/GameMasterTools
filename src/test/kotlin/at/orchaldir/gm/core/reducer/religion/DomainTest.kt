package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.CALENDAR0
import at.orchaldir.gm.DOMAIN_ID_0
import at.orchaldir.gm.GOD_ID_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteDomain
import at.orchaldir.gm.core.action.UpdateDomain
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val domain0 = Domain(DOMAIN_ID_0)
private val STATE = State(
    listOf(
        Storage(CALENDAR0),
        Storage(domain0),
        Storage(domain0),
    )
)

class DomainTest {

    @Nested
    inner class DeleteTest {
        val action = DeleteDomain(DOMAIN_ID_0)

        @Test
        fun `Can delete an existing domain`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getDomainStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Domain 0!") { REDUCER.invoke(State(), action) }
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
            val action = UpdateDomain(Domain(DOMAIN_ID_0))
            val state = STATE.removeStorage(DOMAIN_ID_0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot modify an unknown domain`() {
            val domain = Domain(DOMAIN_ID_0)
            val action = UpdateDomain(domain)

            assertIllegalArgument("Requires unknown Domain 0!") {
                REDUCER.invoke(State(), action)
            }
        }

        @Test
        fun `Update a domain`() {
            val domain = Domain(DOMAIN_ID_0, "Test")
            val action = UpdateDomain(domain)

            assertEquals(domain, REDUCER.invoke(STATE, action).first.getDomainStorage().get(DOMAIN_ID_0))
        }
    }

}