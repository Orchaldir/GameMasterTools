package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.FIXATION
import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.SEGMENT
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.editSegments
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseSegments
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showSegments
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import io.ktor.http.*
import io.ktor.server.application.*
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
            is SpearHead -> {
                field("Shape", head.shape)
                fieldFactor("Length", head.length)
                fieldFactor("Width", head.width)
                showColorSchemeItemPart(call, state, head.head, "Spear")
                showPolearmFixation(call, state, head.fixation)
            }
        }
    }
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

            is SpearHead -> {
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
                editColorSchemeItemPart(state, head.head, param, "Spear")
                editPolearmFixation(state, head.fixation, combine(param, FIXATION))
            }
        }
    }
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
    PolearmHeadType.Spear -> SpearHead(
        parse(parameters, combine(param, SHAPE), SpearShape.Leaf),
        parseSpearLength(parameters, param),
        parseSpearWidth(parameters, param),
        parseColorSchemeItemPart(parameters, param),
        parsePolearmFixation(parameters, combine(param, FIXATION)),
    )
}

private fun parseSpearLength(parameters: Parameters, param: String): Factor =
    parseFactor(parameters, combine(param, LENGTH), DEFAULT_SPEAR_LENGTH)

private fun parseSpearWidth(parameters: Parameters, param: String): Factor =
    parseFactor(parameters, combine(param, WIDTH), DEFAULT_SPEAR_WIDTH)