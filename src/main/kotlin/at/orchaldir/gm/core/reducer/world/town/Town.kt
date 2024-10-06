package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.action.CreateTown
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TOWN: Reducer<CreateTown, State> = { state, _ ->
    val town = Town(state.getTownStorage().nextId, foundingDate = state.time.currentDate)

    noFollowUps(state.updateStorage(state.getTownStorage().add(town)))
}

val DELETE_TOWN: Reducer<DeleteTown, State> = { state, action ->
    state.getTownStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getTownStorage().remove(action.id)))
}

val UPDATE_TOWN: Reducer<UpdateTown, State> = { state, action ->
    state.getTownStorage().require(action.town.id)

    noFollowUps(state.updateStorage(state.getTownStorage().update(action.town)))
}
