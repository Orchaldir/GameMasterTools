package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.world.street.StreetTypeId

fun State.canDelete(type: StreetTypeId) = getTowns(type).isEmpty()

fun State.getStreetTypesMadeOf(material: MaterialId) = getStreetTypeStorage().getAll()
    .filter { it.materialCost.contains(material) }
