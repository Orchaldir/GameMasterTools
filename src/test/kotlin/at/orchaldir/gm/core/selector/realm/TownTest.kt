package at.orchaldir.gm.core.selector.town

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EmployedByTown
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.selector.realm.canDeleteTown
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TownTest {

    @Nested
    inner class CanDeleteTest {
        private val town = Town(TOWN_ID_0)
        private val state = State(
            listOf(
                Storage(town),
            )
        )

        @Test
        fun `Cannot delete a town that killed a character`() {
            val dead = Dead(DAY0, KilledBy(TownReference(TOWN_ID_0)))
            val character = Character(CHARACTER_ID_0, vitalStatus = dead)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a town that created another element`() {
            val building = Building(BUILDING_ID_0, builder = TownReference(TOWN_ID_0))
            val newState = state.updateStorage(Storage(building))

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a town that owns another element`() {
            val ownership = History<Reference>(TownReference(TOWN_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)
            val newState = state.updateStorage(Storage(building))

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a town that has districts`() {
            val district = District(DISTRICT_ID_0, town = TOWN_ID_0)
            val newState = state.updateStorage(Storage(district))

            failCanDelete(newState, DISTRICT_ID_0)
        }

        @Test
        fun `Cannot delete a town that has a town map`() {
            val map = TownMap(TOWN_MAP_ID_0, TOWN_ID_0)
            val newState = state.updateStorage(Storage(map))

            failCanDelete(newState, TOWN_MAP_ID_0)
        }

        @Test
        fun `Cannot delete a town that is a capital`() {
            val capital = Realm(REALM_ID_0, capital = History(TOWN_ID_0))
            val newState = state.updateStorage(Storage(capital))

            failCanDelete(newState, REALM_ID_0)
        }

        @Test
        fun `Cannot delete a town that was a capital`() {
            val history = History(null, HistoryEntry(TOWN_ID_0, DAY0))
            val capital = Realm(REALM_ID_0, capital = history)
            val newState = state.updateStorage(Storage(capital))

            failCanDelete(newState, REALM_ID_0)
        }

        @Test
        fun `Cannot delete a town that participated in a war`() {
            val participant = WarParticipant(TownReference(TOWN_ID_0))
            val war = War(WAR_ID_0, participants = listOf(participant))
            val newState = state.updateStorage(Storage(war))

            failCanDelete(newState, WAR_ID_0)
        }

        @Test
        fun `Cannot delete a town that is the home of a character`() {
            val housingStatus = History<Position>(InTown(TOWN_ID_0))
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a town that employs a character`() {
            val employmentStatus = History<EmploymentStatus>(EmployedByTown(JOB_ID_0, TOWN_ID_0))
            val character = Character(CHARACTER_ID_0, employmentStatus = employmentStatus)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a town that employed a character`() {
            val historyEntry = HistoryEntry<EmploymentStatus>(EmployedByTown(JOB_ID_0, TOWN_ID_0), DAY0)
            val employmentStatus = History(Unemployed, listOf(historyEntry))
            val character = Character(CHARACTER_ID_0, employmentStatus = employmentStatus)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a town used as a position`() {
            val business = Business(BUSINESS_ID_0, position = InTown(TOWN_ID_0))
            val newState = state.updateStorage(Storage(business))

            failCanDelete(newState, BUSINESS_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(TOWN_ID_0).addId(blockingId), state.canDeleteTown(TOWN_ID_0))
        }
    }

}