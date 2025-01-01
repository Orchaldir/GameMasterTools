package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.book.Book
import at.orchaldir.gm.core.model.item.book.BookId
import at.orchaldir.gm.core.model.language.LanguageId

fun State.canDeleteBook(book: BookId) = true

fun State.countBooks(language: LanguageId) = getBookStorage()
    .getAll()
    .count { c -> c.language == language }

fun countLanguages(books: Collection<Book>) = books
    .groupingBy { it.language }
    .eachCount()

fun State.getBooks(language: LanguageId) = getBookStorage()
    .getAll()
    .filter { c -> c.language == language }

