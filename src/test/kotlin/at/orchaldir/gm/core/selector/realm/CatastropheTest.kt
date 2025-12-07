package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.FinishedWar
import at.orchaldir.gm.core.model.realm.InterruptedByCatastrophe
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayOfCatastrophe
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.DeathByCatastrophe
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.Wasteland
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CatastropheTest {

    @Nested
    inner class CanDeleteTest {
        private val catastrophe = Catastrophe(CATASTROPHE_ID_0)
        private val state = State(
            listOf(
                Storage(catastrophe),
            )
        )

        @Test
        fun `Cannot delete a catastrophe that killed a character`() {
            val dead = Dead(DAY0, DeathByCatastrophe(CATASTROPHE_ID_0))
            val character = Character(CHARACTER_ID_0, status = dead)
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a catastrophe that is remembered by a holiday`() {
            val purpose = HolidayOfCatastrophe(CATASTROPHE_ID_0)
            val holiday = Holiday(HOLIDAY_ID_0, purpose = purpose)
            val newState = state.updateStorage(Storage(holiday))

            failCanDelete(newState, HOLIDAY_ID_0)
        }

        @Test
        fun `Cannot delete a catastrophe that created a region`() {
            val region = Region(REGION_ID_0, data = Wasteland(CATASTROPHE_ID_0))
            val newState = state.updateStorage(Storage(region))

            failCanDelete(newState, REGION_ID_0)
        }

        @Test
        fun `Cannot delete a catastrophe that interrupted a war`() {
            val status = FinishedWar(InterruptedByCatastrophe(CATASTROPHE_ID_0), DAY0)
            val war = War(WAR_ID_0, status = status)
            val newState = state.updateStorage(Storage(war))

            failCanDelete(newState, WAR_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(CATASTROPHE_ID_0).addId(blockingId), state.canDeleteCatastrophe(CATASTROPHE_ID_0))
        }
    }

}