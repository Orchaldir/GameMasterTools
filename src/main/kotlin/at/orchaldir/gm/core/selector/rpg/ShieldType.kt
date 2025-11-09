package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.ShieldTypeId
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId

fun State.canDeleteShieldType(type: ShieldTypeId) = DeleteResult(type)

fun State.getShieldType(equipment: Equipment) = getShieldTypeStorage()
    .getOptional(null)

fun State.getShieldTypes(type: DamageTypeId) = getShieldTypeStorage()
    .getAll()
    .filter { it.contains(type) }
