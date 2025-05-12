package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.QUOTE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.editList
import at.orchaldir.gm.app.html.editTextArea
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.model.parseQuoteId
import at.orchaldir.gm.app.html.parseList
import at.orchaldir.gm.app.html.parseNotEmptyString
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.ArticleId
import at.orchaldir.gm.core.model.item.text.content.ContentEntry
import at.orchaldir.gm.core.model.item.text.content.ContentEntryType
import at.orchaldir.gm.core.model.item.text.content.LinkedQuote
import at.orchaldir.gm.core.model.item.text.content.Paragraph
import at.orchaldir.gm.core.model.item.text.content.SimpleQuote
import at.orchaldir.gm.core.model.name.NotEmptyString
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import kotlinx.html.HtmlBlockTag

val IGNORE = ArticleId(Int.MAX_VALUE)

// show

fun HtmlBlockTag.showContentEntries(
    call: ApplicationCall,
    state: State,
    entries: List<ContentEntry>,
) {
    fieldList("Entries", entries) { entry ->
        when (entry) {
            is Paragraph -> field("Text", entry.text)
            is SimpleQuote -> field("Text", entry.text)
            is LinkedQuote -> fieldLink("Quote", call, state, entry.quote)
        }
    }
}

// edit

fun HtmlBlockTag.editContentEntries(
    state: State,
    entries: List<ContentEntry>,
    param: String,
) {
    val entryTypes = if (state.getQuoteStorage().isEmpty()) {
        ContentEntryType.entries - ContentEntryType.LinkedQuote
    } else {
        ContentEntryType.entries
    }

    editList(
        "Entries",
        param,
        entries,
        1,
        10000,
        1
    ) { index, entryParam, entry ->
        selectValue(
            "Type",
            combine(entryParam, TYPE),
            entryTypes,
            entry.getType(),
        )

        when (entry) {
            is Paragraph -> editText(entryParam, entry.text)
            is SimpleQuote -> editText(entryParam, entry.text)
            is LinkedQuote -> selectElement(
                state,
                "Quote",
                QUOTE,
                state.getQuoteStorage().getAll(),
                entry.quote,
            )
        }
    }
}

private fun HtmlBlockTag.editText(
    entryParam: String,
    string: NotEmptyString,
) {
    editTextArea(
        entryParam,
        90,
        10,
        string.text
    )
}

// parse

fun parseContentEntries(
    parameters: Parameters,
    param: String,
): List<ContentEntry> = parseList(parameters, param, 1) { index, entryParam ->
    parseContentEntry(parameters, entryParam)
}

private fun parseContentEntry(parameters: Parameters, param: String) =
    when (parse(parameters, combine(param, TYPE), ContentEntryType.Paragraph)) {
        ContentEntryType.Paragraph -> Paragraph(
            parseNotEmptyString(parameters, param, "Text"),
        )

        ContentEntryType.SimpleQuote -> SimpleQuote(
            parseNotEmptyString(parameters, param, "Text"),
        )

        ContentEntryType.LinkedQuote -> LinkedQuote(parseQuoteId(parameters, QUOTE))
    }