package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.language.*

fun State.canDelete(language: LanguageId) = getCharacters(language).isEmpty() && getChildren(language).isEmpty()

fun State.getChildren(language: LanguageId) = languages.getAll().filter { l ->
    when (l.origin) {
        is CombinedLanguage -> l.origin.parents.contains(language)
        is EvolvedLanguage -> l.origin.parent == language
        else -> false
    }
}

fun State.getPossibleLanguages(id: CharacterId): List<Language> {
    val known = characters.get(id)?.languages ?: return listOf()

    return languages.getAll().filter { l -> !known.containsKey(l.id) }
}

fun State.getPossibleParents(language: LanguageId) = languages.getAll().filter { l -> l.id != language }

fun State.getInventedLanguages(inventor: CharacterId) = languages.getAll().filter { l ->
    when (l.origin) {
        is InventedLanguage -> l.origin.inventor == inventor
        else -> false
    }
}