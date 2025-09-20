package at.orchaldir.gm.core.selector.culture

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.item.getTexts
import at.orchaldir.gm.core.selector.item.periodical.getPeriodicals
import at.orchaldir.gm.core.selector.world.getPlanes

fun State.canDeleteLanguage(language: LanguageId) = DeleteResult(language)
    .addElements(getCharacters(language))
    .addElements(getCharacterTemplates(language))
    .addElements(getChildren(language))
    .addElements(getCultures(language))
    .addElements(getPeriodicals(language))
    .addElements(getPlanes(language))
    .addElements(getTexts(language))

fun State.countChildren(language: LanguageId) = getLanguageStorage()
    .getAll()
    .count { l -> l.origin.isChildOf(language.value) }

fun State.getChildren(language: LanguageId) = getLanguageStorage()
    .getAll()
    .filter { l -> l.origin.isChildOf(language.value) }

fun State.getKnownLanguages(character: Character) = getDefaultLanguages(character.culture) + character.languages
fun State.getKnownLanguages(template: CharacterTemplate) = getDefaultLanguages(template.culture) + template.languages

fun State.getDefaultLanguages(culture: CultureId?) = getCultureStorage()
    .getOptional(culture)
    ?.languages
    ?.getValuesFor(Rarity.Everyone)
    ?.associateWith { ComprehensionLevel.Native } ?: emptyMap()

fun State.getPossibleParents(language: LanguageId) = getLanguageStorage()
    .getAllExcept(language)
