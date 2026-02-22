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
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.selector.realm.canDeleteSettlement
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TownTest {

    @Nested
    inner class CanDeleteTest {
        private val settlement = Settlement(TOWN_ID_0)
        private val state = State(
            listOf(
                Storage(settlement),
            )
        )

        @Test
        fun `Cannot delete a town that killed a character`() {
            val dead = Dead(DAY0, KilledBy(SettlementReference(TOWN_ID_0)))
            val character = Character(CHARACTER_ID_0, status = dead)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a town that created another element`() {
            val building = Building(BUILDING_ID_0, builder = SettlementReference(TOWN_ID_0))
            val newState = state.updateStorage(building)

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a town that owns another element`() {
            val ownership = History<Reference>(SettlementReference(TOWN_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)
            val newState = state.updateStorage(building)

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a town that has districts`() {
            val district = District(DISTRICT_ID_0, position = InSettlement(TOWN_ID_0))
            val newState = state.updateStorage(district)

            failCanDelete(newState, DISTRICT_ID_0)
        }

        @Test
        fun `Cannot delete a town that has a town map`() {
            val map = SettlementMap(TOWN_MAP_ID_0, TOWN_ID_0)
            val newState = state.updateStorage(map)

            failCanDelete(newState, TOWN_MAP_ID_0)
        }

        @Test
        fun `Cannot delete a town that is a capital`() {
            val capital = Realm(REALM_ID_0, capital = History(TOWN_ID_0))
            val newState = state.updateStorage(capital)

            failCanDelete(newState, REALM_ID_0)
        }

        @Test
        fun `Cannot delete a town that was a capital`() {
            val history = History(null, HistoryEntry(TOWN_ID_0, DAY0))
            val capital = Realm(REALM_ID_0, capital = history)
            val newState = state.updateStorage(capital)

            failCanDelete(newState, REALM_ID_0)
        }

        @Test
        fun `Cannot delete a town that participated in a war`() {
            val participant = WarParticipant(SettlementReference(TOWN_ID_0))
            val war = War(WAR_ID_0, participants = listOf(participant))
            val newState = state.updateStorage(war)

            failCanDelete(newState, WAR_ID_0)
        }

        @Test
        fun `Cannot delete a town that is the home of a character`() {
            val housingStatus = History<Position>(InSettlement(TOWN_ID_0))
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a town that employs a character`() {
            val employmentStatus = History<EmploymentStatus>(EmployedByTown(JOB_ID_0, TOWN_ID_0))
            val character = Character(CHARACTER_ID_0, employmentStatus = employmentStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a town that employed a character`() {
            val historyEntry = HistoryEntry<EmploymentStatus>(EmployedByTown(JOB_ID_0, TOWN_ID_0), DAY0)
            val employmentStatus = History(Unemployed, listOf(historyEntry))
            val character = Character(CHARACTER_ID_0, employmentStatus = employmentStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a town used as a position`() {
            val business = Business(BUSINESS_ID_0, position = InSettlement(TOWN_ID_0))
            val newState = state.updateStorage(business)

            failCanDelete(newState, BUSINESS_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(TOWN_ID_0).addId(blockingId), state.canDeleteSettlement(TOWN_ID_0))
        }
    }

}