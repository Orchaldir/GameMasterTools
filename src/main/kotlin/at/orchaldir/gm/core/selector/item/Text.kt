package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.text.OriginalText
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.item.text.TranslatedText
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.selector.util.getCreatorName

fun State.canDeleteText(text: TextId) = getTranslationsOf(text).isEmpty()

fun State.countTexts(font: FontId) = getTextStorage()
    .getAll()
    .count { c -> c.contains(font) }

fun State.countTexts(language: LanguageId) = getTextStorage()
    .getAll()
    .count { c -> c.language == language }

fun State.countTexts(material: MaterialId) = getTextStorage()
    .getAll()
    .count { it.contains(material) }

fun State.countTexts(quote: QuoteId) = getTextStorage()
    .getAll()
    .count { it.content.contains(quote) }

fun State.countTexts(spell: SpellId) = getTextStorage()
    .getAll()
    .count { it.content.contains(spell) }

fun countEachTextFormat(texts: Collection<Text>) = texts
    .groupingBy { it.format.getType() }
    .eachCount()

fun countEachLanguage(texts: Collection<Text>) = texts
    .groupingBy { it.language }
    .eachCount()

fun countEachTextOrigin(texts: Collection<Text>) = texts
    .groupingBy { it.origin.getType() }
    .eachCount()

fun State.getAuthorName(id: TextId): String? {
    val original = getOriginal(id)

    return getAuthorName(original)
}

fun State.getAuthorName(text: Text) = when (val origin = getOriginal(text).origin) {
    is OriginalText -> getCreatorName(origin.author)
    else -> error("The original text must be an original text!")
}

fun State.hasAuthor(text: Text) = when (val origin = getOriginal(text).origin) {
    is OriginalText -> origin.author != UndefinedCreator
    else -> error("The original text must be an original text!")
}

fun State.getOriginal(id: TextId): Text {
    val text = getTextStorage().getOrThrow(id)

    return getOriginal(text)
}

fun State.getOriginal(text: Text) = when (text.origin) {
    is OriginalText -> text
    is TranslatedText -> getOriginal(text.origin.text)
}

fun State.getTexts(font: FontId) = getTextStorage()
    .getAll()
    .filter { b -> b.contains(font) }

fun State.getTexts(language: LanguageId) = getTextStorage()
    .getAll()
    .filter { b -> b.language == language }

fun State.getTextsContaining(quote: QuoteId) = getTextStorage()
    .getAll()
    .filter { it.contains(quote) }

fun State.getTextsContaining(spell: SpellId) = getTextStorage()
    .getAll()
    .filter { b -> b.content.contains(spell) }

fun State.getTextsMadeOf(material: MaterialId) = getTextStorage()
    .getAll()
    .filter { it.contains(material) }

fun State.getTranslationsOf(text: TextId) = getTextStorage()
    .getAll()
    .filter { b -> b.origin.isTranslationOf(text) }

fun State.getTextsPublishedBy(publisher: BusinessId) = getTextStorage()
    .getAll()
    .filter { it.publisher == publisher }

