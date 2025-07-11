package at.orchaldir.gm.core.reducer.health

import at.orchaldir.gm.core.action.CreateDisease
import at.orchaldir.gm.core.action.DeleteDisease
import at.orchaldir.gm.core.action.UpdateDisease
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkOrigin
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.health.canDeleteDisease
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_DISEASE: Reducer<CreateDisease, State> = { state, _ ->
    val disease = Disease(state.getDiseaseStorage().nextId)

    noFollowUps(state.updateStorage(state.getDiseaseStorage().add(disease)))
}

val DELETE_DISEASE: Reducer<DeleteDisease, State> = { state, action ->
    state.getDiseaseStorage().require(action.id)
    validateCanDelete(state.canDeleteDisease(action.id), action.id)

    noFollowUps(state.updateStorage(state.getDiseaseStorage().remove(action.id)))
}

val UPDATE_DISEASE: Reducer<UpdateDisease, State> = { state, action ->
    val disease = action.disease
    state.getDiseaseStorage().require(disease.id)
    validateDisease(state, disease)

    noFollowUps(state.updateStorage(state.getDiseaseStorage().update(disease)))
}

fun validateDisease(state: State, disease: Disease) {
    checkDate(state, disease.startDate(), "Disease")
    checkOrigin(state, disease.id, disease.origin, disease.date)
    state.getDataSourceStorage().require(disease.sources)
}
