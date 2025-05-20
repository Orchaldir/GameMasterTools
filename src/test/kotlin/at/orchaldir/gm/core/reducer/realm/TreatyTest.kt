package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteTreaty
import at.orchaldir.gm.core.action.UpdateTreaty
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayOfTreaty
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
    inner class DeleteTest {

        private val action = DeleteTreaty(TREATY_ID_0)

        @Test
        fun `Can delete an existing Treaty`() {
            val state = State(Storage(Treaty(TREATY_ID_0)))

            assertEquals(0, REDUCER.invoke(state, action).first.getTreatyStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a treaty that is celebrated by a holiday`() {
            val purpose = HolidayOfTreaty(TREATY_ID_0)
            val organization = Holiday(HOLIDAY_ID_0, purpose = purpose)
            val newState = STATE.updateStorage(Storage(organization))

            assertIllegalArgument("Cannot delete Treaty 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a treated that ended a war`() {
            val status = FinishedWar(Peace(TREATY_ID_0), DAY0)
            val war = War(WAR_ID_0, status = status)
            val newState = STATE.updateStorage(Storage(war))

            assertIllegalArgument("Cannot delete Treaty 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateTreaty(Treaty(TREATY_ID_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `A participating realm must exist`() {
            val participant = TreatyParticipant(UNKNOWN_REALM_ID)
            val action = UpdateTreaty(Treaty(TREATY_ID_0, participants = listOf(participant)))

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `A character signing the treaty must exist`() {
            val participant = TreatyParticipant(REALM_ID_0, UNKNOWN_CHARACTER_ID)
            val action = UpdateTreaty(Treaty(TREATY_ID_0, participants = listOf(participant)))

            assertIllegalArgument("Requires unknown Character 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update is valid`() {
            val treaty = Treaty(TREATY_ID_0, Name.Companion.init("Test"))
            val action = UpdateTreaty(treaty)

            assertEquals(treaty, REDUCER.invoke(STATE, action).first.getTreatyStorage().get(TREATY_ID_0))
        }
    }

}