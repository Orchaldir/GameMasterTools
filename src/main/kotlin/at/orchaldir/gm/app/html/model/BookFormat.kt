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
    field("Format", format.getType().name)

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
    field("Binding", binding.getType().name)

    when (binding) {
        is CopticBinding -> {
            showCover(call, state, binding.cover)
        }

        is Hardcover -> {
            showCover(call, state, binding.cover)
        }

        is LeatherBinding -> {
            showCover(call, state, binding.cover)
            field("Leather Color", binding.leatherColor.name)
            fieldLink("Leather Material", call, state, binding.leatherMaterial)
            field("Leather Binding", binding.type.name)
        }
    }
}

private fun BODY.showCover(
    call: ApplicationCall,
    state: State,
    cover: BookCover,
) {
    field("Cover Color", cover.color.name)
    fieldLink("Cover Material", call, state, cover.material)
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

// parse

fun parseBookFormat(parameters: Parameters) = when (parse(parameters, FORMAT, BookFormatType.Undefined)) {
    BookFormatType.Codex -> Codex(
        parseInt(parameters, PAGES, 100),
        parseBinding(parameters),
    )

    BookFormatType.Undefined -> UndefinedBookFormat
}

private fun parseBinding(parameters: Parameters): BookBinding =
    when (parse(parameters, BINDING, BookBindingType.Hardcover)) {
        BookBindingType.Coptic -> CopticBinding(
            parseCover(parameters),
            SimpleSewingPattern(Color.Crimson, Size.Medium, listOf(StitchType.Kettle, StitchType.Kettle))
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