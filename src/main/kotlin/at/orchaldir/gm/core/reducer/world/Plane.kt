package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreatePlane
import at.orchaldir.gm.core.action.DeletePlane
import at.orchaldir.gm.core.action.UpdatePlane
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.plane.Demiplane
import at.orchaldir.gm.core.model.world.plane.HeartPlane
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.ReflectivePlane
import at.orchaldir.gm.core.selector.world.canDeletePlane
import at.orchaldir.gm.core.selector.world.getHeartPlane
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_PLANE: Reducer<CreatePlane, State> = { state, _ ->
    val plane = Plane(state.getPlaneStorage().nextId)

    noFollowUps(state.updateStorage(state.getPlaneStorage().add(plane)))
}

val DELETE_PLANE: Reducer<DeletePlane, State> = { state, action ->
    state.getPlaneStorage().require(action.id)

    require(state.canDeletePlane(action.id)) { "Plane ${action.id.value} is used!" }

    noFollowUps(state.updateStorage(state.getPlaneStorage().remove(action.id)))
}

val UPDATE_PLANE: Reducer<UpdatePlane, State> = { state, action ->
    val plane = action.plane

    state.getPlaneStorage().require(plane.id)
    checkPurpose(state, plane)

    noFollowUps(state.updateStorage(state.getPlaneStorage().update(plane)))
}

private fun checkPurpose(state: State, plane: Plane) {
    when (val purpose = plane.purpose) {
        is Demiplane -> state.getPlaneStorage().require(purpose.plane)
        is HeartPlane -> {
            state.getGodStorage().require(purpose.god)
            val heartPlane = state.getHeartPlane(purpose.god)
            require(heartPlane == null || heartPlane.id == plane.id) { "God ${purpose.god.value} already has a heart plane!" }
        }

        is ReflectivePlane -> state.getPlaneStorage().require(purpose.plane)
        else -> doNothing()
    }
}