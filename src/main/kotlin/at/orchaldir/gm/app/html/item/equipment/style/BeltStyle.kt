package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BELT_STRAP_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.ItemPartType
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBeltStyle(
    call: ApplicationCall,
    state: State,
    style: BeltStyle,
) {
    showDetails("Style") {
        field("Type", style.getType())

        when (style) {
            is BuckleAndStrap -> {
                showBuckle(call, state, style.buckle)
                showItemPart(call, state, style.strap, "Strap")
                showBeltHoles(style.holes)
            }
            is RopeBelt -> {
                showItemPart(call, state, style.main, "Rope")
                field("Length", style.length)
                field("Thickness", style.thickness)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editBeltStyle(
    state: State,
    style: BeltStyle,
    param: String = BELT,
) {
    showDetails("Belt Style", true) {
        selectValue(
            "Type",
            combine(param, STYLE),
            BeltStyleType.entries,
            style.getType(),
        )

        when (style) {
            is BuckleAndStrap -> {
                editBuckle(state, style.buckle)
                editItemPart(
                    state,
                    style.strap,
                    combine(param, STRAP),
                    "Strap",
                    BELT_STRAP_MATERIALS,
                )
                editBeltHoles(style.holes)
            }
            is RopeBelt -> {
                editItemPart(
                    state,
                    style.main,
                    combine(param, STRAP),
                    "Rope",
                    ItemPartType.Cord,
                )
                selectValue(
                    "Length",
                    combine(param, LENGTH),
                    Size.entries,
                    style.length,
                )
                selectValue(
                    "Thickness",
                    combine(param, THICKNESS),
                    Size.entries,
                    style.thickness,
                )
            }
        }
    }
}

// parse

fun parseBeltStyle(
    state: State,
    parameters: Parameters,
    param: String = BELT,
): BeltStyle {
    val type = parse(parameters, combine(param, STYLE), BeltStyleType.BuckleAndStrap)

    return when (type) {
        BeltStyleType.BuckleAndStrap -> BuckleAndStrap(
            parseBuckle(state, parameters),
            parseItemPart(
                state,
                parameters,
                combine(param, STRAP),
                BELT_STRAP_MATERIALS,
            ),
            parseBeltHoles(parameters),
        )
        BeltStyleType.Rope -> RopeBelt(
            parseItemPart(
                state,
                parameters,
                combine(param, STRAP),
                ItemPartType.Cord,
            ),
            parse(parameters, combine(param, LENGTH), Size.Medium),
            parse(parameters, combine(param, THICKNESS), Size.Medium),
        )
    }
}
