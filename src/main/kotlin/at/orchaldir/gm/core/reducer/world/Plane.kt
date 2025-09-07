package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreatePlane
import at.orchaldir.gm.core.action.DeletePlane
import at.orchaldir.gm.core.action.UpdatePlane
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.plane.*
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.world.canDeletePlane
import at.orchaldir.gm.core.selector.world.getHeartPlane
import at.orchaldir.gm.core.selector.world.getPrisonPlane
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_PLANE: Reducer<CreatePlane, State> = { state, _ ->
    val plane = Plane(state.getPlaneStorage().nextId)

    noFollowUps(state.updateStorage(state.getPlaneStorage().add(plane)))
}

val UPDATE_PLANE: Reducer<UpdatePlane, State> = { state, action ->
    val plane = action.plane

    state.getPlaneStorage().require(plane.id)
    validatePlane(state, plane)

    noFollowUps(state.updateStorage(state.getPlaneStorage().update(plane)))
}

fun validatePlane(state: State, plane: Plane) {
    checkPurpose(state, plane)
    state.getLanguageStorage().require(plane.languages)
    state.getDataSourceStorage().require(plane.sources)
}

private fun checkPurpose(state: State, plane: Plane) {
    when (val purpose = plane.purpose) {
        is Demiplane -> state.getPlaneStorage().require(purpose.plane)
        is IndependentPlane -> doNothing()
        is ReflectivePlane -> state.getPlaneStorage().require(purpose.plane)
        MaterialPlane -> doNothing()

        is HeartPlane -> {
            state.getGodStorage().require(purpose.god)
            val heartPlane = state.getHeartPlane(purpose.god)
            require(heartPlane == null || heartPlane.id == plane.id) { "God ${purpose.god.value} already has a heart plane!" }
        }

        is PrisonPlane -> purpose.gods.forEach { god ->
            state.getGodStorage().require(god)
            val prisonPlane = state.getPrisonPlane(god)
            require(prisonPlane == null || prisonPlane.id == plane.id) { "God ${god.value} already has a prison plane!" }
        }
    }
}