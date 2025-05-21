package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.SpellId
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.quote.QuoteId
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
        is SimpleChapters -> TextContentType.Chapters
        UndefinedTextContent -> TextContentType.Undefined
    }

    fun pages() = when (this) {
        is AbstractChapters -> countPages(this.chapters, this.tableOfContents)
        is AbstractText -> content.pages
        is SimpleChapters -> countPages(this.chapters, this.tableOfContents)
        UndefinedTextContent -> 0
    }

    fun spells() = when (this) {
        is AbstractChapters -> chapters.fold(setOf()) { sum, chapter -> sum + chapter.content.spells }
        is AbstractText -> content.spells
        is SimpleChapters -> emptySet()
        UndefinedTextContent -> emptySet()
    }

    fun contains(font: FontId) = when (this) {
        is AbstractChapters -> style.contains(font) || pageNumbering.contains(font) || tableOfContents.contains(font)
        is AbstractText -> style.contains(font) || pageNumbering.contains(font)
        is SimpleChapters -> style.contains(font) || pageNumbering.contains(font) || tableOfContents.contains(font)
        UndefinedTextContent -> false
    }

    fun contains(quote: QuoteId) = when (this) {
        is SimpleChapters -> chapters.any { chapter ->
            chapter.entries.any { entry ->
                entry.contains(quote)
            }
        }

        else -> false
    }

    fun contains(spell: SpellId) = when (this) {
        is AbstractChapters -> chapters.any { it.content.spells.contains(spell) }
        is AbstractText -> content.spells.contains(spell)
        is SimpleChapters -> false
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
@SerialName("SimpleChapters")
data class SimpleChapters(
    val chapters: List<SimpleChapter> = emptyList(),
    val style: ContentStyle = ContentStyle(),
    val pageNumbering: PageNumbering = NoPageNumbering,
    val tableOfContents: TableOfContents = NoTableOfContents,
) : TextContent()

@Serializable
@SerialName("Undefined")
data object UndefinedTextContent : TextContent()

private fun countPages(
    chapters: List<Chapter>,
    toc: TableOfContents,
) = chapters.fold(0) { sum, chapter -> sum + chapter.pages() } + toc.pages()