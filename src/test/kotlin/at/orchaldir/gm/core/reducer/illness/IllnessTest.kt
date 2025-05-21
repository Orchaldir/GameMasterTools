package at.orchaldir.gm.core.reducer.illness

import at.orchaldir.gm.CALENDAR0
import at.orchaldir.gm.ILLNESS_ID_0
import at.orchaldir.gm.REALM_ID_0
import at.orchaldir.gm.UNKNOWN_CHARACTER_ID
import at.orchaldir.gm.UNKNOWN_ILLNESS_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteIllness
import at.orchaldir.gm.core.action.UpdateIllness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.illness.Illness
import at.orchaldir.gm.core.model.IllnessId
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.CreatedOrigin
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IllnessTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Realm(REALM_ID_0)),
            Storage(Illness(ILLNESS_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {

        private val action = DeleteIllness(ILLNESS_ID_0)

        @Test
        fun `Can delete an existing Illness`() {
            val state = State(Storage(Illness(ILLNESS_ID_0)))

            assertEquals(0, REDUCER.invoke(state, action).first.getIllnessStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateIllness(Illness(UNKNOWN_ILLNESS_ID))

            assertIllegalArgument("Requires unknown Illness 99!") { REDUCER.invoke(State(), action) }
        }

        // See OriginTest for more
        @Test
        fun `Test origin`() {
            val origin = CreatedOrigin<IllnessId>(CreatedByCharacter(UNKNOWN_CHARACTER_ID))
            val action = UpdateIllness(Illness(ILLNESS_ID_0, origin = origin))

            assertIllegalArgument("Cannot use an unknown Character 99 as Creator!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update is valid`() {
            val battle = Illness(ILLNESS_ID_0, Name.Companion.init("Test"))
            val action = UpdateIllness(battle)

            assertEquals(battle, REDUCER.invoke(STATE, action).first.getIllnessStorage().get(ILLNESS_ID_0))
        }
    }

}