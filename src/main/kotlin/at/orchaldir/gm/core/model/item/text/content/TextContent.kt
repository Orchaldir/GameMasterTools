package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.magic.SpellId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TextContentType {
    AbstractChapters,
    AbstractText,
    Undefined,
}

@Serializable
sealed class TextContent {

    fun getType() = when (this) {
        is AbstractChapters -> TextContentType.AbstractChapters
        is AbstractText -> TextContentType.AbstractText
        UndefinedTextContent -> TextContentType.Undefined
    }

    fun pages() = when (this) {
        is AbstractChapters -> chapters.fold(0) { sum, chapter -> sum + chapter.content.pages }
        is AbstractText -> content.pages
        UndefinedTextContent -> 0
    }

    fun spells() = when (this) {
        is AbstractChapters -> chapters.fold(setOf()) { sum, chapter -> sum + chapter.content.spells }
        is AbstractText -> content.spells
        UndefinedTextContent -> emptySet()
    }

    fun contains(spell: SpellId) = when (this) {
        is AbstractChapters -> chapters.any { it.content.spells.contains(spell) }
        is AbstractText -> content.spells.contains(spell)
        UndefinedTextContent -> false
    }
}

@Serializable
@SerialName("Abstract")
data class AbstractText(
    val content: AbstractContent,
) : TextContent()

@Serializable
@SerialName("AbstractChapters")
data class AbstractChapters(
    val chapters: List<AbstractChapter> = emptyList(),
) : TextContent()

@Serializable
@SerialName("Undefined")
data object UndefinedTextContent : TextContent()
