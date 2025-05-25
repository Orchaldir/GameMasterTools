package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeletePantheon
import at.orchaldir.gm.core.action.UpdatePantheon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.BeliefStatus
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.WorshipsPantheon
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PantheonTest {

    private val pantheon0 = Pantheon(PANTHEON_ID_0)
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(God(GOD_ID_0)),
            Storage(pantheon0),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeletePantheon(PANTHEON_ID_0)

        @Test
        fun `Can delete an existing pantheon`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getPantheonStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Pantheon 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete the pantheon that a character believes in`() {
            val beliefStatus = History<BeliefStatus>(WorshipsPantheon(PANTHEON_ID_0))
            val character = Character(CHARACTER_ID_0, beliefStatus = beliefStatus)
            val newState = state.updateStorage(Storage(character))

            assertIllegalArgument("Cannot delete Pantheon 0, because it is used!") { REDUCER.invoke(newState, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdatePantheon(Pantheon(UNKNOWN_PANTHEON_ID))

            assertIllegalArgument("Requires unknown Pantheon 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an unknown god`() {
            val action = UpdatePantheon(Pantheon(PANTHEON_ID_0, gods = setOf(UNKNOWN_GOD_ID)))

            assertIllegalArgument("Requires unknown God 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Update a pantheon`() {
            val pantheon = Pantheon(
                PANTHEON_ID_0,
                gods = setOf(GOD_ID_0),
            )
            val action = UpdatePantheon(pantheon)

            assertEquals(pantheon, REDUCER.invoke(state, action).first.getPantheonStorage().get(PANTHEON_ID_0))
        }
    }

}