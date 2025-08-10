package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.CreateDistrict
import at.orchaldir.gm.core.action.DeleteDistrict
import at.orchaldir.gm.core.action.UpdateDistrict
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.reducer.util.validatePopulation
import at.orchaldir.gm.core.selector.realm.canDeleteDistrict
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_DISTRICT: Reducer<CreateDistrict, State> = { state, _ ->
    val district = District(state.getDistrictStorage().nextId)

    validateDistrict(state, district)

    noFollowUps(state.updateStorage(state.getDistrictStorage().add(district)))
}

val DELETE_DISTRICT: Reducer<DeleteDistrict, State> = { state, action ->
    state.getDistrictStorage().require(action.id)

    validateCanDelete(state.canDeleteDistrict(action.id), action.id)

    noFollowUps(state.updateStorage(state.getDistrictStorage().remove(action.id)))
}

val UPDATE_DISTRICT: Reducer<UpdateDistrict, State> = { state, action ->
    val district = action.district
    state.getDistrictStorage().require(district.id)

    validateDistrict(state, district)

    noFollowUps(state.updateStorage(state.getDistrictStorage().update(district)))
}

fun validateDistrict(state: State, district: District) {
    state.getTownStorage().require(district.town)
    validateCreator(state, district.founder, district.id, district.foundingDate, "founder")
    validatePopulation(state, district.population)
}
