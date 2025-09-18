package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.LANGUAGES
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.parseLanguageId
import at.orchaldir.gm.app.html.util.showLocalElements
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.world.getDemiplanes
import at.orchaldir.gm.core.selector.world.getMoonsLinkedTo
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
    fieldIdList(call, state, plane.languages)

    fieldElements(call, state, "Demiplanes", state.getDemiplanes(plane.id))
    fieldElements(call, state, "Reflection", state.getReflections(plane.id))
    fieldElements(call, state, "Associated Moons", state.getMoonsLinkedTo(plane.id))
    showLocalElements(call, state, plane.id)
    showDataSources(call, state, plane.sources)
}

// edit

fun HtmlBlockTag.editPlane(
    state: State,
    plane: Plane,
) {
    selectName(plane.name)
    selectOptionalNotEmptyString("Optional Title", plane.title, TITLE)
    editPlanePurpose(state, plane)
    selectElements(
        state,
        "Languages",
        LANGUAGES,
        state.getLanguageStorage().getAll(),
        plane.languages,
    )
    editDataSources(state, plane.sources)
}

// parse

fun parsePlaneId(parameters: Parameters, param: String) = parseOptionalPlaneId(parameters, param) ?: PlaneId(0)
fun parseOptionalPlaneId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { PlaneId(it) }

fun parsePlane(parameters: Parameters, id: PlaneId) = Plane(
    id,
    parseName(parameters),
    parseOptionalNotEmptyString(parameters, TITLE),
    parsePlanePurpose(parameters),
    parseElements(parameters, LANGUAGES, ::parseLanguageId),
    parseDataSources(parameters),
)
