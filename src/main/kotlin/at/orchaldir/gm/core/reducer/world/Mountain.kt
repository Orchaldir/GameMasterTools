package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateMountain
import at.orchaldir.gm.core.action.DeleteMountain
import at.orchaldir.gm.core.action.UpdateMountain
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.Mountain
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_MOUNTAIN: Reducer<CreateMountain, State> = { state, _ ->
    val moon = Mountain(state.getMountainStorage().nextId)

    noFollowUps(state.updateStorage(state.getMountainStorage().add(moon)))
}

val DELETE_MOUNTAIN: Reducer<DeleteMountain, State> = { state, action ->
    state.getMountainStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getMountainStorage().remove(action.id)))
}

val UPDATE_MOUNTAIN: Reducer<UpdateMountain, State> = { state, action ->
    noFollowUps(state.updateStorage(state.getMountainStorage().update(action.mountain)))
}