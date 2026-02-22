package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EmployedByRealm
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RealmTest {

    @Nested
    inner class CanDeleteTest {
        private val realm = Realm(REALM_ID_0)
        private val state = State(
            listOf(
                Storage(realm),
            )
        )

        @Test
        fun `Cannot delete a realm that killed a character`() {
            val dead = Dead(DAY0, KilledBy(RealmReference(REALM_ID_0)))
            val character = Character(CHARACTER_ID_0, status = dead)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a realm that created another element`() {
            val building = Building(BUILDING_ID_0, builder = RealmReference(REALM_ID_0))
            val newState = state.updateStorage(building)

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a realm that owns another element`() {
            val ownership = History<Reference>(RealmReference(REALM_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)
            val newState = state.updateStorage(building)

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a realm that owns another realm`() {
            val realm1 = Realm(REALM_ID_1, owner = History(REALM_ID_0))
            val newState = state.updateStorage(Storage(listOf(realm, realm1)))

            failCanDelete(newState, REALM_ID_1)
        }

        @Test
        fun `Cannot delete a realm that owned another realm`() {
            val history = History(null, HistoryEntry(REALM_ID_0, DAY0))
            val realm1 = Realm(REALM_ID_1, owner = history)
            val newState = state.updateStorage(Storage(listOf(realm, realm1)))

            failCanDelete(newState, REALM_ID_1)
        }

        @Test
        fun `Cannot delete a realm that owns a town`() {
            val settlement = Settlement(SETTLEMENT_ID_0, owner = History(REALM_ID_0))
            val newState = state.updateStorage(settlement)

            failCanDelete(newState, SETTLEMENT_ID_0)
        }

        @Test
        fun `Cannot delete a realm that owned a town`() {
            val history = History(null, HistoryEntry(REALM_ID_0, DAY0))
            val settlement = Settlement(SETTLEMENT_ID_0, owner = history)
            val newState = state.updateStorage(settlement)

            failCanDelete(newState, SETTLEMENT_ID_0)
        }

        @Test
        fun `Cannot delete a realm that participated in a war`() {
            val participant = WarParticipant(RealmReference(REALM_ID_0))
            val war = War(WAR_ID_0, participants = listOf(participant))
            val newState = state.updateStorage(war)

            failCanDelete(newState, WAR_ID_0)
        }

        @Test
        fun `Cannot delete a realm that participated in a battle`() {
            val battle = Battle(BATTLE_ID_0, participants = listOf(BattleParticipant(REALM_ID_0)))
            val newState = state.updateStorage(battle)

            failCanDelete(newState, BATTLE_ID_0)
        }

        @Test
        fun `Cannot delete a realm that employs a character`() {
            val employmentStatus = History<EmploymentStatus>(EmployedByRealm(JOB_ID_0, REALM_ID_0))
            val character = Character(CHARACTER_ID_0, employmentStatus = employmentStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a realm that is the home of a character`() {
            val housingStatus = History<Position>(InRealm(REALM_ID_0))
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a realm that signed a treaty`() {
            val treaty = Treaty(TREATY_ID_0, participants = listOf(TreatyParticipant(REALM_ID_0)))
            val newState = state.updateStorage(treaty)

            failCanDelete(newState, TREATY_ID_0)
        }

        @Test
        fun `Cannot delete a realm used as a position`() {
            val business = Business(BUSINESS_ID_0, position = InRealm(REALM_ID_0))
            val newState = state.updateStorage(business)

            failCanDelete(newState, BUSINESS_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(REALM_ID_0).addId(blockingId), state.canDeleteRealm(REALM_ID_0))
        }
    }

}