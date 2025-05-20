package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.FONT
import at.orchaldir.gm.app.INITIAL
import at.orchaldir.gm.app.POSITION
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.font.editFontOption
import at.orchaldir.gm.app.html.util.font.parseFontOption
import at.orchaldir.gm.app.html.util.font.showFontOption
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag


// show

fun HtmlBlockTag.showInitials(
    call: ApplicationCall,
    state: State,
    initials: Initials,
) {
    showDetails("Initials") {
        field("Type", initials.getType())

        when (initials) {
            NormalInitials -> doNothing()
            is LargeInitials -> {
                fieldFactor("Size", initials.size)
                field("Position", initials.position)
            }

            is FontInitials -> {
                showFontOption(call, state, "Font", initials.fontOption)
                field("Position", initials.position)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editInitials(
    state: State,
    initials: Initials,
    param: String,
) {
    val param = combine(param, INITIAL)

    showDetails("Initials", true) {
        selectValue("Type", param, InitialsType.entries, initials.getType())

        when (initials) {
            NormalInitials -> doNothing()
            is LargeInitials -> {
                selectFactor(
                    "Size",
                    combine(param, SIZE),
                    initials.size,
                    MIN_INITIAL_SIZE,
                    MAX_INITIAL_SIZE,
                )
                selectPosition(param, initials.position)
            }

            is FontInitials -> {
                editFontOption(state, "Font", initials.fontOption, combine(param, FONT))
                selectPosition(param, initials.position)
            }
        }
    }
}

private fun DETAILS.selectPosition(
    param: String,
    position: InitialPosition,
) {
    selectValue(
        "Position",
        combine(param, POSITION),
        InitialPosition.entries,
        position,
    )
}


// parse

fun parseInitials(parameters: Parameters, param: String): Initials {
    val param = combine(param, INITIAL)

    return when (parse(parameters, param, InitialsType.Normal)) {
        InitialsType.Normal -> NormalInitials
        InitialsType.Large -> LargeInitials(
            parseFactor(parameters, combine(param, SIZE), DEFAULT_INITIAL_SIZE),
            parsePosition(parameters, param),
        )

        InitialsType.Font -> FontInitials(
            parseFontOption(parameters, combine(param, FONT)),
            parsePosition(parameters, param),
        )
    }
}

private fun parsePosition(
    parameters: Parameters,
    param: String,
): InitialPosition = parse(parameters, combine(param, POSITION), InitialPosition.DropCap)
