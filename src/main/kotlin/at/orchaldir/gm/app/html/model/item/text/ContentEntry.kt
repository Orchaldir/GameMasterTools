package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.html.editList
import at.orchaldir.gm.app.html.editTextArea
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.parseList
import at.orchaldir.gm.app.html.parseNotEmptyString
import at.orchaldir.gm.core.model.item.text.content.ContentEntry
import at.orchaldir.gm.core.model.item.text.content.Paragraph
import io.ktor.http.Parameters
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showContentEntries(
    entries: List<ContentEntry>,
) {
    fieldList("Entries", entries) { entry ->
        when (entry) {
            is Paragraph -> +entry.text.text
        }
    }
}

// edit

fun HtmlBlockTag.editContentEntries(
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
        when (entry) {
            is Paragraph -> editTextArea(
                entryParam,
                90,
                10,
                entry.text.text
            )
        }
    }
}

// parse

fun parseContentEntries(
    parameters: Parameters,
    param: String,
): List<ContentEntry> = parseList(parameters, param, 1) { index, entryParam ->
    Paragraph(parseNotEmptyString(parameters, entryParam, "Text"))
}