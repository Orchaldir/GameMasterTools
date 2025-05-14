package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteRealm
import at.orchaldir.gm.core.action.UpdateRealm
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.CreatedByRealm
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.OwnedByRealm
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.model.world.building.Building
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

        // see CreatorTest for other elements
        @Test
        fun `Cannot delete a realm that created another element`() {
            val building = Building(BUILDING_ID_0, builder = CreatedByRealm(REALM_ID_0))
            val newState = STATE.updateStorage(Storage(building))

            assertIllegalArgument("Cannot delete Realm 0, because of created elements (Building)!") {
                REDUCER.invoke(newState, action)
            }
        }

        // see OwnershipTest for other elements
        @Test
        fun `Cannot delete a realm that owns another element`() {
            val ownership = History<Owner>(OwnedByRealm(REALM_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)
            val newState = STATE.updateStorage(Storage(building))

            assertIllegalArgument("Cannot delete Realm 0, because of owned elements (Building)!") {
                REDUCER.invoke(newState, action)
            }
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

            assertIllegalArgument("Cannot use an unknown Character 99 as founder!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a realm`() {
            val realm = Realm(REALM_ID_0, NAME)
            val action = UpdateRealm(realm)

            assertEquals(realm, REDUCER.invoke(STATE, action).first.getRealmStorage().get(REALM_ID_0))
        }
    }

}