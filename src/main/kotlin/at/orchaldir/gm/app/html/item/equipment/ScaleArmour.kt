package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.math.parseComplexShape
import at.orchaldir.gm.app.html.math.selectComplexShape
import at.orchaldir.gm.app.html.math.showComplexShape
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.DEFAULT_SCALE_COLUMNS
import at.orchaldir.gm.core.model.item.equipment.DEFAULT_SCALE_OVERLAP
import at.orchaldir.gm.core.model.item.equipment.MAX_SCALE_COLUMNS
import at.orchaldir.gm.core.model.item.equipment.MAX_SCALE_OVERLAP
import at.orchaldir.gm.core.model.item.equipment.MIN_SCALE_COLUMNS
import at.orchaldir.gm.core.model.item.equipment.MIN_SCALE_OVERLAP
import at.orchaldir.gm.core.model.item.equipment.ScaleArmour
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.ZERO
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showScaleArmour(
    call: ApplicationCall,
    state: State,
    armour: ScaleArmour,
) {
    field("Length", armour.length)
    field("Sleeve Style", armour.sleeveStyle)
    showColorSchemeItemPart(call, state, armour.scale, "Scale")
    showComplexShape(armour.shape, "Scale Shape")
    field("Columns", armour.columns)
    fieldFactor("Row Overlap", armour.overlap)
}

// edit

fun FORM.editScaleArmour(
    state: State,
    armour: ScaleArmour,
) {
    selectValue("Length", LENGTH, OuterwearLength.entries, armour.length)
    selectSleeveStyle(SleeveStyle.entries, armour.sleeveStyle)
    editColorSchemeItemPart(state, armour.scale, MAIN, "Scale")
    selectComplexShape(armour.shape, SCALE)
    selectInt(
        "Columns",
        armour.columns,
        MIN_SCALE_COLUMNS,
        10,
        MAX_SCALE_COLUMNS,
        COLUMNS,
    )
    selectFactor(
        "Row Overlap",
        OFFSET,
        armour.overlap,
        MIN_SCALE_OVERLAP,
        MAX_SCALE_OVERLAP,
    )
}

// parse

fun parseScaleArmour(parameters: Parameters) = ScaleArmour(
    parse(parameters, LENGTH, OuterwearLength.Hip),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parseColorSchemeItemPart(parameters, MAIN),
    parseComplexShape(parameters, SCALE),
    parseInt(parameters, COLUMNS, DEFAULT_SCALE_COLUMNS),
    parseFactor(parameters, OFFSET, DEFAULT_SCALE_OVERLAP),
)
