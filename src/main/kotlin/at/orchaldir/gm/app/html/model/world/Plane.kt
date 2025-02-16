package at.orchaldir.gm.app.html.model.world

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.TILE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.selector.world.getMoons
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPlane(
    call: ApplicationCall,
    state: State,
    plane: Plane,
) {
    optionalField("Title", plane.title)
    showPlanePurpose(call, state, plane.purpose)
    showList("Associated Moons", state.getMoons(plane.id)) { moon ->
        link(call, moon)
    }
}

// edit

fun HtmlBlockTag.editPlane(
    state: State,
    plane: Plane,
) {
    selectName(plane.name)
    selectText("Optional Name", plane.title ?: "", TILE, 0)
    editPlanePurpose(state, plane)
}

// parse

fun parsePlaneId(parameters: Parameters, param: String) = PlaneId(parseInt(parameters, param))

fun parsePlaneId(value: String) = PlaneId(value.toInt())

fun parsePlane(parameters: Parameters, id: PlaneId) = Plane(
    id,
    parameters.getOrFail(NAME),
    parameters[TILE]?.ifEmpty { null },
    parsePlanePurpose(parameters),
)
