package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.math.*
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
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
    field("Segment Shape", armour.shape)
    field("Rows", armour.rows)
    field("Breastplate Rows", armour.breastplateRows)
}


// edit

fun HtmlBlockTag.editArmour(state: State, armour: Armour) {
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
    selectValue("Segment Shape", SHAPE, SegmentedPlateShape.entries, armour.shape)
    selectInt(
        "Rows",
        armour.rows,
        MIN_SCALE_COLUMNS,
        MAX_SCALE_COLUMNS,
        1,
        NUMBER,
    )
    selectInt(
        "Breastplate Rows",
        armour.breastplateRows,
        1,
        armour.rows - 1,
        1,
        combine(TOP, NUMBER),
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
            parse(parameters, SHAPE, SegmentedPlateShape.Straight),
            parseInt(parameters, NUMBER, DEFAULT_SCALE_COLUMNS),
            parseInt(
                parameters,
                combine(TOP, NUMBER),
                DEFAULT_BREASTPLATE_ROWS,
            ),
        )
    }
}
