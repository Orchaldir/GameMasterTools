package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.model.rpg.combat.ShieldTypeId
import at.orchaldir.gm.core.selector.item.equipment.getShields

fun State.canDeleteShieldType(type: ShieldTypeId) = DeleteResult(type)
    .addElements(getShields(type))

fun State.getShieldType(equipment: Equipment) = getShieldTypeStorage()
    .getOptional(equipment.data.getShieldStats()?.type)

fun State.getShieldTypes(type: DamageTypeId) = getShieldTypeStorage()
    .getAll()
    .filter { it.contains(type) }
