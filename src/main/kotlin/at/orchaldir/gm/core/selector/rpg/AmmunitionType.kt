package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.AmmunitionTypeId

fun State.canDeleteAmmunitionType(type: AmmunitionTypeId) = DeleteResult(type)
