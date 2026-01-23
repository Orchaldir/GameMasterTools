package at.orchaldir.gm.core.selector.item.ammunition

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ammunition.AmmunitionId
import at.orchaldir.gm.core.model.rpg.combat.AmmunitionTypeId

fun State.canDeleteAmmunition(type: AmmunitionId) = DeleteResult(type)

fun State.getAmmunition(type: AmmunitionTypeId) = getAmmunitionStorage()
    .getAll()
    .filter { it.type == type }
