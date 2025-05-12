package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Rarity
import kotlinx.serialization.Serializable

const val MIN_PARAGRAPH_LENGTH = 8
const val MAX_PARAGRAPH_LENGTH = 16
const val MIN_QUOTE_LENGTH = 1
const val MAX_QUOTE_LENGTH = 3

@Serializable
data class ParagraphGeneration(
    val minLength: Int = MIN_PARAGRAPH_LENGTH,
    val maxLength: Int = MAX_PARAGRAPH_LENGTH,
)

@Serializable
data class ContentGeneration(
    val main: ParagraphGeneration = ParagraphGeneration(),
    val quote: ParagraphGeneration = ParagraphGeneration(MIN_QUOTE_LENGTH, MAX_QUOTE_LENGTH),
    val rarity: OneOf<ContentEntryType> = OneOf.init(
        mapOf(
            ContentEntryType.Paragraph to Rarity.Common,
            ContentEntryType.Quote to Rarity.Rare,
        )
    ),
)