package at.orchaldir.gm.core.selector.rpg.combat

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.selector.rpg.equipment.getEquipmentTypes

fun State.canDeleteDamageType(type: DamageTypeId) = DeleteResult(type)
    .addElements(getEquipmentTypes(type))

