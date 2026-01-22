package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.AmmunitionId

fun State.canDeleteAmmunition(type: AmmunitionId) = DeleteResult(type)
