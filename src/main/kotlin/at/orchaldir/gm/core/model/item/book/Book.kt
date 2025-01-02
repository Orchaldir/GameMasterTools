package at.orchaldir.gm.core.model.item.book

import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.Created
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val BOOK_TYPE = "Book"

@JvmInline
@Serializable
value class BookId(val value: Int) : Id<BookId> {

    override fun next() = BookId(value + 1)
    override fun type() = BOOK_TYPE
    override fun value() = value

}

@Serializable
data class Book(
    val id: BookId,
    val name: String = "Book ${id.value}",
    val origin: BookOrigin = OriginalBook(UndefinedCreator),
    val date: Date? = null,
    val language: LanguageId = LanguageId(0),
) : ElementWithSimpleName<BookId>, Created {

    override fun id() = id
    override fun name() = name

    override fun creator() = origin.creator()

}