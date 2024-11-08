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
    creationDate: Date,
) = checkHistory(state, ownership, creationDate, "owner", ::checkLivingStatus)

private fun checkLivingStatus(
    state: State,
    livingStatus: LivingStatus,
    noun: String,
    start: Date,
) {
    when (livingStatus) {
        Homeless -> doNothing()
        is InApartment -> {
            val apartmentHouse = state.getBuildingStorage().getOrThrow(livingStatus.building)

            if (apartmentHouse.purpose is ApartmentHouse) {
                require(livingStatus.apartmentIndex < apartmentHouse.purpose.apartments) { "Apartment index is too high!" }
            } else {
                error("Living in an apartment requires an apartment house!")
            }
        }

        is InHouse -> state.getBuildingStorage().require(livingStatus.building)
    }
}