package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EmployedByTown
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TownTest {

    private val OWNER = History<Owner>(OwnedByTown(TOWN_ID_0))
    private val PREVIOUS_OWNER = History(UndefinedOwner, listOf(HistoryEntry(OwnedByTown(TOWN_ID_0), Day(0))))
    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Street(STREET_ID_0)),
            Storage(StreetTemplate(STREET_TYPE_ID_0)),
            Storage(Town(TOWN_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {

        private val action = DeleteTown(TOWN_ID_0)

        @Test
        fun `Can delete an existing Town`() {
            val state = State(Storage(Town(TOWN_ID_0)))

            assertEquals(0, REDUCER.invoke(state, action).first.getTownStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a town that build a building`() {
            val state = createState(Building(BUILDING_ID_0, builder = CreatedByTown(TOWN_ID_0)))

            assertIllegalArgument("Cannot delete Town 0, because of created elements (Building)!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Cannot delete a town that employs a character`() {
            val employmentStatus = History<EmploymentStatus>(EmployedByTown(JOB_ID_0, TOWN_ID_0))
            val state = createState(Character(CHARACTER_ID_0, employmentStatus = employmentStatus))

            assertIllegalArgument("Cannot delete Town 0, because it has or had employees!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a town that employed a character`() {
            val historyEntry = HistoryEntry<EmploymentStatus>(EmployedByTown(JOB_ID_0, TOWN_ID_0), DAY0)
            val employmentStatus = History(Unemployed, listOf(historyEntry))
            val state = createState(Character(CHARACTER_ID_0, employmentStatus = employmentStatus))

            assertIllegalArgument("Cannot delete Town 0, because it has or had employees!") {
                REDUCER.invoke(state, action)
            }
        }

        @Nested
        inner class BuildingOwnerTest {

            @Test
            fun `Cannot delete a building owner`() {
                val state = createState(Building(BUILDING_ID_0, ownership = OWNER))

                assertIllegalArgument("Cannot delete Town 0, because of owned elements (Building)!") {
                    REDUCER.invoke(state, action)
                }
            }

            @Test
            fun `Cannot delete a previous building owner`() {
                val state = createState(Building(BUILDING_ID_0, ownership = PREVIOUS_OWNER))

                assertIllegalArgument("Cannot delete Town 0, because of previously owned elements (Building)!") {
                    REDUCER.invoke(state, action)
                }
            }
        }

        @Nested
        inner class BusinessOwnerTest {

            @Test
            fun `Cannot delete a business owner`() {
                val state = createState(Business(BUSINESS_ID_0, ownership = OWNER))

                assertIllegalArgument("Cannot delete Town 0, because of owned elements (Business)!") {
                    REDUCER.invoke(state, action)
                }
            }

            @Test
            fun `Cannot delete a previous business owner`() {
                val state = createState(Business(BUSINESS_ID_0, ownership = PREVIOUS_OWNER))

                assertIllegalArgument("Cannot delete Town 0, because of previously owned elements (Business)!") {
                    REDUCER.invoke(state, action)
                }
            }
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> createState(element: ELEMENT): State {
            val state = State(
                listOf(
                    Storage(listOf(Town(TOWN_ID_0))),
                    Storage(listOf(element))
                )
            )
            return state
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateTown(Town(TOWN_ID_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Founder must exist`() {
            val action = UpdateTown(Town(TOWN_ID_0, founder = CreatedByCharacter(CHARACTER_ID_0)))

            assertIllegalArgument("Cannot use an unknown character 0 as founder!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateTown(Town(TOWN_ID_0, foundingDate = FUTURE_DAY_0))

            assertIllegalArgument("Date (Town) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update is valid`() {
            val town = Town(TOWN_ID_0, Name.Companion.init("Test"))
            val action = UpdateTown(town)

            assertEquals(town, REDUCER.invoke(STATE, action).first.getTownStorage().get(TOWN_ID_0))
        }
    }

}