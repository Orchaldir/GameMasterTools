package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitGroup
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.religion.getGodsWith

fun State.canDeleteCharacterTrait(id: CharacterTraitId) = DeleteResult(id)
    .addElements(getCharacters(id))
    .addElements(getCharacterTemplates(id))
    .addElements(getGodsWith(id))

fun countEachCharacterTraitForGods(gods: Collection<God>) = gods
    .flatMap { it.personality }
    .groupingBy { it }
    .eachCount()

fun State.getCharacterTraits(group: CharacterTraitGroup) = getCharacterTraitStorage()
    .getAll()
    .filter { group == it.group }

fun State.getCharacterTraitGroups() = getCharacterTraitStorage()
    .getAll()
    .mapNotNull { it.group }
    .toSet()
