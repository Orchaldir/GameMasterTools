package at.orchaldir.gm.app.html.util.color

import at.orchaldir.gm.app.GROUP
import at.orchaldir.gm.app.SCHEME
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.core.selector.util.sortColorSchemeGroups
import at.orchaldir.gm.core.selector.util.sortColorSchemes
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldColorSchemeOption(
    call: ApplicationCall,
    state: State,
    option: ColorSchemeOption,
) {
    field("Color Schemes") {
        showColorSchemeOption(call, state, option)
    }
}

fun HtmlBlockTag.showColorSchemeOption(
    call: ApplicationCall,
    state: State,
    option: ColorSchemeOption,
) = when (option) {
    NoColorSchemes -> "None"
    is UseColorSchemeGroup -> link(call, state, option.group)
    is UseColorSchemes -> showInlineIds(call, state, option.schemes)
}

// edit


fun HtmlBlockTag.editColorSchemeOption(
    state: State,
    option: ColorSchemeOption,
    param: String,
) {
    val groups = state.sortColorSchemeGroups()
    val schemes = state.sortColorSchemes()

    showDetails("Color Schemes", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            ColorSchemeOptionType.entries,
            option.getType(),
        ) { type ->
            when (type) {
                ColorSchemeOptionType.Group -> groups.isEmpty()
                ColorSchemeOptionType.None -> false
                ColorSchemeOptionType.Schemes -> schemes.isEmpty()
            }
        }

        when (option) {
            NoColorSchemes -> doNothing()
            is UseColorSchemeGroup -> selectElement(
                state,
                combine(param, GROUP),
                groups,
                option.group,
            )

            is UseColorSchemes -> selectElements(
                state,
                combine(param, SCHEME),
                schemes,
                option.schemes,
            )
        }
    }
}

// parse

fun parseColorSchemeOption(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, combine(param, TYPE), ColorSchemeOptionType.None)) {
    ColorSchemeOptionType.Group -> UseColorSchemeGroup(
        parseColorSchemeGroupId(parameters, combine(param, GROUP)),
    )

    ColorSchemeOptionType.None -> NoColorSchemes
    ColorSchemeOptionType.Schemes -> UseColorSchemes(
        parseElements(parameters, combine(param, SCHEME), ::parseColorSchemeId),
    )
}
