package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.BookId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.utils.Id

fun State.canDeleteBook(book: BookId) = getTranslationsOf(book).isEmpty()

fun State.countBooks(language: LanguageId) = getBookStorage()
    .getAll()
    .count { c -> c.language == language }

fun countLanguages(books: Collection<Book>) = books
    .groupingBy { it.language }
    .eachCount()

fun countBookOriginTypes(books: Collection<Book>) = books
    .groupingBy { it.origin.getType() }
    .eachCount()

fun State.getBooks(language: LanguageId) = getBookStorage()
    .getAll()
    .filter { b -> b.language == language }

fun State.getTranslationsOf(book: BookId) = getBookStorage()
    .getAll()
    .filter { b -> b.origin.isTranslationOf(book) }

fun <ID : Id<ID>> State.getBooksTranslatedBy(id: ID) = getBookStorage()
    .getAll()
    .filter { it.origin.wasTranslatedBy(id) }

fun <ID : Id<ID>> State.getBooksWrittenBy(id: ID) = getBookStorage()
    .getAll()
    .filter { it.origin.wasWrittenBy(id) }

