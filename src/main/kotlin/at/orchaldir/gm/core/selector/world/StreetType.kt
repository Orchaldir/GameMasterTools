package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.town.TownId

fun State.canDelete(template: StreetTemplateId) = getTowns(template).isEmpty()

fun State.getStreetTemplatesMadeOf(material: MaterialId) = getStreetTemplateStorage()
    .getAll()
    .filter { it.materialCost.contains(material) }

fun State.countStreetTemplates(town: TownId) = getTownStorage()
    .getOrThrow(town)
    .map.tiles
    .mapNotNull { it.construction.getOptionalStreetTemplate() }
    .groupingBy { it }
    .eachCount()

fun State.countStreetTemplates(material: MaterialId) = getStreetTemplateStorage()
    .getAll()
    .count { it.materialCost.contains(material) }
