package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateDistrict
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.population.TotalPopulation
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DistrictTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(District(DISTRICT_ID_0)),
            Storage(Town(TOWN_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateDistrict(District(UNKNOWN_DISTRICT_ID))

            assertIllegalArgument("Requires unknown District 99!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Founder must exist`() {
            val action = UpdateDistrict(District(DISTRICT_ID_0, founder = CharacterReference(UNKNOWN_CHARACTER_ID)))

            assertIllegalArgument("Requires unknown founder (Character 99)!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Town must exist`() {
            val action = UpdateDistrict(District(DISTRICT_ID_0, town = UNKNOWN_TOWN_ID))

            assertIllegalArgument("Requires unknown Town 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The population is validated`() {
            val action = UpdateDistrict(District(DISTRICT_ID_0, population = TotalPopulation(0)))

            assertIllegalArgument("The total population must be greater than 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update is valid`() {
            val district = District(DISTRICT_ID_0, Name.Companion.init("Test"))
            val action = UpdateDistrict(district)

            assertEquals(district, REDUCER.invoke(STATE, action).first.getDistrictStorage().get(DISTRICT_ID_0))
        }
    }

}