package at.orchaldir.gm.core.selector.rpg.equipment

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ammunition.Ammunition
import at.orchaldir.gm.core.model.rpg.equipment.AmmunitionTypeId
import at.orchaldir.gm.core.selector.item.ammunition.getAmmunition

fun State.canDeleteAmmunitionType(type: AmmunitionTypeId) = DeleteResult(type)
    .addElements(getAmmunition(type))
    .addElements(getEquipmentTypes(type))

fun State.getAmmunitionType(ammunition: Ammunition) = getAmmunitionTypeStorage()
    .getOptional(ammunition.type)
