package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateMountain
import at.orchaldir.gm.core.action.DeleteMountain
import at.orchaldir.gm.core.action.UpdateMountain
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.Mountain
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_MOUNTAIN: Reducer<CreateMountain, State> = { state, _ ->
    val moon = Mountain(state.getMountainStorage().nextId)

    noFollowUps(state.updateStorage(state.getMountainStorage().add(moon)))
}

val DELETE_MOUNTAIN: Reducer<DeleteMountain, State> = { state, action ->
    state.getMountainStorage().require(action.id)
    require(state.canDelete(action.id)) { "Mountain ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getMountainStorage().remove(action.id)))
}

val UPDATE_MOUNTAIN: Reducer<UpdateMountain, State> = { state, action ->
    val mountain = action.mountain
    state.getMountainStorage().require(mountain.id)

    validateMountain(state, mountain)

    noFollowUps(state.updateStorage(state.getMountainStorage().update(mountain)))
}

fun validateMountain(
    state: State,
    mountain: Mountain,
) {
    mountain.resources.forEach { state.getMaterialStorage().require(it) }
}