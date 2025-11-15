package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.math.editRotatedShape
import at.orchaldir.gm.app.html.math.parseComplexShape
import at.orchaldir.gm.app.html.math.parseRotatedShape
import at.orchaldir.gm.app.html.math.selectComplexShape
import at.orchaldir.gm.app.html.math.showComplexShape
import at.orchaldir.gm.app.html.math.showRotatedShape
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showClubHead(
    call: ApplicationCall,
    state: State,
    head: ClubHead,
) {
    showDetails("Club Head") {
        field("Type", head.getType())

        when (head) {
            NoClubHead -> doNothing()
            is SimpleClubHead -> {
                showComplexShape(head.shape)
                showColorSchemeItemPart(call, state, head.part, "Head")
            }
            is SimpleFlangedHead -> {
                showComplexShape(head.shape)
                showColorSchemeItemPart(call, state, head.part, "Head")
            }
            is ComplexFlangedHead -> {
                showRotatedShape(head.shape)
                showColorSchemeItemPart(call, state, head.part, "Head")
            }
        }
    }
}
// edit

fun HtmlBlockTag.editClubHead(
    state: State,
    head: ClubHead,
    param: String,
) {
    showDetails("Club Head", true) {
        selectValue("Type", param, ClubHeadType.entries, head.getType())

        when (head) {
            NoClubHead -> doNothing()
            is SimpleClubHead -> {
                selectComplexShape(head.shape, combine(param, SHAPE))
                editColorSchemeItemPart(state, head.part, param, "Head")
            }
            is SimpleFlangedHead -> {
                selectComplexShape(head.shape, combine(param, SHAPE))
                editColorSchemeItemPart(state, head.part, param, "Head")
            }
            is ComplexFlangedHead -> {
                editRotatedShape(head.shape, combine(param, SHAPE))
                editColorSchemeItemPart(state, head.part, param, "Head")
            }
        }
    }
}

// parse

fun parseClubHead(
    parameters: Parameters,
    param: String = AXE,
) = when (parse(parameters, param, ClubHeadType.None)) {
    ClubHeadType.None -> NoClubHead
    ClubHeadType.Simple -> SimpleClubHead(
        parseComplexShape(parameters, combine(param, SHAPE)),
        parseColorSchemeItemPart(parameters, param),
    )
    ClubHeadType.SimpleFlanged -> SimpleFlangedHead(
        parseComplexShape(parameters, combine(param, SHAPE)),
        parseColorSchemeItemPart(parameters, param),
    )
    ClubHeadType.ComplexFlanged -> ComplexFlangedHead(
        parseRotatedShape(parameters, combine(param, SHAPE)),
        parseColorSchemeItemPart(parameters, param),
    )
}
