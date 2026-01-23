package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.AmmunitionId
import at.orchaldir.gm.core.model.rpg.combat.AmmunitionTypeId

fun State.canDeleteAmmunition(type: AmmunitionId) = DeleteResult(type)

fun State.getAmmunitions(type: AmmunitionTypeId) = getAmmunitionStorage()
    .getAll()
    .filter { it.type == type }
