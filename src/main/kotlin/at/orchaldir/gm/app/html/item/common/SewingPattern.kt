package at.orchaldir.gm.app.html.item.common

import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.SEWING
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.common.ComplexSewingPattern
import at.orchaldir.gm.core.model.item.common.ComplexStitch
import at.orchaldir.gm.core.model.item.common.MIN_STITCHES
import at.orchaldir.gm.core.model.item.common.SewingPattern
import at.orchaldir.gm.core.model.item.common.SewingPatternType
import at.orchaldir.gm.core.model.item.common.SimpleSewingPattern
import at.orchaldir.gm.core.model.item.common.StitchType
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.LINE_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSewingPattern(
    call: ApplicationCall,
    state: State,
    pattern: SewingPattern,
) {
    showDetails("Sewing") {
        field("Pattern", pattern.getType())

        when (pattern) {
            is SimpleSewingPattern -> {
                showItemPart(call, state, pattern.thread)
                field("Size", pattern.size)
                field("Distance Between Edge & Hole", pattern.length)
                fieldList("Stitches", pattern.stitches) { stitch ->
                    +stitch.name
                }
            }

            is ComplexSewingPattern -> {
                showList(pattern.stitches) { complex ->
                    showItemPart(call, state, complex.thread)
                    field("Size", complex.size)
                    field("Distance Between Edge & Hole", complex.length)
                    field("Stitch", complex.stitch)
                }
            }
        }
    }
}

// edit

fun HtmlBlockTag.editSewingPattern(state: State, pattern: SewingPattern) {
    showDetails("Sewing Pattern", true) {
        selectValue("Type", SEWING, SewingPatternType.entries, pattern.getType())

        when (pattern) {
            is SimpleSewingPattern -> {
                editItemPart(state, pattern.thread, SEWING, allowedTypes = LINE_MATERIALS)
                selectValue("Size", combine(SEWING, SIZE), Size.entries, pattern.size)
                selectValue("Distance Between Edge & Hole", combine(SEWING, LENGTH), Size.entries, pattern.length)
                editSewingPattern(pattern.stitches) { elementParam, element ->
                    selectValue("Stitch", elementParam, StitchType.entries, element)
                }
            }

            is ComplexSewingPattern -> {
                editSewingPattern(pattern.stitches) { elementParam, element ->
                    editItemPart(state, element.thread, elementParam, allowedTypes = LINE_MATERIALS)
                    selectValue("Size", combine(elementParam, SIZE), Size.entries, element.size)
                    selectValue(
                        "Distance Between Edge & Hole",
                        combine(elementParam, LENGTH),
                        Size.entries,
                        element.length,
                    )
                    selectValue("Stitch", elementParam, StitchType.entries, element.stitch)
                }
            }
        }
    }
}

private fun <T> DETAILS.editSewingPattern(
    elements: Collection<T>,
    editElement: HtmlBlockTag.(String, T) -> Unit,
) {
    editList("Stitch", SEWING, elements, MIN_STITCHES, 20, 1) { _, param, element ->
        editElement(param, element)
    }
}

// parse

fun parseSewing(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, SEWING, SewingPatternType.Simple)) {
    SewingPatternType.Simple -> SimpleSewingPattern(
        parseItemPart(state, parameters, SEWING, LINE_MATERIALS),
        parse(parameters, combine(SEWING, SIZE), Size.Medium),
        parse(parameters, combine(SEWING, LENGTH), Size.Medium),
        parseSimplePattern(parameters),
    )

    SewingPatternType.Complex -> ComplexSewingPattern(
        parseComplexPattern(state, parameters),
    )
}

private fun parseSimplePattern(parameters: Parameters) = parseList(parameters, SEWING, 2) { _, param ->
    parse(parameters, param, StitchType.Kettle)
}

private fun parseComplexPattern(
    state: State,
    parameters: Parameters,
) = parseList(parameters, SEWING, 2) { _, param ->
    ComplexStitch(
        parseItemPart(state, parameters, param, LINE_MATERIALS),
        parse(parameters, combine(param, SIZE), Size.Medium),
        parse(parameters, combine(param, LENGTH), Size.Medium),
        parse(parameters, param, StitchType.Kettle),
    )
}
