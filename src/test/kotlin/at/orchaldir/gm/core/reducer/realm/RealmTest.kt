package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.EconomyWithPercentages
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.util.VitalStatusType
import at.orchaldir.gm.core.model.realm.population.TotalPopulation
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.core.reducer.util.testAllowedVitalStatusTypes
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RealmTest {

    private val realm0 = Realm(REALM_ID_0)
    private val realm1 = Realm(REALM_ID_1)
    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(listOf(realm0, realm1)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(Realm(UNKNOWN_REALM_ID))

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The Founder must exist`() {
            val realm = Realm(REALM_ID_0, founder = CharacterReference(UNKNOWN_CHARACTER_ID))
            val action = UpdateAction(realm)

            assertIllegalArgument("Requires unknown founder (Character 99)!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The Capital must exist`() {
            val realm = Realm(REALM_ID_0, capital = History(UNKNOWN_TOWN_ID))
            val action = UpdateAction(realm)

            assertIllegalArgument("Requires unknown Town 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The realm owning this realm must exist`() {
            val realm = Realm(REALM_ID_0, owner = History(UNKNOWN_REALM_ID))
            val action = UpdateAction(realm)

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `A realm cannot own itself`() {
            val realm = Realm(REALM_ID_0, owner = History(REALM_ID_0))
            val action = UpdateAction(realm)

            assertIllegalArgument("A realm cannot own itself!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot have the same owner 2 times in a row`() {
            val history = History<RealmId?>(REALM_ID_1, HistoryEntry(REALM_ID_1, DAY0))
            val realm = Realm(REALM_ID_0, owner = history)
            val action = UpdateAction(realm)

            assertIllegalArgument("Cannot have the same owner 2 times in a row!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The legal code must exist`() {
            val realm = Realm(REALM_ID_0, legalCode = History(UNKNOWN_LEGAL_CODE_ID))
            val action = UpdateAction(realm)

            assertIllegalArgument("Requires unknown Legal Code 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The currency must exist`() {
            val realm = Realm(REALM_ID_0, currency = History(UNKNOWN_CURRENCY_ID))
            val action = UpdateAction(realm)

            assertIllegalArgument("Requires unknown Currency 99!") { REDUCER.invoke(STATE, action) }
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
                Realm(REALM_ID_0, date = DAY0, status = status)
            }
        }

        @Test
        fun `The population is validated`() {
            val action = UpdateAction(Realm(REALM_ID_0, population = TotalPopulation(-1)))

            assertIllegalArgument("The total population must be >= 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The economy is validated`() {
            val action = UpdateAction(Realm(REALM_ID_0, economy = EconomyWithPercentages(-1)))

            assertIllegalArgument("The total number of businesses must be >= 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a realm`() {
            val realm = Realm(REALM_ID_0, NAME)
            val action = UpdateAction(realm)

            assertEquals(realm, REDUCER.invoke(STATE, action).first.getRealmStorage().get(REALM_ID_0))
        }
    }

}