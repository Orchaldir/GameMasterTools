package at.orchaldir.gm.core.model.item.text

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
value class TextId(val value: Int) : Id<TextId> {

    override fun next() = TextId(value + 1)
    override fun type() = BOOK_TYPE
    override fun value() = value

}

@Serializable
data class Text(
    val id: TextId,
    val name: String = "Book ${id.value}",
    val origin: BookOrigin = OriginalBook(UndefinedCreator),
    val date: Date? = null,
    val language: LanguageId = LanguageId(0),
    val format: BookFormat = UndefinedBookFormat,
) : ElementWithSimpleName<TextId>, Created {

    override fun id() = id
    override fun name() = name

    override fun creator() = origin.creator()

}