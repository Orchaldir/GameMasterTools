package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.*
import at.orchaldir.gm.app.html.util.fieldDistance
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.fieldSize
import at.orchaldir.gm.app.html.util.parseDistance
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.parseSize
import at.orchaldir.gm.app.html.util.selectDistance
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.html.util.selectPercentage
import at.orchaldir.gm.app.html.util.selectSize
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.book.typography.Typography
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag


private val prefix = SiPrefix.Milli

// show

fun HtmlBlockTag.showTextFormat(
    call: ApplicationCall,
    state: State,
    format: TextFormat,
) {
    showDetails("Format") {
        field("Type", format.getType())

        when (format) {
            UndefinedTextFormat -> doNothing()
            is Book -> {
                field("Pages", format.pages)
                showColorItemPart(call, state, format.page, "Page")
                showBinding(call, state, format.binding)
                fieldSize("Size", format.size)
            }

            is Scroll -> {
                fieldDistance("Roll Length", format.rollLength)
                fieldDistance("Roll Diameter", format.rollDiameter)
                showColorItemPart(call, state, format.main)
                showScrollFormat(call, state, format.format)
            }
        }
    }
}

private fun HtmlBlockTag.showBinding(
    call: ApplicationCall,
    state: State,
    binding: BookBinding,
) {
    showDetails("Binding") {
        field("Type", binding.getType())

        when (binding) {
            is CopticBinding -> {
                showFillItemPart(call, state, binding.cover, "Cover")
                showSewingPattern(call, state, binding.sewingPattern)
            }

            is Hardcover -> {
                showFillItemPart(call, state, binding.cover, "Cover")
                showBossesPattern(call, state, binding.bosses)
                showEdgeProtection(call, state, binding.protection)
            }

            is LeatherBinding -> {
                showFillItemPart(call, state, binding.cover, "Cover")
                showColorItemPart(call, state, binding.leather, "Leather")
                field("Leather Binding", binding.style)
            }
        }
    }
}


private fun HtmlBlockTag.showBossesPattern(
    call: ApplicationCall,
    state: State,
    pattern: BossesPattern,
) {
    showDetails("Bosses Pattern") {
        field("Type", pattern.getType())

        when (pattern) {
            NoBosses -> doNothing()
            is SimpleBossesPattern -> {
                field("Shape", pattern.shape)
                field("Size", pattern.size)
                showColorItemPart(call, state, pattern.boss)
                field("Pattern", pattern.pattern.toString())
            }
        }
    }
}

private fun HtmlBlockTag.showEdgeProtection(
    call: ApplicationCall,
    state: State,
    protection: EdgeProtection,
) {
    showDetails("Edge Protection") {
        field("Type", protection.getType())

        when (protection) {
            NoEdgeProtection -> doNothing()
            is ProtectedCorners -> {
                field("Corner Shape", protection.shape)
                fieldFactor("Corner Size", protection.size)
                showColorItemPart(call, state, protection.main)
            }

            is ProtectedEdge -> {
                fieldFactor("Edge Width", protection.width)
                showColorItemPart(call, state, protection.main)
            }
        }
    }
}

private fun HtmlBlockTag.showSewingPattern(
    call: ApplicationCall,
    state: State,
    pattern: SewingPattern,
) {
    showDetails("Sewing") {
        field("Pattern", pattern.getType())

        when (pattern) {
            is SimpleSewingPattern -> {
                showColorItemPart(call, state, pattern.thread)
                field("Size", pattern.size)
                field("Distance Between Edge & Hole", pattern.length)
                fieldList("Stitches", pattern.stitches) { stitch ->
                    +stitch.name
                }
            }

            is ComplexSewingPattern -> {
                showList(pattern.stitches) { complex ->
                    showColorItemPart(call, state, complex.thread)
                    field("Size", complex.size)
                    field("Distance Between Edge & Hole", complex.length)
                    field("Stitch", complex.stitch)
                }
            }
        }
    }
}

private fun HtmlBlockTag.showScrollFormat(
    call: ApplicationCall,
    state: State,
    format: ScrollFormat,
) {
    field("Scroll Format", format.getType())

    when (format) {
        is ScrollWithOneRod -> showScrollHandle(call, state, format.handle)
        is ScrollWithTwoRods -> showScrollHandle(call, state, format.handle)
        ScrollWithoutRod -> doNothing()
    }
}

private fun HtmlBlockTag.showScrollHandle(
    call: ApplicationCall,
    state: State,
    handle: ScrollHandle,
) {
    fieldList("Handle Segments", handle.segments) { segment ->
        fieldDistance("Length", segment.length)
        fieldDistance("Diameter", segment.diameter)
        showColorItemPart(call, state, segment.main)
        field("Shape", segment.shape)
    }
}

// edit

fun FORM.editTextFormat(
    state: State,
    format: TextFormat,
    hasAuthor: Boolean,
) {
    showDetails("Format", true) {
        selectValue("Type", FORMAT, TextFormatType.entries, format.getType())

        when (format) {
            UndefinedTextFormat -> doNothing()
            is Book -> {
                selectInt("Pages", format.pages, MIN_PAGES, 10000, 1, PAGES)
                editColorItemPart(state, format.page, PAGE, "Page")
                editBinding(state, format.binding, hasAuthor)
                selectSize(SIZE, format.size, MIN_TEXT_SIZE, MAX_TEXT_SIZE, prefix)
            }

            is Scroll -> {
                selectDistance(
                    "Roll Length",
                    LENGTH,
                    format.rollLength,
                    MIN_TEXT_SIZE,
                    MAX_TEXT_SIZE,
                    prefix,
                )
                selectDistance(
                    "Roll Diameter",
                    DIAMETER,
                    format.rollDiameter,
                    MIN_TEXT_SIZE,
                    MAX_TEXT_SIZE,
                    prefix,
                )
                selectFactor(
                    "Page Width",
                    WIDTH,
                    format.pageWidth,
                    MIN_PAGE_WIDTH_FACTOR,
                    MAX_PAGE_WIDTH_FACTOR,
                )
                editColorItemPart(state, format.main, SCROLL)
                editScrollFormat(state, format.format)
            }
        }
    }
}

private fun HtmlBlockTag.editBinding(
    state: State,
    binding: BookBinding,
    hasAuthor: Boolean,
) {
    showDetails("Binding", true) {
        selectValue("Type", BINDING, BookBindingType.entries, binding.getType())

        when (binding) {
            is CopticBinding -> {
                editCover(state, binding.cover, binding.typography, hasAuthor)
                editSewingPattern(state, binding.sewingPattern)
            }

            is Hardcover -> {
                editCover(state, binding.cover, binding.typography, hasAuthor)
                editBossesPattern(state, binding.bosses)
                editEdgeProtection(state, binding.protection)
            }

            is LeatherBinding -> {
                editCover(state, binding.cover, binding.typography, hasAuthor)
                editColorItemPart(state, binding.leather, LEATHER)
                selectValue(
                    "Leather Binding",
                    combine(LEATHER, BINDING),
                    LeatherBindingStyle.entries,
                    binding.style,
                )
            }
        }
    }
}

private fun HtmlBlockTag.editCover(
    state: State,
    cover: FillItemPart,
    typography: Typography,
    hasAuthor: Boolean,
) {
    showDetails("Cover", true) {
        editFillItemPart(state, cover, COVER)
        editTypography(state, typography, hasAuthor)
    }
}

private fun HtmlBlockTag.editBossesPattern(
    state: State,
    bosses: BossesPattern,
) {
    showDetails("Bosses", true) {
        selectValue("Pattern", BOSSES, BossesPatternType.entries, bosses.getType())

        when (bosses) {
            is NoBosses -> doNothing()
            is SimpleBossesPattern -> {
                selectValue("Bosses Shape", combine(BOSSES, SHAPE), BossesShape.entries, bosses.shape)
                selectValue("Bosses Size", combine(BOSSES, SIZE), Size.entries, bosses.size)
                editColorItemPart(state, bosses.boss, BOSSES)
                selectInt("Bosses Pattern Size", bosses.pattern.size, 1, 20, 1, combine(BOSSES, NUMBER))

                showListWithIndex(bosses.pattern) { index, count ->
                    val countParam = combine(BOSSES, index)
                    selectInt("Count", count, 1, 20, 1, countParam)
                }
            }
        }
    }
}

private fun HtmlBlockTag.editEdgeProtection(
    state: State,
    protection: EdgeProtection,
) {
    showDetails("Edge Protection", true) {
        selectValue("Type", EDGE, EdgeProtectionType.entries, protection.getType())

        when (protection) {
            NoEdgeProtection -> doNothing()
            is ProtectedCorners -> {
                selectValue("Corner Shape", combine(EDGE, SHAPE), CornerShape.entries, protection.shape)
                selectPercentage(
                    "Corner Size",
                    combine(EDGE, SIZE),
                    protection.size,
                    1,
                    50,
                    1,
                )
                editColorItemPart(state, protection.main, EDGE)
            }

            is ProtectedEdge -> {
                selectPercentage(
                    "Edge Width",
                    combine(EDGE, SIZE),
                    protection.width,
                    1,
                    20,
                    1,
                )
                editColorItemPart(state, protection.main, EDGE)
            }
        }
    }
}

private fun HtmlBlockTag.editSewingPattern(state: State, pattern: SewingPattern) {
    showDetails("Sewing Pattern", true) {
        selectValue("Type", SEWING, SewingPatternType.entries, pattern.getType())

        when (pattern) {
            is SimpleSewingPattern -> {
                editColorItemPart(state, pattern.thread, SEWING, "Thread")
                selectValue("Size", combine(SEWING, SIZE), Size.entries, pattern.size)
                selectValue("Distance Between Edge & Hole", combine(SEWING, LENGTH), Size.entries, pattern.length)
                editSewingPattern(pattern.stitches) { elementParam, element ->
                    selectValue("Stitch", elementParam, StitchType.entries, element)
                }
            }

            is ComplexSewingPattern -> {
                editSewingPattern(pattern.stitches) { elementParam, element ->
                    editColorItemPart(state, element.thread, elementParam, "Thread")
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

private fun HtmlBlockTag.editScrollFormat(
    state: State,
    format: ScrollFormat,
) {
    selectValue("Scroll Format", SCROLL, ScrollFormatType.entries, format.getType())

    when (format) {
        is ScrollWithOneRod -> editScrollHandle(state, format.handle)
        is ScrollWithTwoRods -> editScrollHandle(state, format.handle)
        ScrollWithoutRod -> doNothing()
    }
}

private fun HtmlBlockTag.editScrollHandle(
    state: State,
    handle: ScrollHandle,
) {
    editList("Pattern", HANDLE, handle.segments, 1, 20, 1) { _, segmentParam, segment ->
        selectDistance(
            "Length",
            combine(segmentParam, LENGTH),
            segment.length,
            MIN_SEGMENT_DISTANCE,
            MAX_SEGMENT_DISTANCE,
            prefix,
        )
        selectDistance(
            "Diameter",
            combine(segmentParam, DIAMETER),
            segment.diameter,
            MIN_SEGMENT_DISTANCE,
            MAX_SEGMENT_DISTANCE,
            prefix,
        )
        editColorItemPart(state, segment.main, segmentParam)
        selectValue("Shape", combine(segmentParam, SHAPE), HandleSegmentShape.entries, segment.shape)
    }
}

// parse

fun parseTextFormat(parameters: Parameters) = when (parse(parameters, FORMAT, TextFormatType.Undefined)) {
    TextFormatType.Book -> Book(
        parseBinding(parameters),
        parseInt(parameters, PAGES, DEFAULT_PAGES),
        parseColorItemPart(parameters, PAGE),
        parseSize(parameters, SIZE, prefix, DEFAULT_BOOK_SIZE),
    )

    TextFormatType.Scroll -> Scroll(
        parseScrollFormat(parameters),
        parseDistance(parameters, LENGTH, prefix, DEFAULT_ROLL_LENGTH),
        parseDistance(parameters, DIAMETER, prefix, DEFAULT_ROLL_DIAMETER),
        parseFactor(parameters, WIDTH, DEFAULT_PAGE_WIDTH_FACTOR),
        parseColorItemPart(parameters, SCROLL),
    )

    TextFormatType.Undefined -> UndefinedTextFormat
}

private fun parseBinding(parameters: Parameters) = when (parse(parameters, BINDING, BookBindingType.Hardcover)) {
    BookBindingType.Coptic -> CopticBinding(
        parseFillItemPart(parameters, COVER),
        parseTextTypography(parameters),
        parseSewing(parameters),
    )

    BookBindingType.Hardcover -> Hardcover(
        parseFillItemPart(parameters, COVER),
        parseTextTypography(parameters),
        parseBosses(parameters),
        parseEdgeProtection(parameters),
    )

    BookBindingType.Leather -> LeatherBinding(
        parse(parameters, combine(LEATHER, BINDING), LeatherBindingStyle.Half),
        parseFillItemPart(parameters, COVER),
        parseColorItemPart(parameters, LEATHER),
        parseTextTypography(parameters),
    )
}

private fun parseBosses(parameters: Parameters) = when (parse(parameters, BOSSES, BossesPatternType.None)) {
    BossesPatternType.Simple -> SimpleBossesPattern(
        parseBossesPattern(parameters),
        parse(parameters, combine(BOSSES, SHAPE), BossesShape.Circle),
        parse(parameters, combine(BOSSES, SIZE), Size.Medium),
        parseColorItemPart(parameters, BOSSES),
    )

    BossesPatternType.None -> NoBosses
}

private fun parseBossesPattern(parameters: Parameters): List<Int> {
    val count = parseInt(parameters, combine(BOSSES, NUMBER), 1)

    return (0..<count)
        .map { index ->
            parseInt(parameters, combine(BOSSES, index), 1)
        }
}

private fun parseEdgeProtection(parameters: Parameters) = when (parse(parameters, EDGE, EdgeProtectionType.None)) {
    EdgeProtectionType.None -> NoEdgeProtection
    EdgeProtectionType.Corners -> ProtectedCorners(
        parse(parameters, combine(EDGE, SHAPE), CornerShape.Triangle),
        parseFactor(parameters, combine(EDGE, SIZE), DEFAULT_PROTECTED_CORNER_SIZE),
        parseColorItemPart(parameters, EDGE),
    )

    EdgeProtectionType.Edge -> ProtectedEdge(
        parseFactor(parameters, combine(EDGE, SIZE), DEFAULT_PROTECTED_EDGE_WIDTH),
        parseColorItemPart(parameters, EDGE),
    )
}

private fun parseSewing(parameters: Parameters) = when (parse(parameters, SEWING, SewingPatternType.Simple)) {
    SewingPatternType.Simple -> SimpleSewingPattern(
        parseColorItemPart(parameters, SEWING),
        parse(parameters, combine(SEWING, SIZE), Size.Medium),
        parse(parameters, combine(SEWING, LENGTH), Size.Medium),
        parseSimplePattern(parameters),
    )

    SewingPatternType.Complex -> ComplexSewingPattern(parseComplexPattern(parameters))
}

private fun parseSimplePattern(parameters: Parameters) = parseList(parameters, SEWING, 2) { _, param ->
    parse(parameters, param, StitchType.Kettle)
}

private fun parseComplexPattern(parameters: Parameters) = parseList(parameters, SEWING, 2) { _, param ->
    ComplexStitch(
        parseColorItemPart(parameters, param),
        parse(parameters, combine(param, SIZE), Size.Medium),
        parse(parameters, combine(param, LENGTH), Size.Medium),
        parse(parameters, param, StitchType.Kettle),
    )
}

private fun parseScrollFormat(parameters: Parameters) = when (parse(parameters, SCROLL, ScrollFormatType.NoRod)) {
    ScrollFormatType.NoRod -> ScrollWithoutRod
    ScrollFormatType.OneRod -> ScrollWithOneRod(parseScrollHandle(parameters))
    ScrollFormatType.TwoRods -> ScrollWithTwoRods(parseScrollHandle(parameters))
}

private fun parseScrollHandle(parameters: Parameters) = ScrollHandle(
    parseHandleSegments(parameters),
)

private fun parseHandleSegments(parameters: Parameters) = parseList(parameters, HANDLE, 1) { _, param ->
    HandleSegment(
        parseDistance(parameters, combine(param, LENGTH), prefix, 40),
        parseDistance(parameters, combine(param, DIAMETER), prefix, 15),
        parseColorItemPart(parameters, param),
        parse(parameters, combine(param, SHAPE), HandleSegmentShape.Cylinder),
    )
}
