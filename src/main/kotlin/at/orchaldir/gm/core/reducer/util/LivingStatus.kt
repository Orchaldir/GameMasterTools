package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.model.world.building.SingleFamilyHouse
import at.orchaldir.gm.utils.doNothing

fun checkLivingStatusHistory(
    state: State,
    ownership: History<LivingStatus>,
    startDate: Date,
) = checkHistory(state, ownership, startDate, "home", ::checkLivingStatus)

private fun checkLivingStatus(
    state: State,
    livingStatus: LivingStatus,
    noun: String,
    date: Date,
) {
    when (livingStatus) {
        Homeless -> doNothing()
        is InApartment -> {
            val building = state.getBuildingStorage().getOrThrow(livingStatus.building) { "The $noun doesn't exist!" }

            if (building.purpose is ApartmentHouse) {
                require(livingStatus.apartmentIndex < building.purpose.apartments) { "The $noun's apartment index is too high!" }
            } else {
                error("The $noun is not an apartment house!")
            }
        }

        is InHouse -> {
            val building = state.getBuildingStorage().getOrThrow(livingStatus.building) { "The $noun doesn't exist!" }

            require(building.purpose is SingleFamilyHouse) { "The $noun is not a single family house!" }
        }
    }
}