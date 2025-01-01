package at.orchaldir.gm.app.parse.item

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.book.Book
import at.orchaldir.gm.core.model.item.book.BookId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseBookId(value: String) = BookId(value.toInt())

fun parseBookId(parameters: Parameters, param: String) = BookId(parseInt(parameters, param))

fun parseBook(parameters: Parameters, state: State, id: BookId) =
    Book(
        id,
        parameters.getOrFail(NAME),
        parseOptionalDate(parameters, state, DATE),
    )
