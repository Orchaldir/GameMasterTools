package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.settlement.SettlementMapId

fun State.canDeleteStreetTemplate(template: StreetTemplateId) = DeleteResult(template)
    .addElements(getSettlementMaps(template))

fun State.countStreetTemplatesMadeOf(material: MaterialId) = getStreetTemplateStorage()
    .getAll()
    .count { it.materialCost.contains(material) }

fun State.getStreetTemplatesMadeOf(material: MaterialId) = getStreetTemplateStorage()
    .getAll()
    .filter { it.materialCost.contains(material) }

fun State.countEachStreetTemplate(settlement: SettlementMapId) = getSettlementMapStorage()
    .getOrThrow(settlement)
    .map.tiles
    .mapNotNull { it.construction.getOptionalStreetTemplate() }
    .groupingBy { it }
    .eachCount()

fun State.countStreetTemplates(material: MaterialId) = getStreetTemplateStorage()
    .getAll()
    .count { it.materialCost.contains(material) }
