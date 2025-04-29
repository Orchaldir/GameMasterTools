package at.orchaldir.gm.app.html.model.world

import at.orchaldir.gm.app.TILE
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.optionalField
import at.orchaldir.gm.app.html.selectOptionalText
import at.orchaldir.gm.app.html.parseOptionalInt
import at.orchaldir.gm.app.html.parseOptionalString
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.selector.getPlanarLanguages
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.world.getDemiplanes
import at.orchaldir.gm.core.selector.world.getMoons
import at.orchaldir.gm.core.selector.world.getPlanarAlignment
import at.orchaldir.gm.core.selector.world.getReflections
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPlane(
    call: ApplicationCall,
    state: State,
    plane: Plane,
) {
    val currentDate = state.getCurrentDate()

    optionalField("Title", plane.title)
    showPlanePurpose(call, state, plane.purpose)
    optionalField("Current Alignment", state.getPlanarAlignment(plane, currentDate))

    fieldList(call, state, "Demiplanes", state.getDemiplanes(plane.id))
    fieldList(call, state, "Reflection", state.getReflections(plane.id))
    fieldList(call, state, "Associated Moons", state.getMoons(plane.id))
    fieldList(call, state, "Associated Languages", state.getPlanarLanguages(plane.id))
}

// edit

fun HtmlBlockTag.editPlane(
    state: State,
    plane: Plane,
) {
    selectName(plane.name)
    selectOptionalText("Optional Title", plane.title, TILE)
    editPlanePurpose(state, plane)
}

// parse

fun parsePlaneId(parameters: Parameters, param: String) = parseOptionalPlaneId(parameters, param) ?: PlaneId(0)
fun parseOptionalPlaneId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { PlaneId(it) }

fun parsePlane(parameters: Parameters, id: PlaneId) = Plane(
    id,
    parseName(parameters),
    parseOptionalString(parameters, TILE),
    parsePlanePurpose(parameters),
)
