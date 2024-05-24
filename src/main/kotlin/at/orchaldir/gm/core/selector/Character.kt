package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.language.LanguageId

fun State.canDelete(character: CharacterId) = getChildren(character).isEmpty() &&
        getParents(character).isEmpty() &&
        getInventedLanguages(character).isEmpty()

// get characters

fun State.getCharacters(culture: CultureId) = characters.getAll().filter { c -> c.culture == culture }

fun State.getCharacters(language: LanguageId) = characters.getAll().filter { c -> c.languages.containsKey(language) }

fun State.getCharacters(trait: PersonalityTraitId) = characters.getAll().filter { c -> c.personality.contains(trait) }

fun State.getCharacters(race: RaceId) = characters.getAll().filter { c -> c.race == race }

fun State.getOthers(id: CharacterId) = characters.getAll().filter { c -> c.id != id }

// get relatives

fun State.getParents(id: CharacterId): List<Character> {
    val character = characters.get(id) ?: return listOf()

    return when (character.origin) {
        is Born -> listOf(character.origin.father, character.origin.mother).map { characters.getOrThrow(it) }
        else -> listOf()
    }
}

fun State.getPossibleFathers(id: CharacterId) = characters.getAll()
    .filter { it.gender == Gender.Male }
    .filter { it.id != id }

fun State.getPossibleMothers(id: CharacterId) = characters.getAll()
    .filter { it.gender == Gender.Female }
    .filter { it.id != id }

fun State.getChildren(id: CharacterId) = characters.getAll().filter {
    when (it.origin) {
        is Born -> it.origin.isParent(id)
        else -> false
    }
}

fun State.getSiblings(id: CharacterId): Set<Character> {
    val siblings = mutableSetOf<Character>()

    getParents(id).forEach { siblings.addAll(getChildren(it.id)) }
    siblings.removeIf { it.id == id }

    return siblings
}

// relationships

fun State.getOthersWithoutRelationship(character: Character) = characters.getAll()
    .filter { c -> c.id != character.id }
    .filter { c -> !character.relationships.containsKey(c.id) }