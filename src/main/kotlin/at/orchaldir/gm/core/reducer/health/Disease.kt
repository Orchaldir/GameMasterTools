package at.orchaldir.gm.core.reducer.health

import at.orchaldir.gm.core.action.CreateDisease
import at.orchaldir.gm.core.action.DeleteDisease
import at.orchaldir.gm.core.action.UpdateDisease
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.health.CreatedDisease
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.health.DiseaseId
import at.orchaldir.gm.core.model.health.EvolvedDisease
import at.orchaldir.gm.core.model.health.ModifiedDisease
import at.orchaldir.gm.core.model.health.OriginalDisease
import at.orchaldir.gm.core.model.health.UndefinedDiseaseOrigin
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.health.canDeleteDisease
import at.orchaldir.gm.utils.doNothing
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
    checkOrigin(state, disease)
    state.getDataSourceStorage().require(disease.sources)
}

private fun checkOrigin(state: State, disease: Disease) {
    when (val origin = disease.origin) {
        is CreatedDisease -> checkInventor(state, disease, origin.creator)
        is ModifiedDisease -> checkOrigin(state, disease, origin.modifier, origin.parent)
        is EvolvedDisease -> checkParent(state, origin.parent)
        OriginalDisease -> doNothing()
        UndefinedDiseaseOrigin -> doNothing()
    }
}

private fun checkOrigin(
    state: State,
    disease: Disease,
    creator: Creator,
    parent: DiseaseId,
) {
    checkInventor(state, disease, creator)
    checkParent(state, parent)
}

private fun checkInventor(
    state: State,
    disease: Disease,
    creator: Creator,
) {
    validateCreator(state, creator, disease.id, disease.date, "Inventor")
}

private fun checkParent(state: State, parent: DiseaseId) {
    state.getDiseaseStorage().require(parent) { "Parent disease ${parent.value} is unknown!" }
}
