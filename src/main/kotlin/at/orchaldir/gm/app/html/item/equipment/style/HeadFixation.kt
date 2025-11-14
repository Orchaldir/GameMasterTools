package at.orchaldir.gm.app.html.item.equipment.style

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

fun HtmlBlockTag.showHeadFixation(
    call: ApplicationCall,
    state: State,
    fixation: HeadFixation,
) {
    showDetails("Fixation") {
        field("Type", fixation.getType())

        when (fixation) {
            NoHeadFixation -> doNothing()
            is BoundHeadHead -> {
                fieldFactor("Length", fixation.length)
                showColorSchemeItemPart(call, state, fixation.part)
            }

            is Langets -> {
                fieldFactor("Length", fixation.length)
                showColorSchemeItemPart(call, state, fixation.part)
            }

            is SocketedHeadHead -> {
                fieldFactor("Length", fixation.length)
                showColorSchemeItemPart(call, state, fixation.part)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editHeadFixation(
    state: State,
    fixation: HeadFixation,
    param: String,
) {
    showDetails("Fixation", true) {
        selectValue("Type", param, HeadFixationType.entries, fixation.getType())

        when (fixation) {
            NoHeadFixation -> doNothing()
            is BoundHeadHead -> {
                selectLength(param, fixation.length)
                editColorSchemeItemPart(state, fixation.part, param)
            }

            is Langets -> {
                selectLength(param, fixation.length, MIN_LANGETS_LENGTH, MAX_LANGETS_LENGTH)
                editColorSchemeItemPart(state, fixation.part, param)
            }

            is SocketedHeadHead -> {
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

fun parseHeadFixation(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, param, HeadFixationType.None)) {
    HeadFixationType.None -> NoHeadFixation
    HeadFixationType.Bound -> BoundHeadHead(
        parseLength(parameters, param),
        parseColorSchemeItemPart(parameters, param),
    )

    HeadFixationType.Langets -> Langets(
        parseLength(parameters, param, DEFAULT_LANGETS_LENGTH),
        parseColorSchemeItemPart(parameters, param),
    )

    HeadFixationType.Socketed -> SocketedHeadHead(
        parseLength(parameters, param),
        parseColorSchemeItemPart(parameters, param),
    )
}

private fun parseLength(
    parameters: Parameters,
    param: String,
    default: Factor = DEFAULT_FIXATION_LENGTH,
) = parseFactor(parameters, combine(param, LENGTH), default)