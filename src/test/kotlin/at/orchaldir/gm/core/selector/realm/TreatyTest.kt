package at.orchaldir.gm.core.selector.town

import at.orchaldir.gm.DAY0
import at.orchaldir.gm.HOLIDAY_ID_0
import at.orchaldir.gm.TREATY_ID_0
import at.orchaldir.gm.WAR_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.FinishedWar
import at.orchaldir.gm.core.model.realm.Peace
import at.orchaldir.gm.core.model.realm.Treaty
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayOfTreaty
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
            val newState = state.updateStorage(holiday)

            failCanDelete(newState, HOLIDAY_ID_0)
        }

        @Test
        fun `Cannot delete a treaty that ended a war`() {
            val status = FinishedWar(Peace(TREATY_ID_0), DAY0)
            val war = War(WAR_ID_0, status = status)
            val newState = state.updateStorage(war)

            failCanDelete(newState, WAR_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(TREATY_ID_0).addId(blockingId), state.canDeleteTreaty(TREATY_ID_0))
        }
    }

}