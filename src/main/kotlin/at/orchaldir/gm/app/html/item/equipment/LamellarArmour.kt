package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.math.parseUsingRectangularShape
import at.orchaldir.gm.app.html.math.selectUsingRectangularShape
import at.orchaldir.gm.app.html.math.showUsingRectangularShape
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showLamellarArmour(
    call: ApplicationCall,
    state: State,
    armour: LamellarArmour,
) {
    field("Length", armour.length)
    field("Sleeve Style", armour.sleeveStyle)
    showColorSchemeItemPart(call, state, armour.scale, "Scale")
    showUsingRectangularShape(armour.shape)
    showLamellarLacing(call, state, armour.lacing)
    field("Columns", armour.columns)
}

// edit

fun FORM.editLamellarArmour(
    state: State,
    armour: LamellarArmour,
) {
    selectValue("Length", LENGTH, OuterwearLength.entries, armour.length)
    selectSleeveStyle(SleeveStyle.entries, armour.sleeveStyle)
    editColorSchemeItemPart(state, armour.scale, MAIN, "Scale")
    selectUsingRectangularShape(armour.shape, SCALE, LAMELLAR_SHAPES)
    editLamellarLacing(state, armour.lacing)
    selectInt(
        "Columns",
        armour.columns,
        MIN_SCALE_COLUMNS,
        MAX_SCALE_COLUMNS,
        1,
        COLUMNS,
    )
}

// parse

fun parseLamellarArmour(parameters: Parameters) = LamellarArmour(
    parse(parameters, LENGTH, OuterwearLength.Hip),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parseColorSchemeItemPart(parameters, MAIN),
    parseUsingRectangularShape(parameters, SCALE),
    parseLamellarLacing(parameters),
    parseInt(parameters, COLUMNS, DEFAULT_SCALE_COLUMNS),
)
