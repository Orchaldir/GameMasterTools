package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.END
import at.orchaldir.gm.app.REVIVAL
import at.orchaldir.gm.app.START
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.optionalField
import at.orchaldir.gm.app.html.util.parseOptionalDate
import at.orchaldir.gm.app.html.util.selectOptionalDate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.core.selector.world.getEarliestBuilding
import at.orchaldir.gm.core.selector.world.getPossibleStylesForRevival
import at.orchaldir.gm.core.selector.world.getRevivedBy
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showArchitecturalStyle(
    call: ApplicationCall,
    state: State,
    style: ArchitecturalStyle,
) {
    optionalField(call, state, "Start", style.start)
    optionalField(call, state, "End", style.end)
    if (style.revival != null) {
        fieldLink("Revival of", call, state, style.revival)
    }
    fieldElements(call, state, "Revived by", state.getRevivedBy(style.id))
    fieldElements(call, state, state.getBuildings(style.id))
}

// edit

fun HtmlBlockTag.editArchitecturalStyle(
    call: ApplicationCall,
    state: State,
    style: ArchitecturalStyle,
) {
    val minDate = state.getEarliestBuilding(state.getBuildings(style.id))?.constructionDate
    selectName(style.name)
    selectOptionalDate(state, "Start", style.start, START, minDate)
    selectOptionalDate(state, "End", style.end, END, style.start)
    selectOptionalElement(
        state,
        "Revival Of",
        REVIVAL,
        state.getPossibleStylesForRevival(style),
        style.revival,
    )
}

// parse

fun parseArchitecturalStyleId(parameters: Parameters, param: String) = ArchitecturalStyleId(parseInt(parameters, param))

fun parseOptionalArchitecturalStyleId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { ArchitecturalStyleId(it) }

fun parseArchitecturalStyle(state: State, parameters: Parameters, id: ArchitecturalStyleId) = ArchitecturalStyle(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, START),
    parseOptionalDate(parameters, state, END),
    parseOptionalArchitecturalStyleId(parameters, REVIVAL),
)
