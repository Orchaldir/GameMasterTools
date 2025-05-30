package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPolearmFixation(
    call: ApplicationCall,
    state: State,
    fixation: PolearmFixation,
) {
    showDetails("Fixation") {
        field("Type", fixation.getType())

        when (fixation) {
            NoPolearmFixation -> doNothing()
            is BoundPolearmHead -> {
                fieldFactor("Length", fixation.length)
                showColorSchemeItemPart(call, state, fixation.part)
            }

            is Langets -> {
                fieldFactor("Length", fixation.length)
                showColorSchemeItemPart(call, state, fixation.part)
            }

            is SocketedPolearmHead -> {
                fieldFactor("Length", fixation.length)
                showColorSchemeItemPart(call, state, fixation.part)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editPolearmFixation(
    state: State,
    fixation: PolearmFixation,
    param: String,
) {
    showDetails("Fixation", true) {
        selectValue("Type", param, PolearmFixationType.entries, fixation.getType())

        when (fixation) {
            NoPolearmFixation -> doNothing()
            is BoundPolearmHead -> {
                selectLength(param, fixation.length)
                editColorSchemeItemPart(state, fixation.part, param)
            }

            is Langets -> {
                selectLength(param, fixation.length, MIN_LANGETS_LENGTH, MAX_LANGETS_LENGTH)
                editColorSchemeItemPart(state, fixation.part, param)
            }

            is SocketedPolearmHead -> {
                selectLength(param, fixation.length)
                editColorSchemeItemPart(state, fixation.part, param)
            }
        }
    }
}

private fun DETAILS.selectLength(
    param: String,
    length: Factor,
    min: Factor = MIN_FIXATION_LENGTH,
    max: Factor = MAX_FIXATION_LENGTH,
) {
    selectFactor(
        "Length",
        combine(param, LENGTH),
        length,
        min,
        max,
    )
}

// parse

fun parsePolearmFixation(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, param, PolearmFixationType.None)) {
    PolearmFixationType.None -> NoPolearmFixation
    PolearmFixationType.Bound -> BoundPolearmHead(
        parseLength(parameters, param),
        parseColorSchemeItemPart(parameters, param),
    )

    PolearmFixationType.Langets -> Langets(
        parseLength(parameters, param, DEFAULT_LANGETS_LENGTH),
        parseColorSchemeItemPart(parameters, param),
    )

    PolearmFixationType.Socketed -> SocketedPolearmHead(
        parseLength(parameters, param),
        parseColorSchemeItemPart(parameters, param),
    )
}

private fun parseLength(
    parameters: Parameters,
    param: String,
    default: Factor = DEFAULT_FIXATION_LENGTH,
) = parseFactor(parameters, combine(param, LENGTH), default)