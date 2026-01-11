package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.model.rpg.combat.RangedWeaponTypeId
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.selector.item.equipment.getMeleeWeapons
import at.orchaldir.gm.core.selector.item.equipment.getRangedWeapons

fun State.canDeleteRangedWeaponType(type: RangedWeaponTypeId) = DeleteResult(type)
    .addElements(getRangedWeapons(type))

fun State.getRangedWeaponTypes(type: DamageTypeId) = getRangedWeaponTypeStorage()
    .getAll()
    .filter { it.contains(type) }

fun State.getRangedWeaponTypes(statistic: StatisticId) = getRangedWeaponTypeStorage()
    .getAll()
    .filter { it.contains(statistic) }
