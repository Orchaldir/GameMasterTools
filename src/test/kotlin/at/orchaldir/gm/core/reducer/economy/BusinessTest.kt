package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class BusinessTest {

    @Nested
    inner class DeleteTest {
        val action = DeleteBusiness(BUSINESS_ID_0)

        @Test
        fun `Can delete an existing business`() {
            val state = State(Storage(Business(BUSINESS_ID_0)))

            assertEquals(0, REDUCER.invoke(state, action).first.getBusinessStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Business 0!") { REDUCER.invoke(State(), action) }
        }

        // see CreatorTest for other elements
        @Test
        fun `Cannot delete a business that created another element`() {
            val newState = createState(Building(BUILDING_ID_0, builder = BusinessReference(BUSINESS_ID_0)))

            assertIllegalArgument("Cannot delete Business 0, because of created elements (Building)!") {
                REDUCER.invoke(newState, action)
            }
        }

        // see OwnershipTest for other elements
        @Test
        fun `Cannot delete a business that owns another element`() {
            val ownership = History<Reference>(BusinessReference(BUSINESS_ID_0))
            val newState = createState(Building(BUILDING_ID_0, ownership = ownership))

            assertIllegalArgument("Cannot delete Business 0, because of owned elements (Building)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a business where a character is employed`() {
            val state =
                createState(Character(CHARACTER_ID_0, employmentStatus = History(Employed(BUSINESS_ID_0, JobId(0)))))
            val action = DeleteBusiness(BUSINESS_ID_0)

            assertIllegalArgument("Cannot delete Business 0, because it has employees!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a business where a character was previously employed`() {
            val employmentStatus = History(Unemployed, listOf(HistoryEntry(Employed(BUSINESS_ID_0, JobId(0)), DAY0)))
            val state = createState(Character(CHARACTER_ID_0, employmentStatus = employmentStatus))
            val action = DeleteBusiness(BUSINESS_ID_0)

            assertIllegalArgument("Cannot delete Business 0, because it has previous employees!") {
                REDUCER.invoke(state, action)
            }
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> createState(element: ELEMENT) = State(
            listOf(
                Storage(listOf(Business(BUSINESS_ID_0))),
                Storage(listOf(element))
            )
        )
    }

    @Nested
    inner class UpdateTest {

        private val STATE = State(
            listOf(
                Storage(Business(BUSINESS_ID_0)),
                Storage(CALENDAR0),
                Storage(Character(CHARACTER_ID_0)),
            )
        )

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateBusiness(Business(UNKNOWN_BUSINESS_ID))

            assertIllegalArgument("Requires unknown Business 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Owner is an unknown character`() {
            val action =
                UpdateBusiness(Business(BUSINESS_ID_0, ownership = History(CharacterReference(CHARACTER_ID_0))))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Cannot use an unknown Character 0 as owner!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Founder is an unknown character`() {
            val action = UpdateBusiness(Business(BUSINESS_ID_0, founder = CharacterReference(CHARACTER_ID_0)))
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertIllegalArgument("Cannot use an unknown Character 0 as Founder!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `In unknown building`() {
            val action = UpdateBusiness(Business(BUSINESS_ID_0, position = InBuilding(UNKNOWN_BUILDING_ID)))

            assertIllegalArgument("Requires unknown position!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateBusiness(Business(BUSINESS_ID_0, startDate = FUTURE_DAY_0))

            assertIllegalArgument("Date (Business Founding) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Test Success`() {
            val business = Business(BUSINESS_ID_0, Name.init("Test"))
            val action = UpdateBusiness(business)

            assertEquals(business, REDUCER.invoke(STATE, action).first.getBusinessStorage().get(BUSINESS_ID_0))
        }
    }

}