package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.selector.util.requireExists

fun checkHousingStatusHistory(
    state: State,
    ownership: History<HousingStatus>,
    startDate: Date,
) = checkHistory(state, ownership, startDate, "home", ::checkHousingStatus)

private fun checkHousingStatus(
    state: State,
    status: HousingStatus,
    noun: String,
    date: Date?,
) {
    when (status) {
        UndefinedHousingStatus -> return
        Homeless -> return
        is InApartment -> {
            val building = state
                .requireExists(state.getBuildingStorage(), status.building, date) { noun }

            if (building.purpose is ApartmentHouse) {
                require(status.apartmentIndex < building.purpose.apartments) { "The $noun's apartment index is too high!" }
            } else {
                error("The $noun is not an apartment house!")
            }
        }

        is InHouse -> {
            val building = state
                .requireExists(state.getBuildingStorage(), status.building, date) { noun }

            require(building.purpose.isHome()) { "The $noun is not a home!" }
        }

        is InRealm -> state.requireExists(state.getRealmStorage(), status.realm, date) { noun }
        is InTown -> state.requireExists(state.getTownStorage(), status.town, date) { noun }
    }
}