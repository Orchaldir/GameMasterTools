package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseMaterialId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.book.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showBookFormat(
    call: ApplicationCall,
    state: State,
    format: BookFormat,
) {
    field("Format", format.getType())

    when (format) {
        UndefinedBookFormat -> doNothing()
        is Codex -> {
            field("Pages", format.pages)
            showBinding(call, state, format.binding)
        }
    }
}

private fun BODY.showBinding(
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
        }

        is LeatherBinding -> {
            showCover(call, state, binding.cover)
            field("Leather Color", binding.leatherColor)
            fieldLink("Leather Material", call, state, binding.leatherMaterial)
            field("Leather Binding", binding.type)
        }
    }
}

private fun BODY.showCover(
    call: ApplicationCall,
    state: State,
    cover: BookCover,
) {
    field("Cover Color", cover.color)
    fieldLink("Cover Material", call, state, cover.material)
}

private fun BODY.showSewingPattern(pattern: SewingPattern) {
    field("Sewing Pattern", pattern.getType())

    when (pattern) {
        is SimpleSewingPattern -> {
            field("Sewing Color", pattern.color)
            field("Sewing Size", pattern.size)
            showList("Stitches", pattern.stitches) { stitch ->
                +stitch.name
            }
        }

        is ComplexSewingPattern -> {
            showList("Stitches", pattern.stitches) { complex ->
                field("Sewing Color", complex.color)
                field("Sewing Size", complex.size)
                field("Stitch", complex.stitch)
            }
        }
    }
}

// edit

fun FORM.editBookFormat(
    state: State,
    format: BookFormat,
) {
    selectValue("Format", FORMAT, BookFormatType.entries, format.getType(), true)

    when (format) {
        UndefinedBookFormat -> doNothing()
        is Codex -> {
            selectInt("Pages", format.pages, 10, 10000, 1, PAGES)
            editBinding(state, format.binding)
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

private fun FORM.editSewingPattern(pattern: SewingPattern) {
    selectValue("Sewing Pattern", SEWING, SewingPatternType.entries, pattern.getType(), true)

    when (pattern) {
        is SimpleSewingPattern -> {
            selectColor("Sewing Color", combine(SEWING, COLOR), Color.entries, pattern.color)
            selectValue("Sewing Size", combine(SEWING, SIZE), Size.entries, pattern.size, true)
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

                selectColor("Sewing Color", combine(stitchParam, COLOR), Color.entries, complex.color)
                selectValue("Sewing Size", combine(stitchParam, SIZE), Size.entries, complex.size, true)
                selectValue("Stitch", stitchParam, StitchType.entries, complex.stitch, true)
            }
        }
    }
}

private fun FORM.editSewingPatternSize(size: Int) {
    selectInt("Sewing Pattern Size", size, 2, 20, 1, combine(SEWING, NUMBER), true)
}

// parse

fun parseBookFormat(parameters: Parameters) = when (parse(parameters, FORMAT, BookFormatType.Undefined)) {
    BookFormatType.Codex -> Codex(
        parseInt(parameters, PAGES, 100),
        parseBinding(parameters),
    )

    BookFormatType.Undefined -> UndefinedBookFormat
}

private fun parseBinding(parameters: Parameters) = when (parse(parameters, BINDING, BookBindingType.Hardcover)) {
    BookBindingType.Coptic -> CopticBinding(
        parseCover(parameters),
        parseSewing(parameters),
    )

    BookBindingType.Hardcover -> Hardcover(parseCover(parameters))
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

fun parseSewing(parameters: Parameters) = when (parse(parameters, SEWING, SewingPatternType.Simple)) {
    SewingPatternType.Simple -> SimpleSewingPattern(
        parse(parameters, combine(SEWING, COLOR), Color.Crimson),
        parse(parameters, combine(SEWING, SIZE), Size.Medium),
        parseSimplePattern(parameters),
    )

    SewingPatternType.Complex -> TODO()
}

fun parseSimplePattern(parameters: Parameters): List<StitchType> {
    val count = parseInt(parameters, combine(SEWING, NUMBER), 0)

    return (0..<count)
        .map { index ->
            parse(parameters, combine(SEWING, index), StitchType.Kettle)
        }
}
