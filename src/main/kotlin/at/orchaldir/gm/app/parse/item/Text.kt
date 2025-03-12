package at.orchaldir.gm.app.parse.item

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGE
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.html.model.item.text.parseTextContent
import at.orchaldir.gm.app.html.model.item.text.parseTextFormat
import at.orchaldir.gm.app.html.model.parseComplexName
import at.orchaldir.gm.app.html.model.parseCreator
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseLanguageId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import io.ktor.http.*

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