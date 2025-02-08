package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.item.text.OriginalText
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.item.text.TranslatedText
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Id

fun State.canDeleteText(text: TextId) = getTranslationsOf(text).isEmpty()

fun State.countTexts(font: FontId) = getTextStorage()
    .getAll()
    .count { c -> c.format.contains(font) }

fun State.countTexts(language: LanguageId) = getTextStorage()
    .getAll()
    .count { c -> c.language == language }

fun State.countTexts(material: MaterialId) = getTextStorage()
    .getAll()
    .count { it.format.isMadeOf(material) }

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
    is OriginalText -> when (origin.author) {
        is CreatedByBusiness -> getElementName(origin.author.business)
        is CreatedByCharacter -> getElementName(origin.author.character)
        is CreatedByOrganization -> getElementName(origin.author.organization)
        is CreatedByTown -> getElementName(origin.author.town)
        UndefinedCreator -> null
    }

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
    .filter { b -> b.format.contains(font) }

fun State.getTexts(language: LanguageId) = getTextStorage()
    .getAll()
    .filter { b -> b.language == language }

fun State.getTextsContaining(spell: SpellId) = getTextStorage()
    .getAll()
    .filter { b -> b.content.contains(spell) }

fun State.getTextsMadeOf(material: MaterialId) = getTextStorage()
    .getAll()
    .filter { it.format.isMadeOf(material) }

fun State.getTranslationsOf(text: TextId) = getTextStorage()
    .getAll()
    .filter { b -> b.origin.isTranslationOf(text) }

fun <ID : Id<ID>> State.getTextsTranslatedBy(id: ID) = getTextStorage()
    .getAll()
    .filter { it.origin.wasTranslatedBy(id) }

fun <ID : Id<ID>> State.getTextsWrittenBy(id: ID) = getTextStorage()
    .getAll()
    .filter { it.origin.wasWrittenBy(id) }

