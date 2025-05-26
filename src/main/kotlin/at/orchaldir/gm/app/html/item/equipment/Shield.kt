package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.BACK
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.math.parseComplexShape
import at.orchaldir.gm.app.html.math.selectComplexShape
import at.orchaldir.gm.app.html.math.showComplexShape
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
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
    showFillLookupItemPart(call, state, shield.front, "Front")
    showFillLookupItemPart(call, state, shield.back, "Back")
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
    editFillLookupItemPart(state, shield.front, MAIN, "Front")
    editFillLookupItemPart(state, shield.back, BACK, "Back")
}

// parse

fun parseShield(parameters: Parameters) = Shield(
    parseComplexShape(parameters, SHAPE),
    parse(parameters, SIZE, Size.Medium),
    parseShieldBorder(parameters),
    parseShieldBoss(parameters),
    parseFillLookupItemPart(parameters, MAIN),
    parseFillLookupItemPart(parameters, BACK),
)