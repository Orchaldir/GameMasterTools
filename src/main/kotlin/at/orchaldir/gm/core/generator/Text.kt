package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.RarityMap
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.RandomNumberGenerator
import kotlin.random.Random

data class TextGenerator(
    val generator: NumberGenerator,
    val rarityGenerator: RarityGenerator,
    val exampleStrings: List<String>,
) {

    companion object {
        fun create(
            exampleStrings: List<String>,
            rarityGenerator: RarityGenerator,
            text: Int,
            chapter: Int = 0,
        ): TextGenerator {
            val seed = text * 1000 + chapter

            return TextGenerator(
                RandomNumberGenerator(Random(seed)),
                rarityGenerator,
                exampleStrings,
            )
        }
    }

    fun <T> generate(map: RarityMap<T>) = rarityGenerator.generate(map, generator)

    fun generateString(style: ParagraphGeneration): String {
        val sentences = generator.getNumber(style.minLength, style.maxLength + 1)

        return (0..<sentences)
            .joinToString(" ") { generator.select(exampleStrings) }
    }

    fun generateNotEmptyString(style: ParagraphGeneration) =
        NotEmptyString.init(generateString(style))

    fun generateParagraph(style: ContentStyle) =
        Paragraph.fromString(generateString(style.generation.main))

    fun generateParagraphs(style: ContentStyle, minParagraphs: Int, maxParagraphs: Int): List<ContentEntry> {
        val paragraphs = generator.getNumber(minParagraphs, maxParagraphs + 1)

        return (0..<paragraphs)
            .map { generateParagraph(style) }
    }

    fun generateQuote(style: ContentStyle) =
        SimpleQuote.fromString(generateString(style.generation.quote))

    fun generateEntry(style: ContentStyle) = when (generate(style.generation.rarity)) {
        ContentEntryType.Paragraph -> generateParagraph(style)
        ContentEntryType.SimpleQuote, ContentEntryType.LinkedQuote -> generateQuote(style)
    }

    fun generateEntries(style: ContentStyle, minEntries: Int, maxEntries: Int): List<ContentEntry> {
        val entries = generator.getNumber(minEntries, maxEntries + 1)

        return (0..<entries)
            .map { generateEntry(style) }
    }
}
