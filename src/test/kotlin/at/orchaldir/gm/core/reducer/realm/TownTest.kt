package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.population.TotalPopulation
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TownTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
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

        // see CreatorTest for other elements
        @Test
        fun `Cannot delete a town that created another element`() {
            val state = createState(Building(BUILDING_ID_0, builder = CreatedByTown(TOWN_ID_0)))

            assertIllegalArgument("Cannot delete Town 0, because of created elements (Building)!") {
                REDUCER.invoke(state, action)
            }
        }

        // see OwnershipTest for other elements
        @Test
        fun `Cannot delete a town that owns another element`() {
            val ownership = History<Owner>(OwnedByTown(TOWN_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)
            val newState = STATE.updateStorage(Storage(building))

            assertIllegalArgument("Cannot delete Town 0, because of owned elements (Building)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a town with a district`() {
            val state = createState(District(DISTRICT_ID_0))

            assertIllegalArgument("Cannot delete Town 0, because it has district(s)!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a town with a town map`() {
            val state = createState(TownMap(TOWN_MAP_ID_0, TOWN_ID_0))

            assertIllegalArgument("Cannot delete Town 0, because it has a town map!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a town that is a capital`() {
            val state = createState(Realm(REALM_ID_0, capital = History(TOWN_ID_0)))

            assertIllegalArgument("Cannot delete Town 0, because it is a capital!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a town that was a capital`() {
            val history = History<TownId?>(null, HistoryEntry(TOWN_ID_0, DAY0))
            val state = createState(Realm(REALM_ID_0, capital = history))

            assertIllegalArgument("Cannot delete Town 0, because it was a capital!") {
                REDUCER.invoke(state, action)
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
        fun `Cannot delete a town that is the home of a character`() {
            val housingStatus = History<HousingStatus>(InTown(TOWN_ID_0))
            val state = createState(Character(CHARACTER_ID_0, housingStatus = housingStatus))

            assertIllegalArgument("Cannot delete Town 0, because it is a home!") {
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
            val action = UpdateTown(Town(UNKNOWN_TOWN_ID))

            assertIllegalArgument("Requires unknown Town 99!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Founder must exist`() {
            val action = UpdateTown(Town(TOWN_ID_0, founder = CreatedByCharacter(CHARACTER_ID_0)))

            assertIllegalArgument("Cannot use an unknown Character 0 as founder!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Owner must exist`() {
            val action = UpdateTown(Town(TOWN_ID_0, owner = History(UNKNOWN_REALM_ID)))

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateTown(Town(TOWN_ID_0, foundingDate = FUTURE_DAY_0))

            assertIllegalArgument("Date (Town) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The population is validated`() {
            val action = UpdateTown(Town(TOWN_ID_0, population = TotalPopulation(0)))

            assertIllegalArgument("The total population must be greater than 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update is valid`() {
            val town = Town(TOWN_ID_0, Name.Companion.init("Test"))
            val action = UpdateTown(town)

            assertEquals(town, REDUCER.invoke(STATE, action).first.getTownStorage().get(TOWN_ID_0))
        }
    }

}