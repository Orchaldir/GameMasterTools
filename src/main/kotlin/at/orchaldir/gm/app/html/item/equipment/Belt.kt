package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.editFillItemPart
import at.orchaldir.gm.app.html.item.parseFillItemPart
import at.orchaldir.gm.app.html.item.showFillItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Belt
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBelt(
    call: ApplicationCall,
    state: State,
    belt: Belt,
) {
    showBuckle(call, state, belt.buckle)
    showFillItemPart(call, state, belt.strap, "Strap")
    showBeltHoles(belt.holes)
}

private fun HtmlBlockTag.showBuckle(
    call: ApplicationCall,
    state: State,
    buckle: Buckle,
) {
    showDetails("Buckle", true) {
        when (buckle) {
            NoBuckle -> doNothing()
            is SimpleBuckle -> {
                field("Shape", buckle.shape)
                field("Size", buckle.size)
                showFillItemPart(call, state, buckle.part)
            }
        }
    }
}

private fun HtmlBlockTag.showBeltHoles(
    holes: BeltHoles,
) {
    showDetails("Belt Holes", true) {
        field("Type", holes.getType())

        when (holes) {
            NoBeltHoles -> doNothing()
            is OneRowOfBeltHoles -> {
                field("Size", holes.size)
                optionalField("Border Color", holes.border)
            }

            is ThreeRowsOfBeltHoles -> optionalField("Border Color", holes.border)
            is TwoRowsOfBeltHoles -> optionalField("Border Color", holes.border)
        }
    }
}

// edit

fun FORM.editBelt(
    state: State,
    belt: Belt,
) {
    editBuckle(state, belt.buckle)
    editFillItemPart(state, belt.strap, STRAP)
    editBeltHoles(belt.holes)
}

private fun FORM.editBuckle(
    state: State,
    buckle: Buckle,
) {
    showDetails("Buckle", true) {
        selectValue("Type", combine(BUCKLE, TYPE), BuckleType.entries, buckle.getType())

        when (buckle) {
            NoBuckle -> doNothing()
            is SimpleBuckle -> {
                selectValue("Shape", combine(BUCKLE, SHAPE), BuckleShape.entries, buckle.shape)
                selectValue("Size", combine(BUCKLE, SIZE), Size.entries, buckle.size)
                editFillItemPart(state, buckle.part, BUCKLE)
            }
        }
    }
}

private fun FORM.editBeltHoles(
    holes: BeltHoles,
) {
    showDetails("Belt Holes", true) {
        selectValue("Type", combine(HOLE, TYPE), BeltHolesType.entries, holes.getType())

        when (holes) {
            NoBeltHoles -> doNothing()
            is OneRowOfBeltHoles -> {
                selectValue("Size", combine(HOLE, SIZE), Size.entries, holes.size)
                selectBorderColor(holes.border)
            }

            is ThreeRowsOfBeltHoles -> selectBorderColor(holes.border)
            is TwoRowsOfBeltHoles -> selectBorderColor(holes.border)
        }
    }
}

private fun DETAILS.selectBorderColor(color: Color?) {
    selectOptionalColor(color, combine(HOLE, COLOR), "Border Color")
}

// parse

fun parseBelt(parameters: Parameters) = Belt(
    parseBuckle(parameters),
    parseFillItemPart(parameters, STRAP),
    parseBeltHoles(parameters),
)

private fun parseBuckle(parameters: Parameters): Buckle {
    val type = parse(parameters, combine(BUCKLE, TYPE), BuckleType.NoBuckle)

    return when (type) {
        BuckleType.NoBuckle -> NoBuckle
        BuckleType.Simple -> SimpleBuckle(
            parse(parameters, combine(BUCKLE, SHAPE), BuckleShape.Rectangle),
            parse(parameters, combine(BUCKLE, SIZE), Size.Small),
            parseFillItemPart(parameters, BUCKLE),
        )
    }
}

private fun parseBeltHoles(parameters: Parameters): BeltHoles {
    val type = parse(parameters, combine(HOLE, TYPE), BeltHolesType.NoBeltHoles)

    return when (type) {
        BeltHolesType.NoBeltHoles -> NoBeltHoles
        BeltHolesType.OneRow -> OneRowOfBeltHoles(
            parse(parameters, combine(HOLE, SIZE), Size.Small),
            parseBorderColor(parameters),
        )

        BeltHolesType.TwoRows -> TwoRowsOfBeltHoles(parseBorderColor(parameters))
        BeltHolesType.ThreeRows -> ThreeRowsOfBeltHoles(parseBorderColor(parameters))
    }
}

private fun parseBorderColor(parameters: Parameters) =
    parse<Color>(parameters, combine(HOLE, COLOR))
