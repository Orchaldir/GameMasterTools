package at.orchaldir.gm.app.html.model.text

import at.orchaldir.gm.app.CONTENT
import at.orchaldir.gm.app.FORMAT
import at.orchaldir.gm.app.PAGES
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.AbstractText
import at.orchaldir.gm.core.model.item.text.TextContent
import at.orchaldir.gm.core.model.item.text.TextContentType
import at.orchaldir.gm.core.model.item.text.UndefinedTextContent
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM


// show

fun BODY.showTextContent(
    call: ApplicationCall,
    state: State,
    content: TextContent,
) {
    showDetails("Content") {
        field("Type", content.getType())

        when (content) {
            is AbstractText -> {
                field("Pages", content.pages)
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
    showDetails("Text Format", true) {
        selectValue("Type", CONTENT, TextContentType.entries, content.getType(), true)

        when (content) {
            UndefinedTextContent -> doNothing()
            is AbstractText -> {
                selectInt("Pages", content.pages, 1, 10000, 1, combine(CONTENT, PAGES))
            }
        }
    }
}


// parse

fun parseTextContent(parameters: Parameters) = when (parse(parameters, CONTENT, TextContentType.Undefined)) {
    TextContentType.AbstractText -> AbstractText(
        parseInt(parameters, PAGES, 100),
    )

    TextContentType.Undefined -> UndefinedTextContent
}
