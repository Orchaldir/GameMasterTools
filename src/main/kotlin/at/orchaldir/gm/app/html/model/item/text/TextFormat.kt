package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.Factor
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag


private val step = Distance(10)
private val min = Distance(10)
private val max = Distance(2000)

// show

fun BODY.showTextFormat(
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
                showBinding(call, state, format.binding)
                fieldSize("Size", format.size)
            }

            is Scroll -> {
                fieldDistance("Roll Length", format.rollLength)
                fieldDistance("Roll Diameter", format.rollDiameter)
                field("Scroll Color", format.color)
                fieldLink("Scroll Material", call, state, format.material)
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
                showCover(call, state, binding.cover)
                showSewingPattern(binding.sewingPattern)
            }

            is Hardcover -> {
                showCover(call, state, binding.cover)
                showBossesPattern(call, state, binding.bosses)
                showEdgeProtection(call, state, binding.protection)
            }

            is LeatherBinding -> {
                showCover(call, state, binding.cover)
                field("Leather Color", binding.leatherColor)
                fieldLink("Leather Material", call, state, binding.leatherMaterial)
                field("Leather Binding", binding.type)
            }
        }
    }
}

private fun HtmlBlockTag.showCover(
    call: ApplicationCall,
    state: State,
    cover: BookCover,
) {
    showDetails("Cover") {
        field("Color", cover.color)
        fieldLink("Material", call, state, cover.material)
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
                field("Color", pattern.color)
                fieldLink("Material", call, state, pattern.material)
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
                field("Corner Size", protection.size)
                field("Corner Color", protection.color)
                fieldLink("Corner Material", call, state, protection.material)
            }

            is ProtectedEdge -> {
                field("Edge Width", protection.width)
                field("Edge Color", protection.color)
                fieldLink("Edge Material", call, state, protection.material)
            }
        }
    }
}

private fun HtmlBlockTag.showSewingPattern(pattern: SewingPattern) {
    showDetails("Sewing") {
        field("Pattern", pattern.getType())

        when (pattern) {
            is SimpleSewingPattern -> {
                field("Color", pattern.color)
                field("Size", pattern.size)
                field("Distance Between Edge & Hole", pattern.length)
                showList("Stitches", pattern.stitches) { stitch ->
                    +stitch.name
                }
            }

            is ComplexSewingPattern -> {
                showList(pattern.stitches) { complex ->
                    field("Color", complex.color)
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
    fieldLink("Handle Material", call, state, handle.material)
    showList("Handle Segments", handle.segments) { segment ->
        fieldDistance("Length", segment.length)
        fieldDistance("Diameter", segment.diameter)
        field("Color", segment.color)
        field("Shape", segment.shape)
    }
}

// edit

fun FORM.editTextFormat(
    state: State,
    format: TextFormat,
    hasAuthor: Boolean,
) {
    showDetails("Text Format", true) {
        selectValue("Type", FORMAT, TextFormatType.entries, format.getType(), true)

        when (format) {
            UndefinedTextFormat -> doNothing()
            is Book -> {
                selectInt("Pages", format.pages, MIN_PAGES, 10000, 1, PAGES)
                editBinding(state, format.binding, hasAuthor)
                selectSize(SIZE, format.size, min, max, step, true)
            }

            is Scroll -> {
                selectDistance("Roll Length", LENGTH, format.rollLength, min, max, step, true)
                selectDistance("Roll Diameter", LENGTH, format.rollDiameter, min, max, step, true)
                selectColor("Scroll Color", COLOR, Color.entries, format.color)
                selectElement(
                    state,
                    "Scroll Material",
                    MATERIAL,
                    state.getMaterialStorage().getAll(),
                    format.material,
                )
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
        selectValue("Type", BINDING, BookBindingType.entries, binding.getType(), true)

        when (binding) {
            is CopticBinding -> {
                editCover(state, binding.cover, hasAuthor)
                editSewingPattern(binding.sewingPattern)
            }

            is Hardcover -> {
                editCover(state, binding.cover, hasAuthor)
                editBossesPattern(state, binding.bosses)
                editEdgeProtection(state, binding.protection)
            }

            is LeatherBinding -> {
                editCover(state, binding.cover, hasAuthor)
                selectColor("Leather Color", combine(LEATHER, BINDING, COLOR), Color.entries, binding.leatherColor)
                selectElement(
                    state,
                    "Leather Material",
                    combine(LEATHER, MATERIAL),
                    state.getMaterialStorage().getAll(),
                    binding.leatherMaterial,
                    false
                )
                selectValue(
                    "Leather Binding",
                    combine(LEATHER, BINDING),
                    LeatherBindingType.entries,
                    binding.type,
                    true
                )
            }
        }
    }
}

private fun HtmlBlockTag.editCover(
    state: State,
    cover: BookCover,
    hasAuthor: Boolean,
) {
    showDetails("Cover", true) {
        selectColor("Cover Color", combine(COVER, BINDING, COLOR), Color.entries, cover.color)
        selectElement(
            state,
            "Cover Material",
            combine(COVER, MATERIAL),
            state.getMaterialStorage().getAll(),
            cover.material,
        )
        editTypography(state, cover.typography, hasAuthor)
    }
}

private fun HtmlBlockTag.editBossesPattern(
    state: State,
    bosses: BossesPattern,
) {
    showDetails("Bosses", true) {
        selectValue("Pattern", BOSSES, BossesPatternType.entries, bosses.getType(), true)

        when (bosses) {
            is NoBosses -> doNothing()
            is SimpleBossesPattern -> {
                selectValue("Bosses Shape", combine(BOSSES, SHAPE), BossesShape.entries, bosses.shape, true)
                selectValue("Bosses Size", combine(BOSSES, SIZE), Size.entries, bosses.size, true)
                selectColor("Bosses Color", combine(BOSSES, COLOR), Color.entries, bosses.color)
                selectElement(
                    state,
                    "Bosses Material",
                    combine(BOSSES, MATERIAL),
                    state.getMaterialStorage().getAll(),
                    bosses.material,
                )
                selectInt("Bosses Pattern Size", bosses.pattern.size, 1, 20, 1, combine(BOSSES, NUMBER), true)

                showListWithIndex(bosses.pattern) { index, count ->
                    val countParam = combine(BOSSES, index)
                    selectInt("Count", count, 1, 20, 1, countParam, true)
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
        selectValue("Type", EDGE, EdgeProtectionType.entries, protection.getType(), true)

        when (protection) {
            NoEdgeProtection -> doNothing()
            is ProtectedCorners -> {
                selectValue("Corner Shape", combine(EDGE, SHAPE), CornerShape.entries, protection.shape, true)
                selectFloat("Corner Size", protection.size.value, 0.01f, 0.5f, 0.01f, combine(EDGE, SIZE), true)
                selectColor("Corner Color", combine(EDGE, COLOR), Color.entries, protection.color)
                selectElement(
                    state,
                    "Corner Material",
                    combine(EDGE, MATERIAL),
                    state.getMaterialStorage().getAll(),
                    protection.material,
                )
            }

            is ProtectedEdge -> {
                selectFloat("Edge Width", protection.width.value, 0.01f, 0.2f, 0.01f, combine(EDGE, SIZE), true)
                selectColor("Edge Color", combine(EDGE, COLOR), Color.entries, protection.color)
                selectElement(
                    state,
                    "Edge Material",
                    combine(EDGE, MATERIAL),
                    state.getMaterialStorage().getAll(),
                    protection.material,
                )
            }
        }
    }
}

private fun HtmlBlockTag.editSewingPattern(pattern: SewingPattern) {
    showDetails("Sewing Pattern", true) {
        selectValue("Type", SEWING, SewingPatternType.entries, pattern.getType(), true)

        when (pattern) {
            is SimpleSewingPattern -> {
                selectColor("Color", combine(SEWING, COLOR), Color.entries, pattern.color)
                selectValue("Size", combine(SEWING, SIZE), Size.entries, pattern.size, true)
                selectValue("Distance Between Edge & Hole", combine(SEWING, LENGTH), Size.entries, pattern.length, true)
                editSewingPatternSize(pattern.stitches.size)

                showListWithIndex(pattern.stitches) { index, stitch ->
                    val stitchParam = combine(SEWING, index)
                    selectValue("Stitch", stitchParam, StitchType.entries, stitch, true)
                }
            }

            is ComplexSewingPattern -> {
                editSewingPatternSize(pattern.stitches.size)

                showListWithIndex(pattern.stitches) { index, complex ->
                    val stitchParam = combine(SEWING, index)

                    selectColor("Color", combine(stitchParam, COLOR), Color.entries, complex.color)
                    selectValue("Size", combine(stitchParam, SIZE), Size.entries, complex.size, true)
                    selectValue(
                        "Distance Between Edge & Hole",
                        combine(stitchParam, LENGTH),
                        Size.entries,
                        complex.length,
                        true
                    )
                    selectValue("Stitch", stitchParam, StitchType.entries, complex.stitch, true)
                }
            }
        }
    }
}

private fun HtmlBlockTag.editSewingPatternSize(size: Int) {
    selectInt("Pattern Size", size, MIN_STITCHES, 20, 1, combine(SEWING, NUMBER), true)
}

private fun HtmlBlockTag.editScrollFormat(
    state: State,
    format: ScrollFormat,
) {
    selectValue("Scroll Format", SCROLL, ScrollFormatType.entries, format.getType(), true)

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
    selectElement(
        state,
        "Handle Material",
        combine(HANDLE, MATERIAL),
        state.getMaterialStorage().getAll(),
        handle.material,
    )
    selectInt("Handle Segment Number", handle.segments.size, 1, 20, 1, combine(HANDLE, NUMBER), true)
    showListWithIndex(handle.segments) { index, segment ->
        val segmentParam = combine(HANDLE, index)

        selectDistance("Length", combine(segmentParam, LENGTH), segment.length, min, max, step, true)
        selectDistance("Diameter", combine(segmentParam, DIAMETER), segment.diameter, min, max, step, true)
        selectColor("Color", combine(segmentParam, COLOR), Color.entries, segment.color)
        selectValue("Shape", combine(segmentParam, SHAPE), HandleSegmentShape.entries, segment.shape, true)
    }
}

// parse

fun parseTextFormat(parameters: Parameters) = when (parse(parameters, FORMAT, TextFormatType.Undefined)) {
    TextFormatType.Book -> Book(
        parseInt(parameters, PAGES, 100),
        parseBinding(parameters),
        parseSize(parameters, SIZE),
    )

    TextFormatType.Scroll -> Scroll(
        parseScrollFormat(parameters),
        parseDistance(parameters, LENGTH, 200),
        parseDistance(parameters, DIAMETER, 50),
        parse(parameters, COLOR, Color.Green),
        parseMaterialId(parameters, MATERIAL),
    )

    TextFormatType.Undefined -> UndefinedTextFormat
}

private fun parseBinding(parameters: Parameters) = when (parse(parameters, BINDING, BookBindingType.Hardcover)) {
    BookBindingType.Coptic -> CopticBinding(
        parseCover(parameters),
        parseSewing(parameters),
    )

    BookBindingType.Hardcover -> Hardcover(
        parseCover(parameters),
        parseBosses(parameters),
        parseEdgeProtection(parameters),
    )

    BookBindingType.Leather -> LeatherBinding(
        parse(parameters, combine(LEATHER, BINDING, COLOR), Color.SaddleBrown),
        parseMaterialId(parameters, combine(LEATHER, MATERIAL)),
        parse(parameters, combine(LEATHER, BINDING), LeatherBindingType.Half),
        parseCover(parameters),
    )
}

private fun parseCover(parameters: Parameters) = BookCover(
    parse(parameters, combine(COVER, BINDING, COLOR), Color.Black),
    parseMaterialId(parameters, combine(COVER, MATERIAL)),
    parseTextTypography(parameters),
)

private fun parseBosses(parameters: Parameters) = when (parse(parameters, BOSSES, BossesPatternType.None)) {
    BossesPatternType.Simple -> SimpleBossesPattern(
        parseBossesPattern(parameters),
        parse(parameters, combine(BOSSES, SHAPE), BossesShape.Circle),
        parse(parameters, combine(BOSSES, SIZE), Size.Medium),
        parse(parameters, combine(BOSSES, COLOR), Color.Crimson),
        parseMaterialId(parameters, combine(BOSSES, MATERIAL)),
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
        parseFactor(parameters, combine(EDGE, SIZE), Factor(0.2f)),
        parse(parameters, combine(EDGE, COLOR), Color.Crimson),
        parseMaterialId(parameters, combine(EDGE, MATERIAL)),
    )

    EdgeProtectionType.Edge -> ProtectedEdge(
        parseFactor(parameters, combine(EDGE, SIZE), Factor(0.2f)),
        parse(parameters, combine(EDGE, COLOR), Color.Crimson),
        parseMaterialId(parameters, combine(EDGE, MATERIAL)),
    )
}

private fun parseSewing(parameters: Parameters) = when (parse(parameters, SEWING, SewingPatternType.Simple)) {
    SewingPatternType.Simple -> SimpleSewingPattern(
        parse(parameters, combine(SEWING, COLOR), Color.Crimson),
        parse(parameters, combine(SEWING, SIZE), Size.Medium),
        parse(parameters, combine(SEWING, LENGTH), Size.Medium),
        parseSimplePattern(parameters),
    )

    SewingPatternType.Complex -> ComplexSewingPattern(parseComplexPattern(parameters))
}

private fun parseSimplePattern(parameters: Parameters): List<StitchType> {
    val count = parseInt(parameters, combine(SEWING, NUMBER), 2)

    return (0..<count)
        .map { index ->
            parse(parameters, combine(SEWING, index), StitchType.Kettle)
        }
}

private fun parseComplexPattern(parameters: Parameters): List<ComplexStitch> {
    val count = parseInt(parameters, combine(SEWING, NUMBER), 2)

    return (0..<count)
        .map { index ->
            val stitchParam = combine(SEWING, index)

            ComplexStitch(
                parse(parameters, combine(stitchParam, COLOR), Color.Crimson),
                parse(parameters, combine(stitchParam, SIZE), Size.Medium),
                parse(parameters, combine(stitchParam, LENGTH), Size.Medium),
                parse(parameters, stitchParam, StitchType.Kettle),
            )
        }
}

private fun parseScrollFormat(parameters: Parameters) = when (parse(parameters, SCROLL, ScrollFormatType.NoRod)) {
    ScrollFormatType.NoRod -> ScrollWithoutRod
    ScrollFormatType.OneRod -> ScrollWithOneRod(parseScrollHandle(parameters))
    ScrollFormatType.TwoRods -> ScrollWithTwoRods(parseScrollHandle(parameters))
}

private fun parseScrollHandle(parameters: Parameters) = ScrollHandle(
    parseHandleSegments(parameters),
    parseMaterialId(parameters, combine(HANDLE, MATERIAL)),
)

private fun parseHandleSegments(parameters: Parameters): List<HandleSegment> {
    val count = parseInt(parameters, combine(HANDLE, NUMBER), 1)

    return (0..<count)
        .map { index ->
            val segmentParam = combine(HANDLE, index)

            HandleSegment(
                parseDistance(parameters, combine(segmentParam, LENGTH), 40),
                parseDistance(parameters, combine(segmentParam, DIAMETER), 15),
                parse(parameters, combine(segmentParam, COLOR), Color.Black),
                parse(parameters, combine(segmentParam, SHAPE), HandleSegmentShape.Cylinder),
            )
        }
}
