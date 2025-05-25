package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.editFillLookupItemPart
import at.orchaldir.gm.app.html.item.parseFillLookupItemPart
import at.orchaldir.gm.app.html.item.showFillLookupItemPart
import at.orchaldir.gm.app.html.math.parseComplexShape
import at.orchaldir.gm.app.html.math.selectComplexShape
import at.orchaldir.gm.app.html.math.showComplexShape
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Shield
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.shape.SHAPES_WITHOUT_CROSS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showShield(
    call: ApplicationCall,
    state: State,
    shield: Shield,
) {
    showComplexShape(shield.shape)
    field("Size", shield.size)
    showShieldBorder(call, state, shield.border)
    showShieldBoss(call, state, shield.boss)
    showFillLookupItemPart(call, state, shield.main, "Main")
}

// edit

fun FORM.editShield(
    state: State,
    shield: Shield,
) {
    selectComplexShape(shield.shape, SHAPE, SHAPES_WITHOUT_CROSS)
    selectValue("Size", SIZE, Size.entries, shield.size)
    editShieldBorder(state, shield.border)
    editShieldBoss(state, shield.boss)
    editFillLookupItemPart(state, shield.main, MAIN, "Main")
}

// parse

fun parseShield(parameters: Parameters) = Shield(
    parseComplexShape(parameters, SHAPE),
    parse(parameters, SIZE, Size.Medium),
    parseShieldBorder(parameters),
    parseShieldBoss(parameters),
    parseFillLookupItemPart(parameters, MAIN),
)