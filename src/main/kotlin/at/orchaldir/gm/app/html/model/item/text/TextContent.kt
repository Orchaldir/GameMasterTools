package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.CONTENT
import at.orchaldir.gm.app.PAGES
import at.orchaldir.gm.app.SPELLS
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.magic.parseSpellId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.AbstractText
import at.orchaldir.gm.core.model.item.text.TextContent
import at.orchaldir.gm.core.model.item.text.TextContentType
import at.orchaldir.gm.core.model.item.text.UndefinedTextContent
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.selector.util.sortSpells
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag


// show

fun HtmlBlockTag.showTextContent(
    call: ApplicationCall,
    state: State,
    content: TextContent,
) {
    showDetails("Content") {
        field("Type", content.getType())

        when (content) {
            is AbstractText -> {
                field("Pages", content.pages)
                showList("Spell", content.spells) { spell ->
                    link(call, state, spell)
                }
            }

            UndefinedTextContent -> doNothing()
        }
    }
}

// edit

fun FORM.editTextContent(
    state: State,
    content: TextContent,
) {
    showDetails("Content", true) {
        selectValue("Type", CONTENT, TextContentType.entries, content.getType(), true)

        when (content) {
            UndefinedTextContent -> doNothing()
            is AbstractText -> {
                selectInt("Pages", content.pages, 1, 10000, 1, combine(CONTENT, PAGES))
                editSpells(state, content.spells)
            }
        }
    }
}

private fun HtmlBlockTag.editSpells(
    state: State,
    spells: Set<SpellId>,
) {
    showDetails("Spells", true) {
        selectElements(state, SPELLS, state.sortSpells(), spells)
    }
}

// parse

fun parseTextContent(parameters: Parameters) = when (parse(parameters, CONTENT, TextContentType.Undefined)) {
    TextContentType.AbstractText -> AbstractText(
        parseInt(parameters, PAGES, 100),
        parseElements(parameters, SPELLS) { parseSpellId(it) },
    )

    TextContentType.Undefined -> UndefinedTextContent
}
