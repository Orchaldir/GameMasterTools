package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.GOD
import at.orchaldir.gm.app.PLANE
import at.orchaldir.gm.app.PURPOSE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.religion.parseGodId
import at.orchaldir.gm.app.html.util.fieldReference
import at.orchaldir.gm.app.html.util.parseCreator
import at.orchaldir.gm.app.html.util.selectCreator
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.plane.*
import at.orchaldir.gm.core.model.world.plane.PlanePurposeType.*
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
    when (purpose) {
        is IndependentPlane -> {
            showPlaneAlignmentPattern(purpose.pattern)
        }

        is PrisonPlane -> {
            fieldReference(call, state, purpose.creator, "Creator")
        }

        else -> doNothing()
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

        is PrisonPlane -> {
            +"Prison of "

            showInlineList(purpose.gods) { god ->
                link(call, state, god)
            }
        }

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
    val otherPlanes = state.getPlaneStorage().getAllExcept(plane.id)
    val gods = state.getGodStorage().getAll()

    showDetails("Purpose", true) {
        selectValue("Type", PURPOSE, PlanePurposeType.entries, plane.purpose.getType()) {
            when (it) {
                Demi, Reflective -> otherPlanes.isEmpty()
                Heart -> gods.isEmpty()
                else -> false
            }
        }
        when (val purpose = plane.purpose) {
            is Demiplane -> selectOtherPlane(state, otherPlanes, purpose.plane)
            is HeartPlane -> selectElement(state, combine(PURPOSE, GOD), gods, purpose.god)
            is IndependentPlane -> editPlaneAlignmentPattern(purpose.pattern)
            MaterialPlane -> doNothing()
            is PrisonPlane -> {
                selectElements(state, "Gods", combine(PURPOSE, GOD), gods, purpose.gods)
                selectCreator(state, purpose.creator, plane.id, null)
            }

            is ReflectivePlane -> selectOtherPlane(state, otherPlanes, purpose.plane)
        }
    }
}

private fun DETAILS.selectOtherPlane(
    state: State,
    otherPlanes: List<Plane>,
    plane: PlaneId,
) {
    selectElement(state, combine(PURPOSE, PLANE), otherPlanes, plane)
}

// parse

fun parsePlanePurpose(parameters: Parameters) = when (parse(parameters, PURPOSE, Independent)) {
    Independent -> IndependentPlane(parsePlaneAlignmentPattern(parameters))
    Demi -> Demiplane(parsePlaneId(parameters, combine(PURPOSE, PLANE)))
    Heart -> HeartPlane(parseGodId(parameters, combine(PURPOSE, GOD)))
    Material -> MaterialPlane
    Prison -> PrisonPlane(
        parseElements(parameters, combine(PURPOSE, GOD), ::parseGodId),
        parseCreator(parameters),
    )

    Reflective -> ReflectivePlane(parsePlaneId(parameters, combine(PURPOSE, PLANE)))
}