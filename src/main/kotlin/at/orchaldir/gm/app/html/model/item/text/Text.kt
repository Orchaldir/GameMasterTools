package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGE
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.optionalField
import at.orchaldir.gm.app.html.model.parseComplexName
import at.orchaldir.gm.app.html.model.parseCreator
import at.orchaldir.gm.app.html.model.parseLanguageId
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.html.model.selectComplexName
import at.orchaldir.gm.app.html.model.selectCreator
import at.orchaldir.gm.app.html.model.selectOptionalDate
import at.orchaldir.gm.app.html.model.showCreator
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showList
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.OriginalText
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.item.text.TextOriginType
import at.orchaldir.gm.core.model.item.text.TranslatedText
import at.orchaldir.gm.core.selector.item.getTranslationsOf
import at.orchaldir.gm.core.selector.item.hasAuthor
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
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
    fieldLink("Language", call, state, text.language)
    showTextFormat(call, state, text.format)
    showTextContent(call, state, text.content)

    showList("Translations", state.getTranslationsOf(text.id)) { text ->
        link(call, text.id, text.getNameWithDate(state))
    }
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
        .sortedBy { it.name }

    selectComplexName(state, text.name)
    editOrigin(state, text)
    selectOptionalDate(state, "Date", text.date, DATE)
    selectElement(state, "Language", LANGUAGE, languages, text.language, true)
    editTextFormat(state, text.format, hasAuthor)
    editTextContent(state, text.content)
}

private fun FORM.editOrigin(
    state: State,
    text: Text,
) {
    selectValue("Origin", ORIGIN, TextOriginType.entries, text.origin.getType(), true)

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
        parseComplexName(parameters),
        parseOrigin(parameters),
        parseOptionalDate(parameters, state, DATE),
        parseLanguageId(parameters, LANGUAGE),
        parseTextFormat(parameters),
        parseTextContent(parameters),
    )

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, TextOriginType.Original)) {
    TextOriginType.Original -> OriginalText(parseCreator(parameters))
    TextOriginType.Translation -> TranslatedText(
        parseTextId(parameters, combine(ORIGIN, REFERENCE)),
        parseCreator(parameters),
    )
}