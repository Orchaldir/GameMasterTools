package at.orchaldir.gm.app.html.util.quote

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.model.util.quote.QuoteType
import at.orchaldir.gm.core.selector.item.getTextsContaining
import at.orchaldir.gm.core.selector.item.periodical.getArticlesContaining
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showQuote(
    call: ApplicationCall,
    state: State,
    quote: Quote,
) {
    val articles = state.getArticlesContaining(quote.id)
    val texts = state.getTextsContaining(quote.id)

    field("Text", quote.text)
    optionalField("Description", quote.description)
    field("Type", quote.type)
    fieldReference(call, state, quote.source, "Source")
    optionalField(call, state, "Date", quote.date)
    fieldElements(call, state, articles)
    fieldElements(call, state, texts)
}

// edit

fun HtmlBlockTag.editQuote(
    call: ApplicationCall,
    state: State,
    quote: Quote,
) {
    editTextArea(
        NAME,
        90,
        10,
        quote.text.text,
    )
    editTextArea(
        "Description",
        TEXT,
        90,
        10,
        quote.description?.text ?: "",
    )
    selectValue("Type", TYPE, QuoteType.entries, quote.type)
    selectCreator(
        state,
        quote.source,
        quote.id,
        null,
        "Source",
    )
    selectOptionalDate(state, "Date", quote.date, DATE)
}

// parse

fun parseQuoteId(value: String) = QuoteId(value.toInt())
fun parseQuoteId(parameters: Parameters, param: String) = QuoteId(parseInt(parameters, param))


fun parseQuote(state: State, parameters: Parameters, id: QuoteId) = Quote(
    id,
    parseNotEmptyString(parameters, NAME, "Text"),
    parseOptionalNotEmptyString(parameters, TEXT),
    parse(parameters, TYPE, QuoteType.Quote),
    parseCreator(parameters, CREATOR),
    parseOptionalDate(parameters, state, DATE),
)
