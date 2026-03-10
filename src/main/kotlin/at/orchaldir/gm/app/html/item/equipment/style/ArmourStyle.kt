package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.math.*
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.ARMOR_PLATE_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.ARMOR_SCALE_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.CHAIN_MAIL_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MADE_FROM_METALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showArmourStyle(
    call: ApplicationCall,
    state: State,
    armour: ArmourStyle,
) {
    showDetails("Armour Style") {
        field("Type", armour.getType())

        when (armour) {
            is ChainMail -> showChainMail(call, state, armour)
            is Cuirass -> showCuirass(call, state, armour)
            is LamellarArmour -> showLamellarArmour(call, state, armour)
            is ScaleArmour -> showScaleArmour(call, state, armour)
            is SegmentedArmour -> showSegmentedArmour(call, state, armour)
        }
    }
}

private fun DETAILS.showChainMail(
    call: ApplicationCall,
    state: State,
    armour: ChainMail,
) {
    showItemPart(call, state, armour.chain)
}

private fun DETAILS.showCuirass(
    call: ApplicationCall,
    state: State,
    armour: Cuirass,
) {
    showItemPart(call, state, armour.main)
}

private fun DETAILS.showLamellarArmour(
    call: ApplicationCall,
    state: State,
    armour: LamellarArmour,
) {
    showItemPart(call, state, armour.scale, "Scale")
    showUsingRectangularShape(armour.shape)
    showLamellarLacing(call, state, armour.lacing)
    field("Columns", armour.columns)
}

private fun DETAILS.showScaleArmour(
    call: ApplicationCall,
    state: State,
    armour: ScaleArmour,
) {
    showItemPart(call, state, armour.scale, "Scale")
    showComplexShape(armour.shape, "Scale Shape")
    field("Columns", armour.columns)
    fieldFactor("Row Overlap", armour.overlap)
}

private fun DETAILS.showSegmentedArmour(
    call: ApplicationCall,
    state: State,
    armour: SegmentedArmour,
) {
    showItemPart(call, state, armour.segment, "Segment")
    field("Segment Shape", armour.shape)
    field("Rows", armour.rows)
    field("Breastplate Rows", armour.breastplateRows)
}


// edit

fun HtmlBlockTag.editArmourStyle(
    state: State,
    armour: ArmourStyle,
    availableTypes: Collection<ArmourType> = ArmourType.entries,
    param: String = STYLE,
) {
    showDetails("Armour Style", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            availableTypes,
            armour.getType(),
        )

        when (armour) {
            is ChainMail -> editChainMail(state, param, armour)
            is Cuirass -> editCuirass(state, param, armour)
            is LamellarArmour -> editLamellarArmour(state, param, armour)
            is ScaleArmour -> editScaleArmour(state, param, armour)
            is SegmentedArmour -> editSegmentedArmour(state, param, armour)
        }
    }
}

private fun DETAILS.editChainMail(
    state: State,
    param: String,
    armour: ChainMail,
) {
    editItemPart(
        state,
        armour.chain,
        combine(param, MAIN),
        allowedTypes = CHAIN_MAIL_MATERIALS,
    )
}

private fun DETAILS.editCuirass(
    state: State,
    param: String,
    armour: Cuirass,
) {
    editItemPart(
        state,
        armour.main,
        combine(param, MAIN),
        allowedTypes = ARMOR_PLATE_MATERIALS,
    )
}

private fun DETAILS.editLamellarArmour(
    state: State,
    param: String,
    armour: LamellarArmour,
) {
    selectScale(state, param, armour.scale)
    selectUsingRectangularShape(armour.shape, combine(param, SCALE), LAMELLAR_SHAPES)
    editLamellarLacing(state, combine(param, LACING), armour.lacing)
    selectInt(
        "Columns",
        armour.columns,
        MIN_SCALE_COLUMNS,
        MAX_SCALE_COLUMNS,
        1,
        combine(param, COLUMNS),
    )
}

private fun DETAILS.editScaleArmour(
    state: State,
    param: String,
    armour: ScaleArmour,
) {
    selectScale(state, param, armour.scale)
    selectComplexShape(armour.shape, combine(param, SCALE))
    selectInt(
        "Columns",
        armour.columns,
        MIN_SCALE_COLUMNS,
        MAX_SCALE_COLUMNS,
        1,
        combine(param, COLUMNS),
    )
    selectFactor(
        "Row Overlap",
        combine(param, OFFSET),
        armour.overlap,
        MIN_SCALE_OVERLAP,
        MAX_SCALE_OVERLAP,
    )
}

private fun DETAILS.editSegmentedArmour(
    state: State,
    param: String,
    armour: SegmentedArmour,
) {
    editItemPart(
        state,
        armour.segment,
        combine(param, MAIN),
        "Segment",
        ARMOR_PLATE_MATERIALS,
    )
    selectValue("Segment Shape", combine(param, SHAPE), SegmentedPlateShape.entries, armour.shape)
    selectInt(
        "Rows",
        armour.rows,
        MIN_SCALE_COLUMNS,
        MAX_SCALE_COLUMNS,
        1,
        combine(param, NUMBER),
    )
    selectInt(
        "Breastplate Rows",
        armour.breastplateRows,
        1,
        armour.rows - 1,
        1,
        combine(param, TOP, NUMBER),
    )
}

private fun DETAILS.selectScale(
    state: State,
    param: String,
    scale: ItemPart,
) {
    editItemPart(
        state,
        scale,
        combine(param, MAIN),
        "Scale",
        ARMOR_SCALE_MATERIALS,
    )
}

// parse

fun parseArmourStyle(
    state: State,
    parameters: Parameters,
    param: String = STYLE,
): ArmourStyle {
    val type = parse(parameters, combine(param, TYPE), ArmourType.Lamellar)

    return when (type) {
        ArmourType.Chain -> ChainMail(
            parseItemPart(state, parameters, combine(param, MAIN), CHAIN_MAIL_MATERIALS),
        )

        ArmourType.Cuirass -> Cuirass(
            parsePlate(state, parameters, param),
        )

        ArmourType.Lamellar -> LamellarArmour(
            parseScale(state, parameters, param),
            parseUsingRectangularShape(parameters, combine(param, SCALE)),
            parseLamellarLacing(state, parameters, combine(param, LACING)),
            parseInt(parameters, combine(param, COLUMNS), DEFAULT_SCALE_COLUMNS),
        )

        ArmourType.Scale -> ScaleArmour(
            parseScale(state, parameters, param),
            parseComplexShape(parameters, combine(param, SCALE)),
            parseInt(parameters, combine(param, COLUMNS), DEFAULT_SCALE_COLUMNS),
            parseFactor(parameters, combine(param, OFFSET), DEFAULT_SCALE_OVERLAP),
        )

        ArmourType.Segmented -> SegmentedArmour(
            parsePlate(state, parameters, param),
            parse(parameters, combine(param, SHAPE), SegmentedPlateShape.Straight),
            parseInt(parameters, combine(param, NUMBER), DEFAULT_SCALE_COLUMNS),
            parseInt(
                parameters,
                combine(param, TOP, NUMBER),
                DEFAULT_BREASTPLATE_ROWS,
            ),
        )
    }
}

private fun parseScale(
    state: State,
    parameters: Parameters,
    param: String,
) = parseItemPart(state, parameters, combine(param, MAIN), ARMOR_SCALE_MATERIALS)

private fun parsePlate(
    state: State,
    parameters: Parameters,
    param: String,
) = parseItemPart(state, parameters, combine(param, MAIN), ARMOR_PLATE_MATERIALS)
