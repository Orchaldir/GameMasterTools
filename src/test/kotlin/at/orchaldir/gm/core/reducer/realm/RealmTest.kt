package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteRealm
import at.orchaldir.gm.core.action.UpdateRealm
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EmployedByRealm
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.HousingStatus
import at.orchaldir.gm.core.model.character.InRealm
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
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
    inner class DeleteTest {
        val action = DeleteRealm(REALM_ID_0)

        @Test
        fun `Can delete an existing realm`() {
            assertEquals(1, REDUCER.invoke(STATE, action).first.getRealmStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteRealm(UNKNOWN_REALM_ID)

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        // see CreatorTest for other elements
        @Test
        fun `Cannot delete a realm that created another element`() {
            val building = Building(BUILDING_ID_0, builder = CreatedByRealm(REALM_ID_0))

            test(building, "Cannot delete Realm 0, because of created elements (Building)!")
        }

        // see OwnershipTest for other elements
        @Test
        fun `Cannot delete a realm that owns another element`() {
            val ownership = History<Owner>(OwnedByRealm(REALM_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)

            test(building, "Cannot delete Realm 0, because of owned elements (Building)!")
        }

        @Test
        fun `Cannot delete a realm that owns another realm`() {
            val newRealm1 = Realm(REALM_ID_1, owner = History(REALM_ID_0))
            val newState = STATE.updateStorage(Storage(listOf(realm0, newRealm1)))

            assertIllegalArgument("Cannot delete Realm 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a realm that owned another realm`() {
            val history = History(null, HistoryEntry(REALM_ID_0, DAY0))
            val newRealm1 = Realm(REALM_ID_1, owner = history)
            val newState = STATE.updateStorage(Storage(listOf(realm0, newRealm1)))

            assertIllegalArgument("Cannot delete Realm 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a realm that owns a town`() {
            val town = Town(TOWN_ID_0, owner = History(REALM_ID_0))

            test(town, "Cannot delete Realm 0, because it is used!")
        }

        @Test
        fun `Cannot delete a realm that owned a town`() {
            val history = History(null, HistoryEntry(REALM_ID_0, DAY0))
            val town = Town(TOWN_ID_0, owner = history)

            test(town, "Cannot delete Realm 0, because it is used!")
        }

        @Test
        fun `Cannot delete a realm that participated in a war`() {
            val war = War(WAR_ID_0, realms = setOf(REALM_ID_0))

            test(war, "Cannot delete Realm 0, because it is used!")
        }

        @Test
        fun `Cannot delete a realm that participated in a battle`() {
            val war = Battle(BATTLE_ID_0, participants = listOf(BattleParticipant(REALM_ID_0)))

            test(war, "Cannot delete Realm 0, because it is used!")
        }

        @Test
        fun `Cannot delete a realm that employs a character`() {
            val employmentStatus = History<EmploymentStatus>(EmployedByRealm(JOB_ID_0, REALM_ID_0))
            val character = Character(CHARACTER_ID_0, employmentStatus = employmentStatus)

            test(character, "Cannot delete Realm 0, because it has or had employees!")
        }

        @Test
        fun `Cannot delete a realm that is the home of a character`() {
            val housingStatus = History<HousingStatus>(InRealm(REALM_ID_0))
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)

            test(character, "Cannot delete Realm 0, because it is used!")
        }

        @Test
        fun `Cannot delete a realm that signed a treaty`() {
            val treaty = Treaty(TREATY_ID_0, participants = listOf(TreatyParticipant(REALM_ID_0)))

            test(treaty, "Cannot delete Realm 0, because of created elements (Treaty)!")
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> test(element: ELEMENT, message: String) {
            val state = STATE.updateStorage(Storage(element))

            assertIllegalArgument(message) {
                REDUCER.invoke(state, action)
            }
        }

    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRealm(Realm(UNKNOWN_REALM_ID))

            assertIllegalArgument("Requires unknown Realm 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The Founder must exist`() {
            val realm = Realm(REALM_ID_0, founder = CreatedByCharacter(UNKNOWN_CHARACTER_ID))
            val action = UpdateRealm(realm)

            assertIllegalArgument("Cannot use an unknown Character 99 as founder!") { REDUCER.invoke(STATE, action) }
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

        @Test
        fun `The catastrophe that destroyed the realm must exist`() {
            val status = Dead(DAY0, DeathByCatastrophe(UNKNOWN_CATASTROPHE_ID))
            val realm = Realm(REALM_ID_0, status = status)
            val action = UpdateRealm(realm)

            assertIllegalArgument("Cannot die from an unknown Catastrophe 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `The war that destroyed the realm must exist`() {
            val status = Dead(DAY0, DeathInWar(UNKNOWN_WAR_ID))
            val realm = Realm(REALM_ID_0, status = status)
            val action = UpdateRealm(realm)

            assertIllegalArgument("Cannot die from an unknown War 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a realm`() {
            val realm = Realm(REALM_ID_0, NAME)
            val action = UpdateRealm(realm)

            assertEquals(realm, REDUCER.invoke(STATE, action).first.getRealmStorage().get(REALM_ID_0))
        }
    }

}