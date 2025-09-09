package at.orchaldir.gm.core.selector.town

import at.orchaldir.gm.BUILDING_ID_0
import at.orchaldir.gm.BUSINESS_ID_0
import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.DAY0
import at.orchaldir.gm.DISTRICT_ID_0
import at.orchaldir.gm.HOLIDAY_ID_0
import at.orchaldir.gm.JOB_ID_0
import at.orchaldir.gm.REALM_ID_0
import at.orchaldir.gm.TOWN_ID_0
import at.orchaldir.gm.TOWN_MAP_ID_0
import at.orchaldir.gm.TREATY_ID_0
import at.orchaldir.gm.WAR_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EmployedByTown
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.FinishedWar
import at.orchaldir.gm.core.model.realm.Peace
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Treaty
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.realm.WarParticipant
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayOfTreaty
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.util.InTown
import at.orchaldir.gm.core.model.util.KilledBy
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.util.TownReference
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.selector.realm.canDeleteTown
import at.orchaldir.gm.core.selector.realm.canDeleteTreaty
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TreatyTest {

    @Nested
    inner class CanDeleteTest {
        private val treaty = Treaty(TREATY_ID_0)
        private val state = State(
            listOf(
                Storage(treaty),
            )
        )

        @Test
        fun `Cannot delete a treaty that is celebrated by a holiday`() {
            val purpose = HolidayOfTreaty(TREATY_ID_0)
            val holiday = Holiday(HOLIDAY_ID_0, purpose = purpose)
            val newState = state.updateStorage(Storage(holiday))

            assertCanDelete(newState, HOLIDAY_ID_0)
        }

        @Test
        fun `Cannot delete a treated that ended a war`() {
            val status = FinishedWar(Peace(TREATY_ID_0), DAY0)
            val war = War(WAR_ID_0, status = status)
            val newState = state.updateStorage(Storage(war))

            assertCanDelete(newState, WAR_ID_0)
        }

        private fun <ID : Id<ID>> assertCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(TREATY_ID_0).addId(blockingId), state.canDeleteTreaty(TREATY_ID_0))
        }
    }

}