package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.PersonalityTraitGroup
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.selector.religion.getGodsWith

fun State.canDeletePersonalityTrait(id: PersonalityTraitId) = DeleteResult(id)
    .addElements(getCharacters(id))
    .addElements(getGodsWith(id))

fun countEachPersonalityForCharacters(characters: Collection<Character>) = characters
    .flatMap { it.personality }
    .groupingBy { it }
    .eachCount()

fun countEachPersonalityForGods(gods: Collection<God>) = gods
    .flatMap { it.personality }
    .groupingBy { it }
    .eachCount()

fun State.getPersonalityTraits(group: PersonalityTraitGroup) = getPersonalityTraitStorage()
    .getAll()
    .filter { group == it.group }

fun State.getPersonalityTraitGroups() = getPersonalityTraitStorage()
    .getAll()
    .mapNotNull { it.group }
    .toSet()
