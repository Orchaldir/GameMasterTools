package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreatePlane
import at.orchaldir.gm.core.action.DeletePlane
import at.orchaldir.gm.core.action.UpdatePlane
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_PLANE: Reducer<CreatePlane, State> = { state, _ ->
    val plane = Plane(state.getPlaneStorage().nextId)

    noFollowUps(state.updateStorage(state.getPlaneStorage().add(plane)))
}

val DELETE_PLANE: Reducer<DeletePlane, State> = { state, action ->
    state.getPlaneStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getPlaneStorage().remove(action.id)))
}

val UPDATE_PLANE: Reducer<UpdatePlane, State> = { state, action ->
    val plane = action.plane

    state.getPlaneStorage().require(plane.id)

    noFollowUps(state.updateStorage(state.getPlaneStorage().update(plane)))
}