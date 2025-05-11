package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.item.text.content.ContentStyle
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

    fun generateParagraph(style: ContentStyle): String {
        val sentences = generator.getNumber(style.minParagraphLength, style.maxParagraphLength + 1)

        return (0..<sentences)
            .joinToString(" ") { generator.select(exampleStrings) }
    }

}
