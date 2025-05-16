package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteWar
import at.orchaldir.gm.core.action.UpdateWar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Dead
import at.orchaldir.gm.core.model.character.DeathByWar
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WarTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(War(WAR_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteWar(WAR_ID_0)

        @Test
        fun `Can delete an existing war`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getWarStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteWar(UNKNOWN_WAR_ID)

            assertIllegalArgument("Requires unknown War 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot delete a war that killed a character`() {
            val dead = Dead(DAY0, DeathByWar(WAR_ID_0))
            val organization = Character(CHARACTER_ID_0, vitalStatus = dead)
            val newState = STATE.updateStorage(Storage(organization))

            assertIllegalArgument("Cannot delete War 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }

    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateWar(War(UNKNOWN_WAR_ID))

            assertIllegalArgument("Requires unknown War 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Realm must exist`() {
            val war = War(WAR_ID_0, realms = setOf(UNKNOWN_REALM_ID))
            val action = UpdateWar(war)

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a war`() {
            val war = War(WAR_ID_0, NAME)
            val action = UpdateWar(war)

            assertEquals(war, REDUCER.invoke(STATE, action).first.getWarStorage().get(WAR_ID_0))
        }
    }

}