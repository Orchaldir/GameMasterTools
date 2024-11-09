package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.utils.doNothing

fun checkLivingStatusHistory(
    state: State,
    ownership: History<LivingStatus>,
    startDate: Date,
) = checkHistory(state, ownership, startDate, "owner", ::checkLivingStatus)

private fun checkLivingStatus(
    state: State,
    livingStatus: LivingStatus,
    noun: String,
    date: Date,
) {
    when (livingStatus) {
        Homeless -> doNothing()
        is InApartment -> {
            val building = state.getBuildingStorage().getOrThrow(livingStatus.building)

            if (building.purpose is ApartmentHouse) {
                require(livingStatus.apartmentIndex < building.purpose.apartments) { "Apartment index is too high!" }
            } else {
                error("Living in an apartment requires an apartment house!")
            }
        }

        is InHouse -> state.getBuildingStorage().require(livingStatus.building)
    }
}