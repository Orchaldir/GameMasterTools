package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.part.*
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPolearmHead(
    call: ApplicationCall,
    state: State,
    head: PolearmHead,
    label: String,
) {
    showDetails(label) {
        field("Type", head.getType())

        when (head) {
            NoPolearmHead -> doNothing()
            RoundedPolearmHead -> doNothing()
            SharpenedPolearmHead -> doNothing()
            is PolearmHeadWithSegments -> showSegments(call, state, head.segments)
            is PolearmHeadWithAxeHead -> {
                showAxeHead(call, state, head.axe)
                showPolearmFixation(call, state, head.fixation)
            }

            is PolearmHeadWithSpearHead -> {
                showSpearHead(call, state, head.spear)
                showPolearmFixation(call, state, head.fixation)
            }

        }
    }
}

private fun DETAILS.showSpearHead(
    call: ApplicationCall,
    state: State,
    head: SpearHead,
) {
    field("Shape", head.shape)
    fieldFactor("Length", head.length)
    fieldFactor("Width", head.width)
    showColorSchemeItemPart(call, state, head.part, "Spear")
}

// edit

fun FORM.editPolearmHead(
    state: State,
    head: PolearmHead,
    param: String,
    label: String,
) {
    showDetails(label, true) {
        selectValue("Type", param, PolearmHeadType.entries, head.getType())

        when (head) {
            NoPolearmHead -> doNothing()
            RoundedPolearmHead -> doNothing()
            SharpenedPolearmHead -> doNothing()
            is PolearmHeadWithSegments -> editSegments(
                state,
                head.segments,
                combine(param, SEGMENT),
                MIN_SEGMENT_LENGTH,
                MAX_SEGMENT_LENGTH,
                MIN_SEGMENT_DIAMETER,
                MAX_SEGMENT_DIAMETER,
            )

            is PolearmHeadWithAxeHead -> {
                editAxeHead(state, head.axe, combine(param, AXE))
                editPolearmFixation(state, head.fixation, combine(param, FIXATION))
            }

            is PolearmHeadWithSpearHead -> {
                editSpearHead(state, head.spear, param)
                editPolearmFixation(state, head.fixation, combine(param, FIXATION))
            }
        }
    }
}

private fun DETAILS.editSpearHead(
    state: State,
    head: SpearHead,
    param: String,
) {
    selectValue(
        "Shape",
        combine(param, SHAPE),
        SpearShape.entries,
        head.shape,
    )
    selectFactor(
        "Length",
        combine(param, LENGTH),
        head.length,
        MIN_SPEAR_LENGTH,
        MAX_SPEAR_LENGTH,
    )
    selectFactor(
        "Width",
        combine(param, WIDTH),
        head.width,
        MIN_SPEAR_WIDTH,
        MAX_SPEAR_WIDTH,
    )
    editColorSchemeItemPart(state, head.part, param, "Spear")
}

// parse

fun parsePolearmHead(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, param, PolearmHeadType.None)) {
    PolearmHeadType.None -> NoPolearmHead
    PolearmHeadType.Rounded -> RoundedPolearmHead
    PolearmHeadType.Sharpened -> SharpenedPolearmHead
    PolearmHeadType.Segments -> PolearmHeadWithSegments(
        parseSegments(parameters, combine(param, SEGMENT)),
    )

    PolearmHeadType.Axe -> PolearmHeadWithAxeHead(
        parseAxeHead(parameters, combine(param, AXE)),
        parsePolearmFixation(parameters, combine(param, FIXATION)),
    )

    PolearmHeadType.Spear -> PolearmHeadWithSpearHead(
        parseSpearHead(parameters, param),
        parsePolearmFixation(parameters, combine(param, FIXATION)),
    )
}

private fun parseSpearHead(parameters: Parameters, param: String) = SpearHead(
    parse(parameters, combine(param, SHAPE), SpearShape.Leaf),
    parseSpearLength(parameters, param),
    parseSpearWidth(parameters, param),
    parseColorSchemeItemPart(parameters, param),
)

private fun parseSpearLength(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, LENGTH), DEFAULT_SPEAR_LENGTH)

private fun parseSpearWidth(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, WIDTH), DEFAULT_SPEAR_WIDTH)