package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.CombinedLanguage
import at.orchaldir.gm.core.model.language.EvolvedLanguage
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.selector.item.countText
import at.orchaldir.gm.utils.Id

fun State.canDelete(language: LanguageId) = countText(language) == 0 &&
        countCharacters(language) == 0 &&
        getChildren(language).isEmpty() &&
        countCultures(language) == 0

fun State.getChildren(language: LanguageId) = getLanguageStorage().getAll().filter { l ->
    when (l.origin) {
        is CombinedLanguage -> l.origin.parents.contains(language)
        is EvolvedLanguage -> l.origin.parent == language
        else -> false
    }
}

fun State.getPossibleParents(language: LanguageId) = getLanguageStorage().getAll().filter { l -> l.id != language }

fun <ID : Id<ID>> State.getLanguagesInventedBy(id: ID) = getLanguageStorage().getAll().filter { l ->
    when (l.origin) {
        is InventedLanguage -> l.origin.inventor.isId(id)
        else -> false
    }
}