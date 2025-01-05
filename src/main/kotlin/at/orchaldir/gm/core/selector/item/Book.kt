package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.utils.Id

fun State.canDeleteBook(book: TextId) = getTranslationsOf(book).isEmpty()

fun State.countBooks(language: LanguageId) = getTextStorage()
    .getAll()
    .count { c -> c.language == language }

fun countLanguages(texts: Collection<Text>) = texts
    .groupingBy { it.language }
    .eachCount()

fun countBookOriginTypes(texts: Collection<Text>) = texts
    .groupingBy { it.origin.getType() }
    .eachCount()

fun State.getBooks(language: LanguageId) = getTextStorage()
    .getAll()
    .filter { b -> b.language == language }

fun State.getTranslationsOf(book: TextId) = getTextStorage()
    .getAll()
    .filter { b -> b.origin.isTranslationOf(book) }

fun <ID : Id<ID>> State.getBooksTranslatedBy(id: ID) = getTextStorage()
    .getAll()
    .filter { it.origin.wasTranslatedBy(id) }

fun <ID : Id<ID>> State.getBooksWrittenBy(id: ID) = getTextStorage()
    .getAll()
    .filter { it.origin.wasWrittenBy(id) }

