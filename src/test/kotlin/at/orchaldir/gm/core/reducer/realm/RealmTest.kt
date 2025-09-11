package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateRealm
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.population.TotalPopulation
import at.orchaldir.gm.core.reducer.REDUCER
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
            val action = UpdateRealm(Realm(UNKNOWN_REALM_ID))

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The Founder must exist`() {
            val realm = Realm(REALM_ID_0, founder = CharacterReference(UNKNOWN_CHARACTER_ID))
            val action = UpdateRealm(realm)

            assertIllegalArgument("Requires unknown founder (Character 99)!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The Capital must exist`() {
            val realm = Realm(REALM_ID_0, capital = History(UNKNOWN_TOWN_ID))
            val action = UpdateRealm(realm)

            assertIllegalArgument("Requires unknown Town 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The realm owning this realm must exist`() {
            val realm = Realm(REALM_ID_0, owner = History(UNKNOWN_REALM_ID))
            val action = UpdateRealm(realm)

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `A realm cannot own itself`() {
            val realm = Realm(REALM_ID_0, owner = History(REALM_ID_0))
            val action = UpdateRealm(realm)

            assertIllegalArgument("A realm cannot own itself!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot have the same owner 2 times in a row`() {
            val history = History<RealmId?>(REALM_ID_1, HistoryEntry(REALM_ID_1, DAY0))
            val realm = Realm(REALM_ID_0, owner = history)
            val action = UpdateRealm(realm)

            assertIllegalArgument("Cannot have the same owner 2 times in a row!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The legal code must exist`() {
            val realm = Realm(REALM_ID_0, legalCode = History(UNKNOWN_LEGAL_CODE_ID))
            val action = UpdateRealm(realm)

            assertIllegalArgument("Requires unknown Legal Code 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The currency must exist`() {
            val realm = Realm(REALM_ID_0, currency = History(UNKNOWN_CURRENCY_ID))
            val action = UpdateRealm(realm)

            assertIllegalArgument("Requires unknown Currency 99!") { REDUCER.invoke(STATE, action) }
        }

        @Nested
        inner class VitalStatusTest {

            @Test
            fun `A realm cannot die`() {
                val status = Dead(DAY0, DeathByCatastrophe(UNKNOWN_CATASTROPHE_ID))
                val realm = Realm(REALM_ID_0, status = status)
                val action = UpdateRealm(realm)

                assertIllegalArgument("Invalid vital status Dead!") { REDUCER.invoke(STATE, action) }
            }

            @Test
            fun `A realm can be alive`() {
                testValidStatus(Alive)
            }

            @Test
            fun `A realm can be abandoned`() {
                testValidStatus(Abandoned(DAY0))
            }

            @Test
            fun `A realm can be destroyed`() {
                testValidStatus(Destroyed(DAY0))
            }

            private fun testValidStatus(status: VitalStatus) {
                val realm = Realm(REALM_ID_0, status = status)
                val action = UpdateRealm(realm)

                REDUCER.invoke(STATE, action)
            }

        }

        @Test
        fun `The population is validated`() {
            val action = UpdateRealm(Realm(REALM_ID_0, population = TotalPopulation(0)))

            assertIllegalArgument("The total population must be greater than 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a realm`() {
            val realm = Realm(REALM_ID_0, NAME)
            val action = UpdateRealm(realm)

            assertEquals(realm, REDUCER.invoke(STATE, action).first.getRealmStorage().get(REALM_ID_0))
        }
    }

}