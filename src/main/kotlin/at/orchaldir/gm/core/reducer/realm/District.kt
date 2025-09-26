package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.UpdateDistrict
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.reducer.util.validatePopulation
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_DISTRICT: Reducer<UpdateDistrict, State> = { state, action ->
    val district = action.district
    state.getDistrictStorage().require(district.id)

    validateDistrict(state, district)

    noFollowUps(state.updateStorage(state.getDistrictStorage().update(district)))
}

fun validateDistrict(state: State, district: District) {
    state.getTownStorage().requireOptional(district.town)
    validateCreator(state, district.founder, district.id, district.foundingDate, "founder")
    validatePopulation(state, district.population)
}
