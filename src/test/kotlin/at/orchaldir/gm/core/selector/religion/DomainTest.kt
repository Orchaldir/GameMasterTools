package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.DOMAIN_ID_0
import at.orchaldir.gm.GOD_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DomainTest {

    @Nested
    inner class CanDeleteTest {
        private val domain = Domain(DOMAIN_ID_0)
        private val state = State(
            listOf(
                Storage(domain),
            )
        )

        @Test
        fun `Cannot delete a domain used by a god`() {
            val god = God(GOD_ID_0, domains = setOf(DOMAIN_ID_0))
            val newState = state.updateStorage(Storage(god))

            failCanDelete(newState, GOD_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(DOMAIN_ID_0).addId(blockingId), state.canDeleteDomain(DOMAIN_ID_0))
        }
    }

}