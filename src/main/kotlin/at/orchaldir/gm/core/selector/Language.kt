package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.language.EvolvedLanguage
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.LanguageId

fun State.canDelete(language: LanguageId) = getCharacters(language).isEmpty() && getChildren(language).isEmpty()

fun State.getChildren(language: LanguageId) = languages.getAll().filter { l ->
    when (l.origin) {
        is EvolvedLanguage -> l.origin.parent == language
        else -> false
    }
}

fun State.getPossibleParents(language: LanguageId) = languages.getAll().filter { l -> l.id != language }

fun State.getInventedLanguages(inventor: CharacterId) = languages.getAll().filter { l ->
    when (l.origin) {
        is InventedLanguage -> l.origin.inventor == inventor
        else -> false
    }
}