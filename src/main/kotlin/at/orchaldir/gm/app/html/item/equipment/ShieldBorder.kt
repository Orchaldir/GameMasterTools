package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.BOSS
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.editColorSchemeItemPart
import at.orchaldir.gm.app.html.item.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.item.showColorSchemeItemPart
import at.orchaldir.gm.app.html.math.parseCircularShape
import at.orchaldir.gm.app.html.math.selectCircularShape
import at.orchaldir.gm.app.html.math.showCircularShape
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.NoShieldBorder
import at.orchaldir.gm.core.model.item.equipment.style.ShieldBorder
import at.orchaldir.gm.core.model.item.equipment.style.ShieldBorderType
import at.orchaldir.gm.core.model.item.equipment.style.SimpleShieldBorder
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
            is SimpleShieldBorder -> showColorSchemeItemPart(call, state, border.part)
        }
    }
}

// edit

fun FORM.editShieldBorder(state: State, border: ShieldBorder) {
    showDetails("Shield Border", true) {
        selectValue("Type", BOSS, ShieldBorderType.entries, border.getType())

        when (border) {
            NoShieldBorder -> doNothing()
            is SimpleShieldBorder -> editColorSchemeItemPart(state, border.part, BOSS)
        }
    }
}

// parse

fun parseShieldBorder(parameters: Parameters) = when (parse(parameters, BOSS, ShieldBorderType.None)) {
    ShieldBorderType.None -> NoShieldBorder
    ShieldBorderType.Simple -> SimpleShieldBorder(
        parseColorSchemeItemPart(parameters, BOSS),
    )
}
