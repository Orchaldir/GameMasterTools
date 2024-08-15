package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.PersonalityTraitGroup

fun State.getPersonalityTraits(group: PersonalityTraitGroup) = getPersonalityTraitStorage().getAll()
    .filter { group == it.group }

fun State.getPersonalityTraitGroups() = getPersonalityTraitStorage().getAll()
    .mapNotNull { it.group }
    .toSet()
