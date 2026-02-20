package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.math.*
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showLegArmourStyle(
    call: ApplicationCall,
    state: State,
    style: LegArmourStyle,
) {
    showDetails("Leg Armour Style") {
        field("Type", style.getType())

        when (style) {
            is SameLegArmour -> field("Length", style.length)
            is DifferentLegArmour -> {
                showArmourStyle(call, state, style.style)
                field("Length", style.length)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editLegArmourStyle(
    state: State,
    armour: LegArmourStyle,
    param: String = LEG,
) {
    showDetails("Armour Style", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            LegArmourStyleType.entries,
            armour.getType(),
        )

        when (armour) {
            is SameLegArmour -> selectLength(armour.length, param)
            is DifferentLegArmour -> {
                editArmourStyle(
                    state,
                    armour.style,
                    LOWER_BODY_ARMOR_TYPES,
                    combine(param, STYLE),
                )
                selectLength(armour.length, param)
            }
        }
    }
}

private fun DETAILS.selectLength(
    length: OuterwearLength,
    param: String,
) {
    selectValue(
        "Length",
        combine(param, LENGTH),
        listOf(OuterwearLength.Hip, OuterwearLength.Ankle),
        length,
    )
}

// parse

fun parseLegArmourStyle(parameters: Parameters, param: String = LEG): LegArmourStyle {
    val type = parse(parameters, combine(param, TYPE), LegArmourStyleType.Same)

    return when (type) {
        LegArmourStyleType.Same -> SameLegArmour(
            parseLength(parameters, param),
        )
        LegArmourStyleType.Different -> DifferentLegArmour(
            parseArmourStyle(parameters, combine(param, STYLE)),
            parseLength(parameters, param),
        )
    }
}

private fun parseLength(
    parameters: Parameters,
    param: String,
): OuterwearLength = parse(parameters, combine(param, LENGTH), OuterwearLength.Hip)
