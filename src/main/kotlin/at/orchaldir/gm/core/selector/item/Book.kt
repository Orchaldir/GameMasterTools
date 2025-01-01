package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.book.Book
import at.orchaldir.gm.core.model.item.book.BookId

fun State.canDeleteBook(book: BookId) = true

fun countLanguages(books: Collection<Book>) = books
    .groupingBy { it.language }
    .eachCount()

