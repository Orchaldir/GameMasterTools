package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.selector.util.exists

fun checkHousingStatusHistory(
    state: State,
    ownership: History<HousingStatus>,
    startDate: Date,
) = checkHistory(state, ownership, startDate, "home", ::checkHousingStatus)

private fun checkHousingStatus(
    state: State,
    housingStatus: HousingStatus,
    noun: String,
    date: Date,
) {
    val building = when (housingStatus) {
        UndefinedHousingStatus -> return
        Homeless -> return
        is InApartment -> {
            val building = state.getBuildingStorage().getOrThrow(housingStatus.building) { "The $noun doesn't exist!" }

            if (building.purpose is ApartmentHouse) {
                require(housingStatus.apartmentIndex < building.purpose.apartments) { "The $noun's apartment index is too high!" }
            } else {
                error("The $noun is not an apartment house!")
            }

            building
        }

        is InHouse -> {
            val building = state.getBuildingStorage().getOrThrow(housingStatus.building) { "The $noun doesn't exist!" }

            require(building.purpose.isHome()) { "The $noun is not a home!" }

            building
        }
    }

    require(state.exists(building, date)) { "The $noun doesn't exist yet!" }
}