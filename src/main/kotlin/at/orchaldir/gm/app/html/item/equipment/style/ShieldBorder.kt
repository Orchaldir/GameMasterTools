package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.BORDER
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.SHIELD_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.NoShieldBorder
import at.orchaldir.gm.core.model.item.equipment.style.ShieldBorder
import at.orchaldir.gm.core.model.item.equipment.style.ShieldBorderType
import at.orchaldir.gm.core.model.item.equipment.style.SimpleShieldBorder
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showShieldBorder(
    call: ApplicationCall,
    state: State,
    border: ShieldBorder,
) {
    showDetails("Shield Border") {
        field("Type", border.getType())

        when (border) {
            NoShieldBorder -> doNothing()
            is SimpleShieldBorder -> {
                field("Size", border.size)
                showItemPart(call, state, border.main)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editShieldBorder(state: State, border: ShieldBorder) {
    showDetails("Shield Border", true) {
        selectValue("Type", BORDER, ShieldBorderType.entries, border.getType())

        when (border) {
            NoShieldBorder -> doNothing()
            is SimpleShieldBorder -> {
                selectValue("Size", combine(BORDER, SIZE), Size.entries, border.size)
                editItemPart(
                    state,
                    border.main,
                    combine(BORDER, MAIN),
                    allowedTypes = SHIELD_MATERIALS,
                )
            }
        }
    }
}

// parse

fun parseShieldBorder(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, BORDER, ShieldBorderType.None)) {
    ShieldBorderType.None -> NoShieldBorder
    ShieldBorderType.Simple -> SimpleShieldBorder(
        parse(parameters, combine(BORDER, SIZE), Size.Medium),
        parseItemPart(state, parameters, combine(BORDER, MAIN), SHIELD_MATERIALS),
    )
}
