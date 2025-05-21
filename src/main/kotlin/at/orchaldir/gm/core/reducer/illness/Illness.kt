package at.orchaldir.gm.core.reducer.illness

import at.orchaldir.gm.core.action.CreateIllness
import at.orchaldir.gm.core.action.DeleteIllness
import at.orchaldir.gm.core.action.UpdateIllness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.illness.*
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkOrigin
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.illness.canDeleteIllness
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ILLNESS: Reducer<CreateIllness, State> = { state, _ ->
    val illness = Illness(state.getIllnessStorage().nextId)

    noFollowUps(state.updateStorage(state.getIllnessStorage().add(illness)))
}

val DELETE_ILLNESS: Reducer<DeleteIllness, State> = { state, action ->
    state.getIllnessStorage().require(action.id)

    validateCanDelete(state.canDeleteIllness(action.id), action.id, "it is used")

    noFollowUps(state.updateStorage(state.getIllnessStorage().remove(action.id)))
}

val UPDATE_ILLNESS: Reducer<UpdateIllness, State> = { state, action ->
    val illness = action.illness

    state.getIllnessStorage().require(illness.id)
    validateIllness(state, illness)

    noFollowUps(state.updateStorage(state.getIllnessStorage().update(illness)))
}

fun validateIllness(state: State, illness: Illness) {
    checkDate(state, illness.startDate(), "Illness")
    checkOrigin(state, state.getIllnessStorage(), illness.id, illness.origin)
}