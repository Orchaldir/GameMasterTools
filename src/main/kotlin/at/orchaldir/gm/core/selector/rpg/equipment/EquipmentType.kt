package at.orchaldir.gm.core.selector.rpg.equipment

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentTypeId
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.model.rpg.equipment.AmmunitionTypeId
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.selector.item.equipment.getEquipment

fun State.canDeleteEquipmentType(type: EquipmentTypeId) = DeleteResult(type)
    .addElements(getEquipment(type))

fun State.getEquipmentType(equipment: Equipment) = getEquipmentTypeStorage()
    .getOptional(equipment.stats.type)

fun State.getEquipmentTypes(type: AmmunitionTypeId) = getEquipmentTypeStorage()
    .getAll()
    .filter { it.contains(type) }

fun State.getEquipmentTypes(type: DamageTypeId) = getEquipmentTypeStorage()
    .getAll()
    .filter { it.contains(type) }

fun State.getEquipmentTypes(statistic: StatisticId) = getEquipmentTypeStorage()
    .getAll()
    .filter { it.contains(statistic) }

fun State.getEquipmentTypesDealing(type: DamageTypeId) = getEquipmentTypeStorage()
    .getAll()
    .filter { it.meleeAttacks.any { it.contains(type) } }

fun State.getEquipmentTypesProtectingFrom(type: DamageTypeId) = getEquipmentTypeStorage()
    .getAll()
    .filter { it.protection.contains(type) }
