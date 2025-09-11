package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.util.BeliefStatus
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.WorshipOfGod
import at.orchaldir.gm.core.model.util.WorshipOfPantheon
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PantheonTest {

    @Nested
    inner class CanDeleteTest {
        private val pantheon = Pantheon(PANTHEON_ID_0)
        private val state = State(
            listOf(
                Storage(pantheon),
            )
        )

        @Test
        fun `Cannot delete a pantheon that a character believes in`() {
            val beliefStatus = History<BeliefStatus>(WorshipOfPantheon(PANTHEON_ID_0))
            val character = Character(CHARACTER_ID_0, beliefStatus = beliefStatus)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(PANTHEON_ID_0).addId(blockingId), state.canDeletePantheon(PANTHEON_ID_0))
        }
    }

}