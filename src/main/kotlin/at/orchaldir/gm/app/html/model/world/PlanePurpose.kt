package at.orchaldir.gm.app.html.model.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.religion.parseGodId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.plane.*
import at.orchaldir.gm.core.model.world.plane.PlanePurposeType.*
import at.orchaldir.gm.core.selector.util.sortGods
import at.orchaldir.gm.core.selector.util.sortPlanes
import at.orchaldir.gm.core.selector.world.getPlanarAlignments
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPlanePurpose(
    call: ApplicationCall,
    state: State,
    purpose: PlanePurpose,
) {
    field("Purpose") {
        displayPlanePurpose(call, state, purpose)
    }
    if (purpose is IndependentPlane) {
        showPlaneAlignmentPattern(purpose.pattern)
    }
}

fun HtmlBlockTag.displayPlanePurpose(
    call: ApplicationCall,
    state: State,
    purpose: PlanePurpose,
    displayIndependent: Boolean = true,
) {
    when (purpose) {
        is Demiplane -> {
            +"Demiplane of "
            link(call, state, purpose.plane)
        }

        is HeartPlane -> {
            +"Heart of "
            link(call, state, purpose.god)
        }

        is IndependentPlane -> if (displayIndependent) {
            +"Independent"
        }

        MaterialPlane -> +"Material Plane"

        is ReflectivePlane -> {
            +"Refection of "
            link(call, state, purpose.plane)
        }

    }
}

// edit

fun HtmlBlockTag.editPlanePurpose(
    state: State,
    plane: Plane,
) {
    val otherPlanes = state.sortPlanes(state.getPlaneStorage().getAllExcept(plane.id))
    val gods = state.sortGods()

    showDetails("Purpose", true) {
        selectValue("Type", PURPOSE, PlanePurposeType.entries, plane.purpose.getType(), true) {
            when (it) {
                Demi, Reflective -> otherPlanes.isEmpty()
                Heart -> gods.isEmpty()
                else -> false
            }
        }
        when (val purpose = plane.purpose) {
            is Demiplane -> selectOtherPlane(state, otherPlanes, purpose.plane)
            is HeartPlane -> selectElement(state, "God", combine(PURPOSE, GOD), gods, purpose.god)
            is IndependentPlane -> editPlaneAlignmentPattern(purpose.pattern)
            MaterialPlane -> doNothing()
            is ReflectivePlane -> selectOtherPlane(state, otherPlanes, purpose.plane)
        }
    }
}

private fun DETAILS.selectOtherPlane(
    state: State,
    otherPlanes: List<Plane>,
    plane: PlaneId,
) {
    selectElement(state, "Plane", combine(PURPOSE, PLANE), otherPlanes, plane)
}

// parse

fun parsePlanePurpose(parameters: Parameters) = when (parse(parameters, PURPOSE, Independent)) {
    Independent -> IndependentPlane(parsePlaneAlignmentPattern(parameters))
    Demi -> Demiplane(parsePlaneId(parameters, combine(PURPOSE, PLANE)))
    Heart -> HeartPlane(parseGodId(parameters, combine(PURPOSE, GOD)))
    Material -> MaterialPlane
    Reflective -> ReflectivePlane(parsePlaneId(parameters, combine(PURPOSE, PLANE)))
}