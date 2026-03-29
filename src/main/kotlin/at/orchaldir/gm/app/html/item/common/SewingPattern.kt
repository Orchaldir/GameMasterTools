package at.orchaldir.gm.app.html.item.common

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SEWING
import at.orchaldir.gm.app.THICKNESS
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.common.ComplexSewingPattern
import at.orchaldir.gm.core.model.item.common.ComplexStitch
import at.orchaldir.gm.core.model.item.common.MAX_STITCHES
import at.orchaldir.gm.core.model.item.common.MIN_STITCHES
import at.orchaldir.gm.core.model.item.common.SewingPattern
import at.orchaldir.gm.core.model.item.common.SewingPatternType
import at.orchaldir.gm.core.model.item.common.SimpleSewingPattern
import at.orchaldir.gm.core.model.item.common.RepeatedStitch
import at.orchaldir.gm.core.model.item.common.StitchType
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
) {
    showDetails(label, true) {
        selectValue("Type", param, SewingPatternType.entries, pattern.getType())

        when (pattern) {
            is RepeatedStitch -> {
                selectCordMaterial(state, param, pattern.cord)
                selectCordThickness(param, pattern.thickness)
                selectStitchWidth(param, pattern.width)
                selectStitchType(param, pattern.stitch)
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
                editSewingPattern(pattern.stitches, param) { elementParam, element ->
                    selectStitchType(elementParam, element)
                }
            }

            is ComplexSewingPattern -> {
                editSewingPattern(pattern.stitches, param) { elementParam, element ->
                    selectCordMaterial(state, elementParam, element.cord)
                    selectCordThickness(elementParam, element.thickness)
                    selectStitchWidth(elementParam, element.width)
                    selectStitchType(elementParam, element.stitch)
                }
            }
        }
    }
}

private fun HtmlBlockTag.selectCordMaterial(
    state: State,
    param: String,
    cord: ItemPart,
) = editItemPart(state, cord, param, allowedTypes = LINE_MATERIALS)

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
    type: StitchType,
) = selectValue(
    "Stitch",
    combine(param, TYPE),
    StitchType.entries,
    type,
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
    stitchParam: String,
): ItemPart = parseItemPart(state, parameters, stitchParam, LINE_MATERIALS)

private fun parseCordThickness(parameters: Parameters, param: String) =
    parse(parameters, combine(param, THICKNESS), Size.Medium)

private fun parseStitchWidth(parameters: Parameters, param: String) =
    parse(parameters, combine(param, WIDTH), Size.Medium)

private fun parseStitchType(parameters: Parameters, param: String) =
    parse(parameters, combine(param, TYPE), StitchType.Kettle)
