package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.CreateCatastrophe
import at.orchaldir.gm.core.action.DeleteCatastrophe
import at.orchaldir.gm.core.action.UpdateCatastrophe
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateHasStartAndEnd
import at.orchaldir.gm.core.selector.realm.canDeleteCatastrophe
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CATASTROPHE: Reducer<CreateCatastrophe, State> = { state, _ ->
    val catastrophe = Catastrophe(state.getCatastropheStorage().nextId)

    noFollowUps(state.updateStorage(state.getCatastropheStorage().add(catastrophe)))
}

val DELETE_CATASTROPHE: Reducer<DeleteCatastrophe, State> = { state, action ->
    state.getCatastropheStorage().require(action.id)

    validateCanDelete(state.canDeleteCatastrophe(action.id), action.id)

    noFollowUps(state.updateStorage(state.getCatastropheStorage().remove(action.id)))
}

val UPDATE_CATASTROPHE: Reducer<UpdateCatastrophe, State> = { state, action ->
    val catastrophe = action.catastrophe
    state.getCatastropheStorage().require(catastrophe.id)

    validateCatastrophe(state, catastrophe)

    noFollowUps(state.updateStorage(state.getCatastropheStorage().update(catastrophe)))
}

fun validateCatastrophe(state: State, catastrophe: Catastrophe) {
    validateHasStartAndEnd(state, catastrophe)
}
