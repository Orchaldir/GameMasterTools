package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.name.NotEmptyString
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ContentEntryType {
    Paragraph,
    Quote,
}

@Serializable
sealed class ContentEntry {

    fun getType() = when (this) {
        is Paragraph -> ContentEntryType.Paragraph
        is QuoteEntry -> ContentEntryType.Quote
    }

    fun <ID : Id<ID>> isSourceOfQuote(id: ID) = when (this) {
        is Paragraph -> false
        is QuoteEntry -> source.isId(id)
    }
}

@Serializable
@SerialName("Paragraph")
data class Paragraph(
    val text: NotEmptyString,
) : ContentEntry() {

    companion object {
        fun fromString(text: String) = Paragraph(NotEmptyString.init(text))
    }

}

@Serializable
@SerialName("Quote")
data class QuoteEntry(
    val text: NotEmptyString,
    val source: Creator = UndefinedCreator,
) : ContentEntry() {

    companion object {
        fun fromString(text: String, creator: Creator = UndefinedCreator) =
            QuoteEntry(NotEmptyString.init(text), creator)
    }

}
