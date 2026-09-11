package at.orchaldir.gm.core.selector.rpg.equipment

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentTypeId
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.selector.item.equipment.getArmors

fun State.canDeleteEquipmentType(type: EquipmentTypeId) = DeleteResult(type)
    .addElements(getArmors(type))

fun State.getArmorType(equipment: Equipment) = getEquipmentTypeStorage()
    .getOptional(equipment.data.getArmorStats()?.type)

fun State.getEquipmentTypes(type: DamageTypeId) = getEquipmentTypeStorage()
    .getAll()
    .filter { it.contains(type) }
