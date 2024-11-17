package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.DAY0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.MonthDefinition
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.economy.business.BUSINESS
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.util.OwnedByCharacter
import at.orchaldir.gm.core.model.util.CreatedByBusiness
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.building.SingleBusiness
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = BusinessId(0)
private val BUILDING0 = BuildingId(0)
private val CHARACTER0 = CharacterId(0)

class BusinessTest {

    @Nested
    inner class DeleteTest {
        val action = DeleteBusiness(ID0)

        @Test
        fun `Can delete an existing business`() {
            val state = State(Storage(Business(ID0)))

            assertEquals(0, REDUCER.invoke(state, action).first.getBusinessStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Business 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a business used by a building`() {
            val state = createState(Building(BUILDING0, purpose = SingleBusiness(ID0)))

            assertIllegalArgument("Cannot delete business 0, because it has a building!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Cannot delete a business that build a building`() {
            val state = createState(Building(BUILDING0, builder = CreatedByBusiness(ID0)))

            assertIllegalArgument("Cannot delete business 0, because it has build a building!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Cannot delete a business where a character is employed`() {
            val state = createState(Character(CHARACTER0, employmentStatus = History(Employed(ID0, JobId(0)))))
            val action = DeleteBusiness(ID0)

            assertIllegalArgument("Cannot delete business 0, because it has employees!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Cannot delete a business where a character was previously employed`() {
            val employmentStatus = History(Unemployed, listOf(HistoryEntry(Employed(ID0, JobId(0)), DAY0)))
            val state = createState(Character(CHARACTER0, employmentStatus = employmentStatus))
            val action = DeleteBusiness(ID0)

            assertIllegalArgument("Cannot delete business 0, because it has previous employees!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> createState(element: ELEMENT) = State(
            listOf(
                Storage(listOf(Business(ID0))),
                Storage(listOf(element))
            )
        )
    }

    @Nested
    inner class UpdateTest {

        private val CALENDAR = Calendar(CalendarId(0), months = listOf(MonthDefinition("a")))
        private val STATE = State(
            listOf(
                Storage(Business(ID0)),
                Storage(CALENDAR),
                Storage(Character(CHARACTER0)),
            )
        )

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateBusiness(Business(ID0))
            val state = STATE.removeStorage(BUSINESS)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Owner is an unknown character`() {
            val action = UpdateBusiness(Business(ID0, ownership = History(OwnedByCharacter(CHARACTER0))))
            val state = STATE.removeStorage(CHARACTER)

            assertIllegalArgument("Cannot use an unknown character 0 as owner!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Test Success`() {
            val business = Business(ID0, "Test")
            val action = UpdateBusiness(business)

            assertEquals(business, REDUCER.invoke(STATE, action).first.getBusinessStorage().get(ID0))
        }
    }

}