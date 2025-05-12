package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.item.text.content.ContentEntry
import at.orchaldir.gm.core.model.item.text.content.ContentStyle
import at.orchaldir.gm.core.model.item.text.content.Paragraph
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.RandomNumberGenerator
import kotlin.random.Random

data class TextGenerator(
    val generator: NumberGenerator,
    val exampleStrings: List<String>,
) {

    companion object {
        fun create(exampleStrings: List<String>, text: Int, chapter: Int = 0): TextGenerator {
            val seed = text * 1000 + chapter

            return TextGenerator(
                RandomNumberGenerator(Random(seed)),
                exampleStrings,
            )
        }
    }

    fun generateParagraphAsString(style: ContentStyle): String {
        val sentences = generator.getNumber(style.minParagraphLength, style.maxParagraphLength + 1)

        return (0..<sentences)
            .joinToString(" ") { generator.select(exampleStrings) }
    }

    fun generateParagraph(style: ContentStyle) =
        Paragraph.fromString(generateParagraphAsString(style))

    fun generateParagraphs(style: ContentStyle, minParagraphs: Int, maxParagraphs: Int): List<ContentEntry> {
        val paragraphs = generator.getNumber(minParagraphs, maxParagraphs + 1)

        return (0..<paragraphs)
            .map { generateParagraph(style) }
    }

}
