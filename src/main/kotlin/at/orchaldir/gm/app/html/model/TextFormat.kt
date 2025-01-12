package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseMaterialId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
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
    showDetails("Description") {
        field("Format", format.getType())

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
    field("Binding", binding.getType())

    when (binding) {
        is CopticBinding -> {
            showCover(call, state, binding.cover)
            showSewingPattern(binding.sewingPattern)
        }

        is Hardcover -> {
            showCover(call, state, binding.cover)
            showBossesPattern(call, state, binding.bosses)
        }

        is LeatherBinding -> {
            showCover(call, state, binding.cover)
            field("Leather Color", binding.leatherColor)
            fieldLink("Leather Material", call, state, binding.leatherMaterial)
            field("Leather Binding", binding.type)
        }
    }
}

private fun HtmlBlockTag.showCover(
    call: ApplicationCall,
    state: State,
    cover: BookCover,
) {
    field("Cover Color", cover.color)
    fieldLink("Cover Material", call, state, cover.material)
}

private fun HtmlBlockTag.showBossesPattern(
    call: ApplicationCall,
    state: State,
    pattern: BossesPattern,
) {
    field("Bosses Pattern Type", pattern.getType())

    when (pattern) {
        NoBosses -> doNothing()
        is SimpleBossesPattern -> {
            field("Bosses Shape", pattern.shape)
            field("Bosses Size", pattern.size)
            field("Bosses Color", pattern.color)
            fieldLink("Bosses Material", call, state, pattern.material)
            field("Bosses Pattern", pattern.pattern.toString())
        }
    }
}

private fun HtmlBlockTag.showSewingPattern(pattern: SewingPattern) {
    field("Sewing Pattern", pattern.getType())

    when (pattern) {
        is SimpleSewingPattern -> {
            field("Sewing Color", pattern.color)
            field("Sewing Size", pattern.size)
            field("Sewing Length", pattern.length)
            showList("Stitches", pattern.stitches) { stitch ->
                +stitch.name
            }
        }

        is ComplexSewingPattern -> {
            showList(pattern.stitches) { complex ->
                field("Color", complex.color)
                field("Size", complex.size)
                field("Length", complex.length)
                field("Stitch", complex.stitch)
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
) {
    selectValue("Format", FORMAT, TextFormatType.entries, format.getType(), true)

    when (format) {
        UndefinedTextFormat -> doNothing()
        is Book -> {
            selectInt("Pages", format.pages, MIN_PAGES, 10000, 1, PAGES)
            editBinding(state, format.binding)
            selectSize(SIZE, format.size, min, max, step, true)
        }

        is Scroll -> {
            selectDistance("Roll Length", LENGTH, format.rollLength, min, max, step, true)
            selectDistance("Roll Diameter", LENGTH, format.rollDiameter, min, max, step, true)
            selectColor("Scroll Color", COLOR, Color.entries, format.color)
            selectValue("Scroll Material", MATERIAL, state.getMaterialStorage().getAll()) { material ->
                label = material.name
                value = material.id.value.toString()
                selected = material.id == format.material
            }
            editScrollFormat(state, format.format)
        }
    }
}

private fun FORM.editBinding(
    state: State,
    binding: BookBinding,
) {
    selectValue("Binding", BINDING, BookBindingType.entries, binding.getType(), true)

    when (binding) {
        is CopticBinding -> {
            editCover(state, binding.cover)
            editSewingPattern(binding.sewingPattern)
        }

        is Hardcover -> {
            editCover(state, binding.cover)
            editBossesPattern(state, binding.bosses)
        }

        is LeatherBinding -> {
            editCover(state, binding.cover)
            selectColor("Leather Color", combine(LEATHER, BINDING, COLOR), Color.entries, binding.leatherColor)
            selectValue(
                "Leather Material",
                combine(LEATHER, MATERIAL),
                state.getMaterialStorage().getAll(),
                false
            ) { material ->
                label = material.name
                value = material.id.value.toString()
                selected = material.id == binding.leatherMaterial
            }
            selectValue("Leather Binding", combine(LEATHER, BINDING), LeatherBindingType.entries, binding.type, true)
        }
    }
}

private fun FORM.editCover(
    state: State,
    cover: BookCover,
) {
    selectColor("Cover Color", combine(COVER, BINDING, COLOR), Color.entries, cover.color)
    selectValue("Cover Material", combine(COVER, MATERIAL), state.getMaterialStorage().getAll()) { material ->
        label = material.name
        value = material.id.value.toString()
        selected = material.id == cover.material
    }
}

private fun FORM.editBossesPattern(
    state: State,
    bosses: BossesPattern,
) {
    selectValue("Bosses Pattern", BOSSES, BossesPatternType.entries, bosses.getType(), true)

    when (bosses) {
        is NoBosses -> doNothing()
        is SimpleBossesPattern -> {
            selectValue("Bosses Shape", combine(BOSSES, SHAPE), BossesShape.entries, bosses.shape, true)
            selectValue("Bosses Size", combine(BOSSES, SIZE), Size.entries, bosses.size, true)
            selectColor("Bosses Color", combine(BOSSES, COLOR), Color.entries, bosses.color)
            selectValue("Bosses Material", combine(BOSSES, MATERIAL), state.getMaterialStorage().getAll()) { material ->
                label = material.name
                value = material.id.value.toString()
                selected = material.id == bosses.material
            }
            selectInt("Bosses Pattern Size", bosses.pattern.size, 1, 20, 1, combine(BOSSES, NUMBER), true)

            showListWithIndex(bosses.pattern) { index, count ->
                val countParam = combine(BOSSES, index)
                selectInt("Count", count, 1, 20, 1, countParam, true)
            }
        }
    }
}

private fun FORM.editSewingPattern(pattern: SewingPattern) {
    selectValue("Sewing Pattern", SEWING, SewingPatternType.entries, pattern.getType(), true)

    when (pattern) {
        is SimpleSewingPattern -> {
            selectColor("Sewing Color", combine(SEWING, COLOR), Color.entries, pattern.color)
            selectValue("Sewing Size", combine(SEWING, SIZE), Size.entries, pattern.size, true)
            selectValue("Sewing Length", combine(SEWING, LENGTH), Size.entries, pattern.length, true)
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
                selectValue("Length", combine(stitchParam, LENGTH), Size.entries, complex.length, true)
                selectValue("Stitch", stitchParam, StitchType.entries, complex.stitch, true)
            }
        }
    }
}

private fun FORM.editSewingPatternSize(size: Int) {
    selectInt("Sewing Pattern Size", size, MIN_STITCHES, 20, 1, combine(SEWING, NUMBER), true)
}

private fun FORM.editScrollFormat(
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

private fun FORM.editScrollHandle(
    state: State,
    handle: ScrollHandle,
) {
    selectValue("Handle Material", combine(HANDLE, MATERIAL), state.getMaterialStorage().getAll()) { material ->
        label = material.name
        value = material.id.value.toString()
        selected = material.id == handle.material
    }
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
    val count = parseInt(parameters, combine(BOSSES, NUMBER), 0)

    return (0..<count)
        .map { index ->
            parseInt(parameters, combine(BOSSES, index), 1)
        }
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
    val count = parseInt(parameters, combine(SEWING, NUMBER), 0)

    return (0..<count)
        .map { index ->
            parse(parameters, combine(SEWING, index), StitchType.Kettle)
        }
}

private fun parseComplexPattern(parameters: Parameters): List<ComplexStitch> {
    val count = parseInt(parameters, combine(SEWING, NUMBER), 0)

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
