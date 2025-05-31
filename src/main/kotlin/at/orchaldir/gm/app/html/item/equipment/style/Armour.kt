package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.COLUMNS
import at.orchaldir.gm.app.LACING
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.OFFSET
import at.orchaldir.gm.app.OVERLAP
import at.orchaldir.gm.app.SCALE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.math.parseComplexShape
import at.orchaldir.gm.app.html.math.parseUsingRectangularShape
import at.orchaldir.gm.app.html.math.selectComplexShape
import at.orchaldir.gm.app.html.math.selectUsingRectangularShape
import at.orchaldir.gm.app.html.math.showComplexShape
import at.orchaldir.gm.app.html.math.showUsingRectangularShape
import at.orchaldir.gm.app.html.parseBool
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectBool
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.Armour
import at.orchaldir.gm.core.model.item.equipment.style.ArmourType
import at.orchaldir.gm.core.model.item.equipment.style.DEFAULT_SCALE_COLUMNS
import at.orchaldir.gm.core.model.item.equipment.style.DEFAULT_SCALE_OVERLAP
import at.orchaldir.gm.core.model.item.equipment.style.LAMELLAR_SHAPES
import at.orchaldir.gm.core.model.item.equipment.style.LamellarArmour
import at.orchaldir.gm.core.model.item.equipment.style.MAX_SCALE_COLUMNS
import at.orchaldir.gm.core.model.item.equipment.style.MAX_SCALE_OVERLAP
import at.orchaldir.gm.core.model.item.equipment.style.MIN_SCALE_COLUMNS
import at.orchaldir.gm.core.model.item.equipment.style.MIN_SCALE_OVERLAP
import at.orchaldir.gm.core.model.item.equipment.style.ScaleArmour
import at.orchaldir.gm.core.model.item.equipment.style.SegmentedArmour
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showArmour(
    call: ApplicationCall,
    state: State,
    armour: Armour,
) {
    showDetails("Armour") {
        field("Type", armour.getType())

        when (armour) {
            is LamellarArmour -> showLamellarArmour(call, state, armour)
            is ScaleArmour -> showScaleArmour(call, state, armour)
            is SegmentedArmour -> showSegmentedArmour(call, state, armour)
        }
    }
}

private fun DETAILS.showLamellarArmour(
    call: ApplicationCall,
    state: State,
    armour: LamellarArmour,
) {
    showColorSchemeItemPart(call, state, armour.scale, "Scale")
    showUsingRectangularShape(armour.shape)
    showLamellarLacing(call, state, armour.lacing)
    field("Columns", armour.columns)
}

private fun DETAILS.showScaleArmour(
    call: ApplicationCall,
    state: State,
    armour: ScaleArmour,
) {
    showColorSchemeItemPart(call, state, armour.scale, "Scale")
    showComplexShape(armour.shape, "Scale Shape")
    field("Columns", armour.columns)
    fieldFactor("Row Overlap", armour.overlap)
}

private fun DETAILS.showSegmentedArmour(
    call: ApplicationCall,
    state: State,
    armour: SegmentedArmour,
) {
    showColorSchemeItemPart(call, state, armour.segment, "Segment")
    field("Segments", armour.segments)
    field("Is overlapping", armour.isOverlapping)
}


// edit

fun FORM.editArmour(state: State, armour: Armour) {
    showDetails("Lacing", true) {
        selectValue("Type", LACING, ArmourType.entries, armour.getType())

        when (armour) {
            is LamellarArmour -> editLamellarArmour(state, armour)
            is ScaleArmour -> editScaleArmour(state, armour)
            is SegmentedArmour -> editSegmentedArmour(state, armour)
        }
    }
}

private fun DETAILS.editLamellarArmour(
    state: State,
    armour: LamellarArmour,
) {
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

private fun DETAILS.editScaleArmour(
    state: State,
    armour: ScaleArmour,
) {
    editColorSchemeItemPart(state, armour.scale, MAIN, "Scale")
    selectComplexShape(armour.shape, SCALE)
    selectInt(
        "Columns",
        armour.columns,
        MIN_SCALE_COLUMNS,
        MAX_SCALE_COLUMNS,
        1,
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

private fun DETAILS.editSegmentedArmour(
    state: State,
    armour: SegmentedArmour,
) {
    editColorSchemeItemPart(state, armour.segment, MAIN, "Scale")
    selectInt(
        "Segments",
        armour.segments,
        MIN_SCALE_COLUMNS,
        MAX_SCALE_COLUMNS,
        1,
        NUMBER,
    )
    selectBool(
        "Is Overlapping",
        armour.isOverlapping,
        OVERLAP,
    )
}

// parse

fun parseArmour(parameters: Parameters): Armour {
    val type = parse(parameters, LACING, ArmourType.Lamellar)

    return when (type) {
        ArmourType.Lamellar -> LamellarArmour(
            parseColorSchemeItemPart(parameters, MAIN),
            parseUsingRectangularShape(parameters, SCALE),
            parseLamellarLacing(parameters),
            parseInt(parameters, COLUMNS, DEFAULT_SCALE_COLUMNS),
        )

        ArmourType.Scale -> ScaleArmour(
            parseColorSchemeItemPart(parameters, MAIN),
            parseComplexShape(parameters, SCALE),
            parseInt(parameters, COLUMNS, DEFAULT_SCALE_COLUMNS),
            parseFactor(parameters, OFFSET, DEFAULT_SCALE_OVERLAP),
        )
        ArmourType.Segmented -> SegmentedArmour(
            parseColorSchemeItemPart(parameters, MAIN),
            parseInt(parameters, NUMBER, DEFAULT_SCALE_COLUMNS),
            parseBool(parameters, OVERLAP),
        )
    }
}
