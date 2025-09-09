package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EmployedByTown
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.business.Business
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
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateTown(Town(UNKNOWN_TOWN_ID))

            assertIllegalArgument("Requires unknown Town 99!") { REDUCER.invoke(State(), action) }
        }

        @Nested
        inner class VitalStatusTest {

            @Test
            fun `A town cannot die`() {
                val status = Dead(DAY0, DeathByCatastrophe(UNKNOWN_CATASTROPHE_ID))
                val town = Town(TOWN_ID_0, status = status)
                val action = UpdateTown(town)

                assertIllegalArgument("Invalid vital status Dead!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `A town can be alive`() {
                testValidStatus(Alive)
            }

            @Test
            fun `A town can be abandoned`() {
                testValidStatus(Abandoned(DAY0))
            }

            @Test
            fun `A town can be destroyed`() {
                testValidStatus(Destroyed(DAY0))
            }

            private fun testValidStatus(status: VitalStatus) {
                val town = Town(TOWN_ID_0, status = status)
                val action = UpdateTown(town)

                REDUCER.invoke(STATE, action);
            }

        }

        @Test
        fun `Founder must exist`() {
            val action = UpdateTown(Town(TOWN_ID_0, founder = CharacterReference(UNKNOWN_CHARACTER_ID)))

            assertIllegalArgument("Requires unknown founder (Character 99)!") { REDUCER.invoke(STATE, action) }
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