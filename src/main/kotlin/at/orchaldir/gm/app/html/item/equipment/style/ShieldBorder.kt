package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.BORDER
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.NoShieldBorder
import at.orchaldir.gm.core.model.item.equipment.style.ShieldBorder
import at.orchaldir.gm.core.model.item.equipment.style.ShieldBorderType
import at.orchaldir.gm.core.model.item.equipment.style.SimpleShieldBorder
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
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
                showColorSchemeItemPart(call, state, border.part)
            }
        }
    }
}

// edit

fun FORM.editShieldBorder(state: State, border: ShieldBorder) {
    showDetails("Shield Border", true) {
        selectValue("Type", BORDER, ShieldBorderType.entries, border.getType())

        when (border) {
            NoShieldBorder -> doNothing()
            is SimpleShieldBorder -> {
                selectValue("Size", combine(BORDER, SIZE), Size.entries, border.size)
                editColorSchemeItemPart(state, border.part, BORDER)
            }
        }
    }
}

// parse

fun parseShieldBorder(parameters: Parameters) = when (parse(parameters, BORDER, ShieldBorderType.None)) {
    ShieldBorderType.None -> NoShieldBorder
    ShieldBorderType.Simple -> SimpleShieldBorder(
        parse(parameters, combine(BORDER, SIZE), Size.Medium),
        parseColorSchemeItemPart(parameters, BORDER),
    )
}
