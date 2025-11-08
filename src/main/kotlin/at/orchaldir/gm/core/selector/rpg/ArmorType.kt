package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.ArmorTypeId
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.selector.item.getArmors

fun State.canDeleteArmorType(type: ArmorTypeId) = DeleteResult(type)
    .addElements(getArmors(type))

fun State.getArmorType(equipment: Equipment) = getArmorTypeStorage()
    .getOptional(equipment.data.getArmorStats()?.type)

fun State.getArmorTypes(type: DamageTypeId) = getArmorTypeStorage()
    .getAll()
    .filter { it.contains(type) }
