package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.utils.Id

fun State.canDeleteText(text: TextId) = getTranslationsOf(text).isEmpty()

fun State.countText(language: LanguageId) = getTextStorage()
    .getAll()
    .count { c -> c.language == language }

fun State.countTexts(material: MaterialId) = getTextStorage()
    .getAll()
    .count { it.format.isMadeOf(material) }

fun countLanguages(texts: Collection<Text>) = texts
    .groupingBy { it.language }
    .eachCount()

fun countTextOriginTypes(texts: Collection<Text>) = texts
    .groupingBy { it.origin.getType() }
    .eachCount()

fun State.getTexts(language: LanguageId) = getTextStorage()
    .getAll()
    .filter { b -> b.language == language }

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

