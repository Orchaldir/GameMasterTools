package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.*
import at.orchaldir.gm.app.html.util.part.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.book.typography.Typography
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBinding(
    call: ApplicationCall,
    state: State,
    binding: BookBinding,
) {
    showDetails("Binding") {
        field("Type", binding.getType())

        when (binding) {
            is CopticBinding -> {
                showItemPart(call, state, binding.cover, "Cover")
                showSewingPattern(call, state, binding.sewingPattern)
            }

            is Hardcover -> {
                showItemPart(call, state, binding.cover, "Cover")
                showBossesPattern(call, state, binding.bosses)
                showEdgeProtection(call, state, binding.protection)
            }

            is LeatherBinding -> {
                showItemPart(call, state, binding.cover, "Cover")
                showItemPart(call, state, binding.leather, "Leather")
                field("Leather Binding", binding.style)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editBinding(
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
                editItemPart(state, binding.leather, LEATHER, "Leather", BOOK_COVER_MATERIALS)
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
    cover: ItemPart,
    typography: Typography,
    hasAuthor: Boolean,
) {
    showDetails("Cover", true) {
        editItemPart(state, cover, COVER, allowedTypes = BOOK_COVER_MATERIALS)
        editTypography(state, typography, hasAuthor)
    }
}

// parse

fun parseBinding(parameters: Parameters) = when (parse(parameters, BINDING, BookBindingType.Hardcover)) {
    BookBindingType.Coptic -> CopticBinding(
        parseItemPart(parameters, COVER, BOOK_COVER_MATERIALS),
        parseTextTypography(parameters),
        parseSewing(parameters),
    )

    BookBindingType.Hardcover -> Hardcover(
        parseItemPart(parameters, COVER, BOOK_COVER_MATERIALS),
        parseTextTypography(parameters),
        parseBosses(parameters),
        parseEdgeProtection(parameters),
    )

    BookBindingType.Leather -> LeatherBinding(
        parse(parameters, combine(LEATHER, BINDING), LeatherBindingStyle.Half),
        parseItemPart(parameters, COVER, BOOK_COVER_MATERIALS),
        parseItemPart(parameters, LEATHER, BOOK_COVER_MATERIALS),
        parseTextTypography(parameters),
    )
}
