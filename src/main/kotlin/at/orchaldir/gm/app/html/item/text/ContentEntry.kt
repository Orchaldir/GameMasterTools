package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.QUOTE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.quote.parseQuoteId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

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
            is Paragraph -> editTextArea(entryParam, entry.text)
            is SimpleQuote -> editTextArea(entryParam, entry.text)
            is LinkedQuote -> selectElement(
                state,
                QUOTE,
                state.getQuoteStorage().getAll(),
                entry.quote,
            )
        }
    }
}

private fun HtmlBlockTag.editTextArea(
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