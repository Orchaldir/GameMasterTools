package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.quote.QuoteId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ContentEntryType {
    Paragraph,
    SimpleQuote,
    LinkedQuote,
}

@Serializable
sealed class ContentEntry {

    fun getType() = when (this) {
        is Paragraph -> ContentEntryType.Paragraph
        is SimpleQuote -> ContentEntryType.SimpleQuote
        is LinkedQuote -> ContentEntryType.LinkedQuote
    }

    fun contains(id: QuoteId) = when (this) {
        is LinkedQuote -> quote == id
        else -> false
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
@SerialName("SimpleQuote")
data class SimpleQuote(
    val text: NotEmptyString,
) : ContentEntry() {

    companion object {
        fun fromString(text: String) = SimpleQuote(NotEmptyString.init(text))
    }

}

@Serializable
@SerialName("LinkedQuote")
data class LinkedQuote(
    val quote: QuoteId,
) : ContentEntry()
