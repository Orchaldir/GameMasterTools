package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateFashion
import at.orchaldir.gm.core.action.DeleteFashion
import at.orchaldir.gm.core.action.UpdateFashion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_FASHION: Reducer<CreateFashion, State> = { state, _ ->
    val nameList = Fashion(state.fashion.nextId)

    noFollowUps(state.copy(fashion = state.fashion.add(nameList)))
}

val DELETE_FASHION: Reducer<DeleteFashion, State> = { state, action ->
    state.fashion.require(action.id)
    //require(state.canDelete(action.id)) { "Name list ${action.id.value} is used" }

    noFollowUps(state.copy(fashion = state.fashion.remove(action.id)))
}

val UPDATE_FASHION: Reducer<UpdateFashion, State> = { state, action ->
    val fashion = action.nameList

    state.fashion.require(fashion.id)

    noFollowUps(state.copy(fashion = state.fashion.update(fashion)))
}
