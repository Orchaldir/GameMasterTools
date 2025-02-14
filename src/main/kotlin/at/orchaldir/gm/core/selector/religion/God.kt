package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.model.religion.GodId

fun State.canDeleteGod(god: GodId) = true

fun State.getGodsWith(id: DomainId) = getGodStorage()
    .getAll()
    .filter { it.domains.contains(id) }


