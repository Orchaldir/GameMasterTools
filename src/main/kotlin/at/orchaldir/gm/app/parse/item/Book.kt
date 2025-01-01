package at.orchaldir.gm.app.parse.item

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.item.book.Book
import at.orchaldir.gm.core.model.item.book.BookId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseBookId(value: String) = BookId(value.toInt())

fun parseBookId(parameters: Parameters, param: String) = BookId(parseInt(parameters, param))

fun parseBook(parameters: Parameters, id: BookId): Book {
    val name = parameters.getOrFail(NAME)

    return Book(id, name)
}
