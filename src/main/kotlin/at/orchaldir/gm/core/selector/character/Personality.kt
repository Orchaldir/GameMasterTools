package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.PersonalityTraitGroup
import at.orchaldir.gm.core.model.religion.God

fun countEachPersonalityForCharacters(characters: Collection<Character>) = characters
    .flatMap { it.personality }
    .groupingBy { it }
    .eachCount()

fun countEachPersonalityForGods(gods: Collection<God>) = gods
    .flatMap { it.personality }
    .groupingBy { it }
    .eachCount()

fun State.getPersonalityTraits(group: PersonalityTraitGroup) = getPersonalityTraitStorage().getAll()
    .filter { group == it.group }

fun State.getPersonalityTraitGroups() = getPersonalityTraitStorage().getAll()
    .mapNotNull { it.group }
    .toSet()
