package at.orchaldir.gm.core.selector.rpg.combat

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.selector.rpg.equipment.getEquipmentTypes
import at.orchaldir.gm.core.selector.rpg.equipment.getMeleeWeaponTypes
import at.orchaldir.gm.core.selector.rpg.equipment.getRangedWeaponTypes

fun State.canDeleteDamageType(type: DamageTypeId) = DeleteResult(type)
    .addElements(getEquipmentTypes(type))
    .addElements(getMeleeWeaponTypes(type))
    .addElements(getRangedWeaponTypes(type))

