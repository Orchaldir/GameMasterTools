package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.FONT
import at.orchaldir.gm.app.INITIAL
import at.orchaldir.gm.app.POSITION
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.fieldFactor
import at.orchaldir.gm.app.html.model.font.editFontOption
import at.orchaldir.gm.app.html.model.font.parseFontOption
import at.orchaldir.gm.app.html.model.font.showFontOption
import at.orchaldir.gm.app.html.model.parseFactor
import at.orchaldir.gm.app.html.model.selectFactor
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

fun HtmlBlockTag.showInitial(
    call: ApplicationCall,
    state: State,
    initial: Initial,
) {
    showDetails("Initials") {
        field("Type", initial.getType())

        when (initial) {
            NormalInitial -> doNothing()
            is LargeInitial -> {
                fieldFactor("Size", initial.size)
                field("Position", initial.position)
            }

            is FontInitial -> {
                showFontOption(call, state, "Font", initial.fontOption)
                field("Position", initial.position)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editInitial(
    state: State,
    initial: Initial,
    param: String,
) {
    val param = combine(param, INITIAL)

    showDetails("Initial", true) {
        selectValue("Type", param, InitialType.entries, initial.getType(), true)

        when (initial) {
            NormalInitial -> doNothing()
            is LargeInitial -> {
                selectFactor(
                    "Size",
                    combine(param, SIZE),
                    initial.size,
                    MIN_INITIAL_SIZE,
                    MAX_INITIAL_SIZE,
                    update = true,
                )
                selectPosition(param, initial.position)
            }

            is FontInitial -> {
                editFontOption(state, "Font", initial.fontOption, combine(param, FONT))
                selectPosition(param, initial.position)
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
        true,
    )
}


// parse

fun parseInitial(parameters: Parameters, param: String): Initial {
    val param = combine(param, INITIAL)

    return when (parse(parameters, param, InitialType.Normal)) {
        InitialType.Normal -> NormalInitial
        InitialType.Large -> LargeInitial(
            parseFactor(parameters, combine(param, SIZE), DEFAULT_INITIAL_SIZE),
            parsePosition(parameters, param),
        )

        InitialType.Font -> FontInitial(
            parseFontOption(parameters, combine(param, FONT)),
            parsePosition(parameters, param),
        )
    }
}

private fun parsePosition(
    parameters: Parameters,
    param: String,
): InitialPosition = parse(parameters, combine(param, POSITION), InitialPosition.DropCap)
