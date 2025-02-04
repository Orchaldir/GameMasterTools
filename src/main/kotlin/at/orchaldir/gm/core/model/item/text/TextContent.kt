package at.orchaldir.gm.core.model.item.text

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TextContentType {
    AbstractText,
    Undefined,
}

@Serializable
sealed class TextContent {

    fun getType() = when (this) {
        is AbstractText -> TextContentType.AbstractText
        UndefinedTextContent -> TextContentType.Undefined
    }
}

@Serializable
@SerialName("AbstractText")
data class AbstractText(
    val pages: Int,
) : TextContent()

@Serializable
@SerialName("Undefined")
data object UndefinedTextContent : TextContent()
