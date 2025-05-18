package at.orchaldir.gm.core.model.item.text

import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TextOriginType {
    Original,
    Translation,
}

@Serializable
sealed class TextOrigin : Creation {

    fun getType() = when (this) {
        is OriginalText -> TextOriginType.Original
        is TranslatedText -> TextOriginType.Translation
    }

    override fun creator() = when (this) {
        is OriginalText -> author
        is TranslatedText -> translator
    }

    fun <ID : Id<ID>> wasTranslatedBy(id: ID) = when (this) {
        is TranslatedText -> translator.isId(id)
        else -> false
    }

    fun isTranslationOf(id: TextId) = when (this) {
        is TranslatedText -> text == id
        else -> false
    }

    fun <ID : Id<ID>> wasWrittenBy(id: ID) = when (this) {
        is OriginalText -> author.isId(id)
        else -> false
    }
}

@Serializable
@SerialName("Original")
data class OriginalText(val author: Creator) : TextOrigin()

@Serializable
@SerialName("Translation")
data class TranslatedText(
    val text: TextId,
    val translator: Creator,
) : TextOrigin()
