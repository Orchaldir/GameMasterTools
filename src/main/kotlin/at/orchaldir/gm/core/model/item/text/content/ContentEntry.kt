package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.name.NotEmptyString
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
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
        is Quote -> ContentEntryType.Quote
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
data class Quote(
    val text: NotEmptyString,
    val creator: Creator = UndefinedCreator,
) : ContentEntry() {

    companion object {
        fun fromString(text: String, creator: Creator = UndefinedCreator) =
            Quote(NotEmptyString.init(text), creator)
    }

}
