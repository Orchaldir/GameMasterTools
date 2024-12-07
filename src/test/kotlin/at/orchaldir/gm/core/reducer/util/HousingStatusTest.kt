package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.HousingStatus
import at.orchaldir.gm.core.model.character.InApartment
import at.orchaldir.gm.core.model.character.InHouse
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingPurpose
import at.orchaldir.gm.core.model.world.building.SingleFamilyHouse
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test


fun create(purpose: BuildingPurpose = SingleFamilyHouse, date: Date = Year(0)) =
    Building(BUILDING_ID_0, constructionDate = date, purpose = purpose)

private val IN_APARTMENT = InApartment(BUILDING_ID_0, 0)
private val IN_HOUSE = InHouse(BUILDING_ID_0)
private val STATE = State(
    listOf(
        Storage(listOf(create(), Building(BUILDING_ID_1))),
        Storage(CALENDAR0),
    )
)

class HousingStatusTest {

    @Test
    fun `Cannot use unknown building as home`() {
        val state = STATE.removeStorage(BUILDING_ID_0)

        assertIllegalArgument("The home doesn't exist!") {
            checkHousingStatusHistory(state, History(IN_HOUSE), DAY0)
        }
    }

    @Test
    fun `Cannot use unknown building as a previous home`() {
        val state = STATE.updateStorage(Storage(Building(BUILDING_ID_0)))
        val entry = HistoryEntry<HousingStatus>(InHouse(BUILDING_ID_1), DAY1)

        assertIllegalArgument("The 1.previous home doesn't exist!") {
            checkHousingStatusHistory(state, History(IN_HOUSE, entry), DAY0)
        }
    }

    @Test
    fun `Cannot use unknown building as apartment house`() {
        val state = STATE.removeStorage(BUILDING_ID_0)

        assertIllegalArgument("The home doesn't exist!") {
            checkHousingStatusHistory(state, History(IN_APARTMENT), DAY0)
        }
    }

    @Test
    fun `Living in an house requires a house`() {
        val state = STATE.updateStorage(Storage(create(ApartmentHouse(2))))

        assertIllegalArgument("The home is not a single family house!") {
            checkHousingStatusHistory(state, History(IN_HOUSE), DAY0)
        }
    }

    @Test
    fun `Living in an apartment requires an apartment house`() {
        assertIllegalState("The home is not an apartment house!") {
            checkHousingStatusHistory(STATE, History(IN_APARTMENT), DAY0)
        }
    }

    @Test
    fun `Cannot use an apartment number higher than the building allows`() {
        val state = STATE.updateStorage(Storage(create(ApartmentHouse(2))))

        assertIllegalArgument("The home's apartment index is too high!") {
            checkHousingStatusHistory(state, History(InApartment(BUILDING_ID_0, 2)), DAY0)
        }
    }

    @Test
    fun `The house doesn't exist yet`() {
        val state = STATE.updateStorage(Storage(create(date = DAY1)))

        assertIllegalArgument("The home doesn't exist yet!") {
            checkHousingStatusHistory(state, History(IN_HOUSE), DAY0)
        }
    }

    @Test
    fun `The apartment house doesn't exist yet`() {
        val state =
            STATE.updateStorage(Storage(create(ApartmentHouse(2), DAY1)))

        assertIllegalArgument("The home doesn't exist yet!") {
            checkHousingStatusHistory(state, History(IN_APARTMENT), DAY0)
        }
    }

    @Test
    fun `Live in a valid single family house`() {
        checkHousingStatusHistory(STATE, History(IN_HOUSE), DAY0)
    }

    @Test
    fun `Live in a valid apartment`() {
        val count = 3
        val state = STATE.updateStorage(Storage(create(ApartmentHouse(count))))

        repeat(count) {
            checkHousingStatusHistory(state, History(InApartment(BUILDING_ID_0, it)), DAY0)
        }
    }

}