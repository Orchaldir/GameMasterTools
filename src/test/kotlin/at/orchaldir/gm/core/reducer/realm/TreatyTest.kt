package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Treaty
import at.orchaldir.gm.core.model.realm.TreatyParticipant
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TreatyTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Realm(REALM_ID_0)),
            Storage(Treaty(TREATY_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(Treaty(TREATY_ID_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `A participating realm must exist`() {
            val participant = TreatyParticipant(UNKNOWN_REALM_ID)
            val action = UpdateAction(Treaty(TREATY_ID_0, participants = listOf(participant)))

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `A character signing the treaty must exist`() {
            val participant = TreatyParticipant(REALM_ID_0, UNKNOWN_CHARACTER_ID)
            val action = UpdateAction(Treaty(TREATY_ID_0, participants = listOf(participant)))

            assertIllegalArgument("Requires unknown Character 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update is valid`() {
            val treaty = Treaty(TREATY_ID_0, Name.init("Test"))
            val action = UpdateAction(treaty)

            assertEquals(treaty, REDUCER.invoke(STATE, action).first.getTreatyStorage().get(TREATY_ID_0))
        }
    }

}