package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.InApartment
import at.orchaldir.gm.core.model.character.InHouse
import at.orchaldir.gm.core.model.character.LivingStatus
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.world.building.BUILDING
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test


private val IN_APARTMENT = InApartment(BUILDING_ID_0, 0)
private val IN_HOUSE = InHouse(BUILDING_ID_0)
private val STATE = State(
    listOf(
        Storage(listOf(Building(BUILDING_ID_0), Building(BUILDING_ID_1))),
        Storage(CALENDAR0),
    )
)

class LivingStatusTest {

    @Test
    fun `Cannot use unknown building as home`() {
        val state = STATE.removeStorage(BUILDING)

        assertIllegalArgument("The home doesn't exist!") {
            checkLivingStatusHistory(state, History(IN_HOUSE), DAY0)
        }
    }

    @Test
    fun `Cannot use unknown building as a previous home`() {
        val state = STATE.updateStorage(Storage(Building(BUILDING_ID_0)))
        val entry = HistoryEntry<LivingStatus>(InHouse(BUILDING_ID_1), DAY1)

        assertIllegalArgument("The 1.previous home doesn't exist!") {
            checkLivingStatusHistory(state, History(IN_HOUSE, entry), DAY0)
        }
    }

    @Test
    fun `Cannot use unknown building as apartment house`() {
        val state = STATE.removeStorage(BUILDING)

        assertIllegalArgument("The home doesn't exist!") {
            checkLivingStatusHistory(state, History(IN_APARTMENT), DAY0)
        }
    }

}