package at.orchaldir.gm.app.html.util.color

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.SCHEME
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.core.selector.item.equipment.getEquipment
import at.orchaldir.gm.core.selector.util.getColorSchemeGroups
import at.orchaldir.gm.core.selector.util.sortColorSchemes
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showColorSchemeGroup(
    call: ApplicationCall,
    state: State,
    group: ColorSchemeGroup,
) {
    field("Name", group.name())
    fieldIds(call, state, group.schemes)
    showUsages(call, state, group.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    id: ColorSchemeGroupId,
) {
    val equipment = state.getEquipment(id)

    if (equipment.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, equipment)
}

// edit

fun HtmlBlockTag.editColorSchemeGroup(
    call: ApplicationCall,
    state: State,
    group: ColorSchemeGroup,
) {
    selectName(group.name)

    selectElements(
        state,
        "Schemes",
        SCHEME,
        state.sortColorSchemes(),
        group.schemes,
    )
}

// parse

fun parseColorSchemeGroupId(value: String) = ColorSchemeGroupId(value.toInt())
fun parseColorSchemeGroupId(parameters: Parameters, param: String) = ColorSchemeGroupId(parseInt(parameters, param))
fun parseOptionalColorSchemeGroupId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { ColorSchemeGroupId(it) }

fun parseColorSchemeGroup(
    state: State,
    parameters: Parameters,
    id: ColorSchemeGroupId,
) = ColorSchemeGroup(
    id,
    parseName(parameters),
    parseElements(parameters, SCHEME, ::parseColorSchemeId),
)
