package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.CREATOR
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.editList
import at.orchaldir.gm.app.html.editTextArea
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.model.fieldCreator
import at.orchaldir.gm.app.html.model.parseCreator
import at.orchaldir.gm.app.html.model.selectCreator
import at.orchaldir.gm.app.html.parseList
import at.orchaldir.gm.app.html.parseNotEmptyString
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.ArticleId
import at.orchaldir.gm.core.model.item.text.content.ContentEntry
import at.orchaldir.gm.core.model.item.text.content.ContentEntryType
import at.orchaldir.gm.core.model.item.text.content.Paragraph
import at.orchaldir.gm.core.model.item.text.content.QuoteEntry
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
            is QuoteEntry -> {
                field("Text", entry.text)
                fieldCreator(call, state, entry.source, "Source")
            }
        }
    }
}

// edit

fun HtmlBlockTag.editContentEntries(
    state: State,
    entries: List<ContentEntry>,
    param: String,
) {
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
            ContentEntryType.entries,
            entry.getType(),
        )

        when (entry) {
            is Paragraph -> editText(entryParam, entry.text)
            is QuoteEntry -> {
                editText(entryParam, entry.text)
                selectCreator(
                    state,
                    entry.source,
                    IGNORE,
                    null,
                    "Source",
                    combine(entryParam, CREATOR),
                )
            }
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

        ContentEntryType.Quote -> QuoteEntry(
            parseNotEmptyString(parameters, param, "Text"),
            parseCreator(parameters, combine(param, CREATOR))
        )
    }