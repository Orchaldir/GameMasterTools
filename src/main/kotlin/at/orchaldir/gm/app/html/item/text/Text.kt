package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.BUSINESS
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.parseLanguageId
import at.orchaldir.gm.app.html.economy.parseOptionalBusinessId
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.ALLOWED_TEXT_ORIGINS
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.selector.item.getTranslationsOf
import at.orchaldir.gm.core.selector.item.hasAuthor
import at.orchaldir.gm.core.selector.util.getExistingElements
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showText(
    call: ApplicationCall,
    state: State,
    text: Text,
) {
    fieldOrigin(call, state, text.origin, ::TextId)
    optionalField(call, state, "Date", text.date)
    optionalFieldLink("Publisher", call, state, text.publisher)
    fieldLink("Language", call, state, text.language)
    showTextFormat(call, state, text.format)
    showTextContent(call, state, text.content)

    fieldList("Translations", state.getTranslationsOf(text.id)) { text ->
        link(call, text.id, text.getNameWithDate(state))
    }

    showDataSources(call, state, text.sources)
}

// edit

fun HtmlBlockTag.editText(
    call: ApplicationCall,
    state: State,
    text: Text,
) {
    val hasAuthor = state.hasAuthor(text)
    val languages = state.getLanguageStorage().getAll()
    val businesses = state.getExistingElements(state.getBusinessStorage(), text.date)

    selectName(text.name)
    editOrigin(
        state,
        text.id,
        text.origin,
        text.date,
        ALLOWED_TEXT_ORIGINS,
        ::TextId,
    )
    selectOptionalDate(state, "Date", text.date, DATE)
    selectOptionalElement(state, "Publisher", BUSINESS, businesses, text.publisher)
    selectElement(state, LANGUAGE, languages, text.language)
    editTextFormat(state, text.format, hasAuthor)
    editTextContent(state, text.content)
    editDataSources(state, text.sources)
}

// parse

fun parseTextId(parameters: Parameters, param: String) = TextId(parseInt(parameters, param))

fun parseText(
    state: State,
    parameters: Parameters,
    id: TextId,
) =
    Text(
        id,
        parseName(parameters),
        parseOrigin(parameters),
        parseOptionalBusinessId(parameters, BUSINESS),
        parseOptionalDate(parameters, state, DATE),
        parseLanguageId(parameters, LANGUAGE),
        parseTextFormat(parameters),
        parseTextContent(parameters),
        parseDataSources(parameters),
    )