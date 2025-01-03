package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.book.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.server.application.*
import kotlinx.html.BODY


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
    fieldLink("Cover Material", call, state, cover.material)
}
