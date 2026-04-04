package at.orchaldir.gm.app.html.item.common

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.common.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.LINE_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

private const val CORD_THICKNESS = "Cord Thickness"
private const val STITCH_WIDTH = "Stitch Width"

fun HtmlBlockTag.showSewingPattern(
    call: ApplicationCall,
    state: State,
    pattern: SewingPattern,
    label: String = "Sewing Pattern",
) {
    showDetails(label) {
        field("Type", pattern.getType())

        when (pattern) {
            is RepeatedStitch -> {
                showItemPart(call, state, pattern.cord)
                field(CORD_THICKNESS, pattern.thickness)
                field(STITCH_WIDTH, pattern.width)
                field("Stitch", pattern.stitch)
                field("Count", pattern.count)
            }

            is SimpleSewingPattern -> {
                showItemPart(call, state, pattern.cord)
                field(CORD_THICKNESS, pattern.thickness)
                field(STITCH_WIDTH, pattern.width)
                fieldList("Stitches", pattern.stitches) { stitch ->
                    +stitch.name
                }
            }

            is ComplexSewingPattern -> {
                showList(pattern.stitches) { complex ->
                    showItemPart(call, state, complex.cord)
                    field(CORD_THICKNESS, complex.thickness)
                    field(STITCH_WIDTH, complex.width)
                    field("Stitch", complex.stitch)
                }
            }
        }
    }
}

// edit

fun HtmlBlockTag.editSewingPattern(
    state: State,
    pattern: SewingPattern,
    param: String = SEWING,
    label: String = "Sewing Pattern",
    allowedStitchTypes: Collection<StitchType> = StitchType.entries,
) {
    showDetails(label, true) {
        selectValue("Type", param, SewingPatternType.entries, pattern.getType())

        when (pattern) {
            is RepeatedStitch -> {
                selectCordMaterial(state, param, pattern.cord)
                selectCordThickness(param, pattern.thickness)
                selectStitchWidth(param, pattern.width)
                selectStitchType(param, pattern.stitch, allowedStitchTypes - StitchType.Empty)
                selectInt(
                    "Count",
                    pattern.count,
                    MIN_STITCHES,
                    MAX_STITCHES,
                    1,
                    combine(param, NUMBER),
                )
            }

            is SimpleSewingPattern -> {
                selectCordMaterial(state, param, pattern.cord)
                selectCordThickness(param, pattern.thickness)
                selectStitchWidth(param, pattern.width)
                editSewingPattern(pattern.stitches, param) { elementParam, stitch ->
                    selectStitchType(elementParam, stitch, allowedStitchTypes)
                }
            }

            is ComplexSewingPattern -> {
                editSewingPattern(pattern.stitches, param) { elementParam, element ->
                    selectCordMaterial(state, elementParam, element.cord)
                    selectCordThickness(elementParam, element.thickness)
                    selectStitchWidth(elementParam, element.width)
                    selectStitchType(elementParam, element.stitch, allowedStitchTypes)
                }
            }
        }
    }
}

private fun HtmlBlockTag.selectCordMaterial(
    state: State,
    param: String,
    cord: ItemPart,
) = editItemPart(
    state,
    cord,
    combine(param, MATERIAL),
    allowedTypes = LINE_MATERIALS,
)

private fun HtmlBlockTag.selectCordThickness(
    param: String,
    radius: Size,
) = selectValue(
    CORD_THICKNESS,
    combine(param, THICKNESS),
    Size.entries,
    radius,
)

private fun HtmlBlockTag.selectStitchWidth(
    param: String,
    width: Size,
) = selectValue(
    STITCH_WIDTH,
    combine(param, WIDTH),
    Size.entries,
    width,
)

private fun HtmlBlockTag.selectStitchType(
    param: String,
    stitch: StitchType,
    allowedStitchTypes: Collection<StitchType>,
) = selectValue(
    "Stitch",
    combine(param, TYPE),
    allowedStitchTypes,
    stitch,
)

private fun <T> DETAILS.editSewingPattern(
    elements: Collection<T>,
    param: String,
    editElement: HtmlBlockTag.(String, T) -> Unit,
) {
    editList("Stitch", param, elements, MIN_STITCHES, MAX_STITCHES, 1) { _, param, element ->
        editElement(param, element)
    }
}

// parse

fun parseSewing(
    state: State,
    parameters: Parameters,
    param: String = SEWING,
) = when (parse(parameters, param, SewingPatternType.Repeated)) {
    SewingPatternType.Repeated -> RepeatedStitch(
        parseCordMaterial(state, parameters, param),
        parseCordThickness(parameters, param),
        parseStitchWidth(parameters, param),
        parseStitchType(parameters, param),
        parseInt(parameters, combine(param, NUMBER), 2),
    )

    SewingPatternType.Simple -> SimpleSewingPattern(
        parseCordMaterial(state, parameters, param),
        parseCordThickness(parameters, param),
        parseStitchWidth(parameters, param),
        parseSimplePattern(parameters, param),
    )

    SewingPatternType.Complex -> ComplexSewingPattern(
        parseComplexPattern(state, parameters, param),
    )
}

private fun parseSimplePattern(
    parameters: Parameters,
    param: String,
) = parseList(parameters, param, 2) { _, stitchParam ->
    parseStitchType(parameters, stitchParam)
}

private fun parseComplexPattern(
    state: State,
    parameters: Parameters,
    param: String,
) = parseList(parameters, param, 2) { _, stitchParam ->
    ComplexStitch(
        parseCordMaterial(state, parameters, stitchParam),
        parseCordThickness(parameters, stitchParam),
        parseStitchWidth(parameters, stitchParam),
        parseStitchType(parameters, stitchParam),
    )
}

private fun parseCordMaterial(
    state: State,
    parameters: Parameters,
    param: String,
) = parseItemPart(state, parameters, combine(param, MATERIAL), LINE_MATERIALS)

private fun parseCordThickness(parameters: Parameters, param: String) =
    parse(parameters, combine(param, THICKNESS), Size.Medium)

private fun parseStitchWidth(parameters: Parameters, param: String) =
    parse(parameters, combine(param, WIDTH), Size.Medium)

private fun parseStitchType(parameters: Parameters, param: String) =
    parse(parameters, combine(param, TYPE), StitchType.Kettle)
