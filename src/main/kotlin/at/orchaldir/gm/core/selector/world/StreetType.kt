package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId

fun State.canDelete(template: StreetTemplateId) = getTowns(template).isEmpty()

fun State.getStreetTemplatesMadeOf(material: MaterialId) = getStreetTemplateStorage().getAll()
    .filter { it.materialCost.contains(material) }
