package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteRealm
import at.orchaldir.gm.core.action.UpdateRealm
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RealmTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Realm(REALM_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteRealm(REALM_ID_0)

        @Test
        fun `Can delete an existing realm`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getRealmStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteRealm(UNKNOWN_REALM_ID)

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRealm(Realm(UNKNOWN_REALM_ID))

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Founder must exist`() {
            val realm = Realm(REALM_ID_0, founder = CreatedByCharacter(UNKNOWN_CHARACTER_ID))
            val action = UpdateRealm(realm)

            assertIllegalArgument("Cannot use an unknown character 99 as founder!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a realm`() {
            val realm = Realm(REALM_ID_0, NAME)
            val action = UpdateRealm(realm)

            assertEquals(realm, REDUCER.invoke(STATE, action).first.getRealmStorage().get(REALM_ID_0))
        }
    }

}