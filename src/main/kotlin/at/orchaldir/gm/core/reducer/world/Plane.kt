package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.plane.*
import at.orchaldir.gm.core.selector.world.getHeartPlane
import at.orchaldir.gm.core.selector.world.getPrisonPlane
import at.orchaldir.gm.utils.doNothing

fun checkPlanePurpose(state: State, plane: Plane) {
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