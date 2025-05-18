package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteCatastrophe
import at.orchaldir.gm.core.action.UpdateCatastrophe
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Dead
import at.orchaldir.gm.core.model.character.DeathByCatastrophe
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayOfCatastrophe
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.DestroyedByCatastrophe
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CatastropheTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Catastrophe(CATASTROPHE_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteCatastrophe(CATASTROPHE_ID_0)

        @Test
        fun `Can delete an existing catastrophe`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getCatastropheStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteCatastrophe(UNKNOWN_CATASTROPHE_ID)

            assertIllegalArgument("Requires unknown Catastrophe 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot delete a catastrophe that killed a character`() {
            val dead = Dead(DAY0, DeathByCatastrophe(CATASTROPHE_ID_0))
            val organization = Character(CHARACTER_ID_0, vitalStatus = dead)
            val newState = STATE.updateStorage(Storage(organization))

            assertIllegalArgument("Cannot delete Catastrophe 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a catastrophe that destroyed a realm`() {
            val status = DestroyedByCatastrophe(CATASTROPHE_ID_0, DAY0)
            val organization = Realm(REALM_ID_0, status = status)
            val newState = STATE.updateStorage(Storage(organization))

            assertIllegalArgument("Cannot delete Catastrophe 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a catastrophe that is remembered by a holiday`() {
            val purpose = HolidayOfCatastrophe(CATASTROPHE_ID_0)
            val organization = Holiday(HOLIDAY_ID_0, purpose = purpose)
            val newState = STATE.updateStorage(Storage(organization))

            assertIllegalArgument("Cannot delete Catastrophe 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateCatastrophe(Catastrophe(UNKNOWN_CATASTROPHE_ID))

            assertIllegalArgument("Requires unknown Catastrophe 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a catastrophe`() {
            val catastrophe = Catastrophe(CATASTROPHE_ID_0, NAME)
            val action = UpdateCatastrophe(catastrophe)

            assertEquals(catastrophe, REDUCER.invoke(STATE, action).first.getCatastropheStorage().get(CATASTROPHE_ID_0))
        }
    }

}