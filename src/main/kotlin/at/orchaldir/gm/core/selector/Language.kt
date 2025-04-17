package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.language.PlanarLanguage
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.selector.culture.countCultures
import at.orchaldir.gm.core.selector.item.countPeriodicals
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.utils.Id

fun State.canDeleteLanguage(language: LanguageId) = countCharacters(language) == 0 &&
        countChildren(language) == 0 &&
        countCultures(language) == 0 &&
        countPeriodicals(language) == 0 &&
        countTexts(language) == 0

fun State.countChildren(language: LanguageId) = getLanguageStorage()
    .getAll()
    .count { l -> l.origin.isChildOf(language) }

fun State.getChildren(language: LanguageId) = getLanguageStorage()
    .getAll()
    .filter { l -> l.origin.isChildOf(language) }

fun State.getKnownLanguages(character: Character) = getDefaultLanguages(character) + character.languages

fun State.getDefaultLanguages(character: Character) = getCultureStorage()
    .getOrThrow(character.culture)
    .languages
    .getValuesFor(Rarity.Everyone)
    .associateWith { ComprehensionLevel.Native }

fun State.getPossibleParents(language: LanguageId) = getLanguageStorage()
    .getAllExcept(language)

fun <ID : Id<ID>> State.getLanguagesInventedBy(id: ID) = getLanguageStorage().getAll().filter { l ->
    when (l.origin) {
        is InventedLanguage -> l.origin.inventor.isId(id)
        else -> false
    }
}

fun State.getPlanarLanguages(plane: PlaneId) = getLanguageStorage()
    .getAll()
    .filter { it.origin is PlanarLanguage && it.origin.plane == plane }
