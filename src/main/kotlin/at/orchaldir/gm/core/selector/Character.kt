package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.language.LanguageId

fun State.getCharacters(culture: CultureId) = characters.getAll().filter { c -> c.culture == culture }

fun State.getCharacters(language: LanguageId) = characters.getAll().filter { c -> c.languages.containsKey(language) }

fun State.getCharacters(trait: PersonalityTraitId) = characters.getAll().filter { c -> c.personality.contains(trait) }

fun State.getCharacters(race: RaceId) = characters.getAll().filter { c -> c.race == race }

// relatives

fun State.getParents(id: CharacterId): List<Character> {
    val character = characters.get(id) ?: return listOf()

    return when (character.origin) {
        is Born -> character.origin.parents.map { characters.getOrThrow(it) }
        else -> listOf()
    }
}

fun State.getChildren(id: CharacterId) = characters.getAll().filter {
    when (it.origin) {
        is Born -> it.origin.parents.contains(id)
        else -> false
    }
}

fun State.getSiblings(id: CharacterId): Set<Character> {
    val siblings = mutableSetOf<Character>()

    getParents(id).forEach { siblings.addAll(getChildren(it.id)) }
    siblings.removeIf { it.id == id }

    return siblings
}