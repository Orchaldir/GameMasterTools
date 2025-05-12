package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.CREATOR
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.editTextArea
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseNotEmptyString
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.quote.Quote
import at.orchaldir.gm.core.model.quote.QuoteId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showQuote(
    call: ApplicationCall,
    state: State,
    quote: Quote,
) {
    field("Text", quote.text)
    fieldCreator(call, state, quote.source, "Source")
    optionalField(call, state, "Date", quote.date)
}

// edit

fun HtmlBlockTag.editQuote(state: State, quote: Quote) {
    editTextArea(
        NAME,
        90,
        10,
        quote.text.text,
    )
    selectCreator(
        state,
        quote.source,
        quote.id,
        null,
        "Source",
        CREATOR,
    )
    selectOptionalDate(state, "Date", quote.date, DATE)
}

// parse

fun parseQuoteId(value: String) = QuoteId(value.toInt())
fun parseQuoteId(parameters: Parameters, param: String) = QuoteId(parseInt(parameters, param))


fun parseQuote(state: State, parameters: Parameters, id: QuoteId) = Quote(
    id,
    parseNotEmptyString(parameters, NAME, "Text"),
    parseCreator(parameters, CREATOR),
    parseOptionalDate(parameters, state, DATE),
)
