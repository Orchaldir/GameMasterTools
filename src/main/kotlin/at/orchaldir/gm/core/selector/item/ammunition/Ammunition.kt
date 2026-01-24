package at.orchaldir.gm.core.selector.item.ammunition

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ammunition.AmmunitionId
import at.orchaldir.gm.core.model.rpg.combat.AmmunitionTypeId
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierId

fun State.canDeleteAmmunition(type: AmmunitionId) = DeleteResult(type)

fun State.getAmmunition(type: AmmunitionTypeId) = getAmmunitionStorage()
    .getAll()
    .filter { it.type == type }

fun State.getAmmunition(modifier: EquipmentModifierId) = getAmmunitionStorage()
    .getAll()
    .filter { it.modifiers.contains(modifier) }
