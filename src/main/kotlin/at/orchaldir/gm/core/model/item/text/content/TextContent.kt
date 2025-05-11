package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.magic.SpellId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TextContentType {
    AbstractChapters,
    AbstractText,
    Chapters,
    Undefined,
}

@Serializable
sealed class TextContent {

    fun getType() = when (this) {
        is AbstractChapters -> TextContentType.AbstractChapters
        is AbstractText -> TextContentType.AbstractText
        is Chapters -> TextContentType.Chapters
        UndefinedTextContent -> TextContentType.Undefined
    }

    fun pages() = when (this) {
        is AbstractChapters -> chapters.fold(0) { sum, chapter -> sum + chapter.content.pages } +
                tableOfContents.pages()

        is AbstractText -> content.pages
        is Chapters -> pages
        UndefinedTextContent -> 0
    }

    fun spells() = when (this) {
        is AbstractChapters -> chapters.fold(setOf()) { sum, chapter -> sum + chapter.content.spells }
        is AbstractText -> content.spells
        is Chapters -> emptySet()
        UndefinedTextContent -> emptySet()
    }

    fun contains(font: FontId) = when (this) {
        is AbstractChapters -> style.contains(font) || pageNumbering.contains(font) || tableOfContents.contains(font)
        is AbstractText -> style.contains(font) || pageNumbering.contains(font)
        is Chapters -> style.contains(font) || pageNumbering.contains(font)
        UndefinedTextContent -> false
    }

    fun contains(spell: SpellId) = when (this) {
        is AbstractChapters -> chapters.any { it.content.spells.contains(spell) }
        is AbstractText -> content.spells.contains(spell)
        is Chapters -> false
        UndefinedTextContent -> false
    }
}

@Serializable
@SerialName("Abstract")
data class AbstractText(
    val content: AbstractContent = AbstractContent(),
    val style: ContentStyle = ContentStyle(),
    val pageNumbering: PageNumbering = NoPageNumbering,
) : TextContent()

@Serializable
@SerialName("AbstractChapters")
data class AbstractChapters(
    val chapters: List<AbstractChapter> = emptyList(),
    val style: ContentStyle = ContentStyle(),
    val pageNumbering: PageNumbering = NoPageNumbering,
    val tableOfContents: TableOfContents = NoTableOfContents,
) : TextContent()

@Serializable
@SerialName("Chapters")
data class Chapters(
    val chapters: List<Chapter> = emptyList(),
    val style: ContentStyle = ContentStyle(),
    val pages: Int = 0, // auto calculated
    val pageNumbering: PageNumbering = NoPageNumbering,
    val tableOfContents: TableOfContents = NoTableOfContents,
) : TextContent()

@Serializable
@SerialName("Undefined")
data object UndefinedTextContent : TextContent()
