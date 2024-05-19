package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.PersonalityTraitGroup

fun State.getPersonalityTraits(group: PersonalityTraitGroup) = personalityTraits.getAll()
    .filter { group == it.group }
