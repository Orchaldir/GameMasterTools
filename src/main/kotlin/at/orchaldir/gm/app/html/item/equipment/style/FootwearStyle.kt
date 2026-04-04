package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.FOOTWEAR_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.ItemPart
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showFootwearStyle(
    call: ApplicationCall,
    state: State,
    style: FootwearStyle,
) {
    showDetails("Footwear Style") {
        field("Type", style.getType())

        when (style) {
            is Boot -> {
                showShaftAndSole(call, state, style.shaft, style.sole)
                showOpening(call, state, style.opening)
            }

            is KneeHighBoot -> {
                showShaftAndSole(call, state, style.shaft, style.sole)
                showOpening(call, state, style.opening)
            }

            is Pumps -> showMain(call, state, style.main)
            is Sandal -> showShaftAndSole(call, state, style.shaft, style.sole)
            is Shoe -> showShaftAndSole(call, state, style.shaft, style.sole)
            is SimpleShoe -> showMain(call, state, style.main)
            is Slipper -> showShaftAndSole(call, state, style.shaft, style.sole)
        }
    }
}

private fun HtmlBlockTag.showShaftAndSole(
    call: ApplicationCall,
    state: State,
    shaft: ItemPart,
    sole: ItemPart,
) {
    showItemPart(call, state, shaft, "Shaft")
    showItemPart(call, state, sole, "Sole")
}

private fun HtmlBlockTag.showMain(
    call: ApplicationCall,
    state: State,
    main: ItemPart,
) {
    showItemPart(call, state, main, "Main")
}

// edit

fun HtmlBlockTag.editFootwearStyle(
    state: State,
    style: FootwearStyle,
    param: String = FOOTWEAR,
) {
    showDetails("Footwear Style", true) {
        selectValue(
            "Type",
            combine(param, STYLE),
            FootwearType.entries,
            style.getType(),
        )

        when (style) {
            is Boot -> {
                editShaftAndSole(state, style.shaft, style.sole)
                editFootwearOpenings(state, param, style.opening)
            }

            is KneeHighBoot -> {
                editShaftAndSole(state, style.shaft, style.sole)
                editFootwearOpenings(state, param, style.opening)
            }

            is Pumps -> editMain(state, style.main)
            is Sandal -> editShaftAndSole(state, style.shaft, style.sole)
            is Shoe -> editShaftAndSole(state, style.shaft, style.sole)
            is SimpleShoe -> editMain(state, style.main)
            is Slipper -> editShaftAndSole(state, style.shaft, style.sole)
        }
    }
}

private fun DETAILS.editFootwearOpenings(
    state: State,
    param: String,
    opening: Opening,
) {
    editOpening(
        state,
        opening,
        FOOTWEAR_OPENINGS,
        combine(param, OPENING),
    )
}

private fun HtmlBlockTag.editShaftAndSole(
    state: State,
    shaft: ItemPart,
    sole: ItemPart,
) {
    editMain(state, shaft, "Shaft")
    editItemPart(state, sole, SOLE, "Sole", FOOTWEAR_MATERIALS)
}

private fun HtmlBlockTag.editMain(
    state: State,
    main: ItemPart,
    label: String = "Main",
) {
    editItemPart(state, main, SHAFT, label, FOOTWEAR_MATERIALS)
}

// parse

fun parseFootwearStyle(
    state: State,
    parameters: Parameters,
    param: String = FOOTWEAR,
): FootwearStyle {
    val type = parse(parameters, combine(param, STYLE), FootwearType.Shoe)

    return when (type) {
        FootwearType.Boot -> Boot(
            parseMain(state, parameters),
            parseSole(state, parameters),
            parseFootwearOpening(state, parameters, param),
        )

        FootwearType.KneeHighBoot -> KneeHighBoot(
            parseMain(state, parameters),
            parseSole(state, parameters),
            parseFootwearOpening(state, parameters, param),
        )

        FootwearType.Pumps -> Pumps(
            parseMain(state, parameters),
        )

        FootwearType.Sandal -> Sandal(
            parseMain(state, parameters),
            parseSole(state, parameters),
        )

        FootwearType.Shoe -> Shoe(
            parseMain(state, parameters),
            parseSole(state, parameters),
        )

        FootwearType.SimpleShoe -> SimpleShoe(
            parseMain(state, parameters),
        )

        FootwearType.Slipper -> Slipper(
            parseMain(state, parameters),
            parseSole(state, parameters),
        )
    }
}

private fun parseFootwearOpening(
    state: State,
    parameters: Parameters,
    param: String,
) = parseOpening(state, parameters, combine(param, OPENING))

private fun parseMain(
    state: State,
    parameters: Parameters,
) = parseItemPart(state, parameters, SHAFT, FOOTWEAR_MATERIALS)

private fun parseSole(
    state: State,
    parameters: Parameters,
) = parseItemPart(state, parameters, SOLE, FOOTWEAR_MATERIALS)