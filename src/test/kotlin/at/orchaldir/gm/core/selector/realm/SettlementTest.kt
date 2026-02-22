package at.orchaldir.gm.core.selector.settlement

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EmployedBySettlement
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

class SettlementTest {

    @Nested
    inner class CanDeleteTest {
        private val settlement = Settlement(SETTLEMENT_ID_0)
        private val state = State(
            listOf(
                Storage(settlement),
            )
        )

        @Test
        fun `Cannot delete a settlement that killed a character`() {
            val dead = Dead(DAY0, KilledBy(SettlementReference(SETTLEMENT_ID_0)))
            val character = Character(CHARACTER_ID_0, status = dead)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a settlement that created another element`() {
            val building = Building(BUILDING_ID_0, builder = SettlementReference(SETTLEMENT_ID_0))
            val newState = state.updateStorage(building)

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a settlement that owns another element`() {
            val ownership = History<Reference>(SettlementReference(SETTLEMENT_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)
            val newState = state.updateStorage(building)

            failCanDelete(newState, BUILDING_ID_0)
        }

        @Test
        fun `Cannot delete a settlement that has districts`() {
            val district = District(DISTRICT_ID_0, position = InSettlement(SETTLEMENT_ID_0))
            val newState = state.updateStorage(district)

            failCanDelete(newState, DISTRICT_ID_0)
        }

        @Test
        fun `Cannot delete a settlement that has a settlement map`() {
            val map = SettlementMap(SETTLEMENT_MAP_ID_0, SETTLEMENT_ID_0)
            val newState = state.updateStorage(map)

            failCanDelete(newState, SETTLEMENT_MAP_ID_0)
        }

        @Test
        fun `Cannot delete a settlement that is a capital`() {
            val capital = Realm(REALM_ID_0, capital = History(SETTLEMENT_ID_0))
            val newState = state.updateStorage(capital)

            failCanDelete(newState, REALM_ID_0)
        }

        @Test
        fun `Cannot delete a settlement that was a capital`() {
            val history = History(null, HistoryEntry(SETTLEMENT_ID_0, DAY0))
            val capital = Realm(REALM_ID_0, capital = history)
            val newState = state.updateStorage(capital)

            failCanDelete(newState, REALM_ID_0)
        }

        @Test
        fun `Cannot delete a settlement that participated in a war`() {
            val participant = WarParticipant(SettlementReference(SETTLEMENT_ID_0))
            val war = War(WAR_ID_0, participants = listOf(participant))
            val newState = state.updateStorage(war)

            failCanDelete(newState, WAR_ID_0)
        }

        @Test
        fun `Cannot delete a settlement that is the home of a character`() {
            val housingStatus = History<Position>(InSettlement(SETTLEMENT_ID_0))
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a settlement that employs a character`() {
            val employmentStatus = History<EmploymentStatus>(EmployedBySettlement(JOB_ID_0, SETTLEMENT_ID_0))
            val character = Character(CHARACTER_ID_0, employmentStatus = employmentStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a settlement that employed a character`() {
            val historyEntry = HistoryEntry<EmploymentStatus>(EmployedBySettlement(JOB_ID_0, SETTLEMENT_ID_0), DAY0)
            val employmentStatus = History(Unemployed, listOf(historyEntry))
            val character = Character(CHARACTER_ID_0, employmentStatus = employmentStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a settlement used as a position`() {
            val business = Business(BUSINESS_ID_0, position = InSettlement(SETTLEMENT_ID_0))
            val newState = state.updateStorage(business)

            failCanDelete(newState, BUSINESS_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(SETTLEMENT_ID_0).addId(blockingId), state.canDeleteSettlement(SETTLEMENT_ID_0))
        }
    }

}