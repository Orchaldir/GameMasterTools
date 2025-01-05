package at.orchaldir.gm.app.parse.item

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.parseBookFormat
import at.orchaldir.gm.app.html.model.parseCreator
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseLanguageId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import io.ktor.http.*
import io.ktor.server.util.*

fun parseBookId(value: String) = BookId(value.toInt())

fun parseBookId(parameters: Parameters, param: String) = BookId(parseInt(parameters, param))

fun parseBook(parameters: Parameters, state: State, id: BookId) =
    Book(
        id,
        parameters.getOrFail(NAME),
        parseOrigin(parameters),
        parseOptionalDate(parameters, state, DATE),
        parseLanguageId(parameters, LANGUAGE),
        parseBookFormat(parameters),
    )

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, BookOriginType.Original)) {
    BookOriginType.Original -> OriginalBook(parseCreator(parameters))
    BookOriginType.Translation -> TranslatedBook(
        parseBookId(parameters, combine(ORIGIN, REFERENCE)),
        parseCreator(parameters),
    )
}