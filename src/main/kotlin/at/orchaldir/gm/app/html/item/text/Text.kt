package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.parseOptionalBusinessId
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.selector.item.getTranslationsOf
import at.orchaldir.gm.core.selector.item.hasAuthor
import at.orchaldir.gm.core.selector.util.getExistingElements
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showText(
    call: ApplicationCall,
    state: State,
    text: Text,
) {
    showOrigin(call, state, text)
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

private fun HtmlBlockTag.showOrigin(
    call: ApplicationCall,
    state: State,
    text: Text,
) {
    when (text.origin) {
        is OriginalText -> field("Author") {
            showCreator(call, state, text.origin.author)
        }

        is TranslatedText -> {
            fieldLink("Translation Of", call, state, text.origin.text)
            field("Translator") {
                showCreator(call, state, text.origin.translator)
            }
        }
    }
}

// edit

fun FORM.editText(
    state: State,
    text: Text,
) {
    val hasAuthor = state.hasAuthor(text)
    val languages = state.getLanguageStorage().getAll()
        .sortedBy { it.name.text }
    val businesses = state.getExistingElements(state.getBusinessStorage(), text.date)

    selectName(text.name)
    editOrigin(state, text)
    selectOptionalDate(state, "Date", text.date, DATE)
    selectOptionalElement(state, "Publisher", BUSINESS, businesses, text.publisher)
    selectElement(state, "Language", LANGUAGE, languages, text.language)
    editTextFormat(state, text.format, hasAuthor)
    editTextContent(state, text.content)
    editDataSources(state, text.sources)
}

private fun FORM.editOrigin(
    state: State,
    text: Text,
) {
    selectValue("Origin", ORIGIN, TextOriginType.entries, text.origin.getType())

    when (text.origin) {
        is OriginalText -> selectCreator(state, text.origin.author, text.id, text.date, "Author")
        is TranslatedText -> {
            val otherTexts = state.getTextStorage().getAllExcept(text.id)

            selectElement(state, "Translation Of", combine(ORIGIN, REFERENCE), otherTexts, text.origin.text)
            selectCreator(state, text.origin.translator, text.id, text.date, "Translator")
        }
    }
}

// parse

fun parseTextId(parameters: Parameters, param: String) = TextId(parseInt(parameters, param))

fun parseText(parameters: Parameters, state: State, id: TextId) =
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

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, TextOriginType.Original)) {
    TextOriginType.Original -> OriginalText(parseCreator(parameters))
    TextOriginType.Translation -> TranslatedText(
        parseTextId(parameters, combine(ORIGIN, REFERENCE)),
        parseCreator(parameters),
    )
}