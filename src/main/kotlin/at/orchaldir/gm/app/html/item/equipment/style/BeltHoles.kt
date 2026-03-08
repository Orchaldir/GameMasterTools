package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.HOLE
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBeltHoles(
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

fun HtmlBlockTag.editBeltHoles(
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

fun parseBeltHoles(parameters: Parameters): BeltHoles {
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
