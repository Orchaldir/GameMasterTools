package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.LEGAL_CODE_ID_0
import at.orchaldir.gm.REALM_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.LegalCode
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LegalCodeTest {

    @Nested
    inner class CanDeleteTest {
        private val code = LegalCode(LEGAL_CODE_ID_0)
        private val state = State(
            listOf(
                Storage(code),
            )
        )

        @Test
        fun `Cannot delete an element used as a position`() {
            val realm = Realm(REALM_ID_0, legalCode = History(LEGAL_CODE_ID_0))
            val newState = state.updateStorage(realm)

            failCanDelete(newState, REALM_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(LEGAL_CODE_ID_0).addId(blockingId), state.canDeleteLegalCode(LEGAL_CODE_ID_0))
        }
    }

}