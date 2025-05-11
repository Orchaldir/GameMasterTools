package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.name.NotEmptyString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ContentEntryType {
    Paragraph,
}

@Serializable
sealed class ContentEntry {

    fun getType() = when (this) {
        is Paragraph -> ContentEntryType.Paragraph
    }
}

@Serializable
@SerialName("Paragraph")
data class Paragraph(
    val text: NotEmptyString,
) : ContentEntry()
