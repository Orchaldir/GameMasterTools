package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.EconomyWithPercentages
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.realm.population.TotalPopulation
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.VitalStatusType
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.core.reducer.util.testAllowedVitalStatusTypes
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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
            val action = UpdateAction(Town(UNKNOWN_TOWN_ID))

            assertIllegalArgument("Requires unknown Town 99!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Test allowed vital status types`() {
            testAllowedVitalStatusTypes(
                STATE,
                mapOf(
                    VitalStatusType.Abandoned to true,
                    VitalStatusType.Alive to true,
                    VitalStatusType.Closed to false,
                    VitalStatusType.Dead to false,
                    VitalStatusType.Destroyed to true,
                    VitalStatusType.Vanished to false,
                ),
            ) { status ->
                Town(TOWN_ID_0, date = DAY0, status = status)
            }
        }

        @Test
        fun `Founder must exist`() {
            val action = UpdateAction(Town(TOWN_ID_0, founder = CharacterReference(UNKNOWN_CHARACTER_ID)))

            assertIllegalArgument("Requires unknown founder (Character 99)!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Owner must exist`() {
            val action = UpdateAction(Town(TOWN_ID_0, owner = History(UNKNOWN_REALM_ID)))

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Date is in the future`() {
            val action = UpdateAction(Town(TOWN_ID_0, date = FUTURE_DAY_0))

            assertIllegalArgument("Date (Town) is in the future!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The population is validated`() {
            val action = UpdateAction(Town(TOWN_ID_0, population = TotalPopulation(-1)))

            assertIllegalArgument("The total population must be >= 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The economy is validated`() {
            val action = UpdateAction(Town(TOWN_ID_0, economy = EconomyWithPercentages(-1)))

            assertIllegalArgument("The total number of businesses must be >= 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update is valid`() {
            val town = Town(TOWN_ID_0, Name.init("Test"))
            val action = UpdateAction(town)

            assertEquals(town, REDUCER.invoke(STATE, action).first.getTownStorage().get(TOWN_ID_0))
        }
    }

}