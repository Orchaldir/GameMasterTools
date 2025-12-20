package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponTypeId
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.selector.item.equipment.getMeleeWeapons

fun State.canDeleteMeleeWeaponType(type: MeleeWeaponTypeId) = DeleteResult(type)
    .addElements(getMeleeWeapons(type))

fun State.getMeleeWeaponType(equipment: Equipment) = getMeleeWeaponTypeStorage()
    .getOptional(equipment.data.getMeleeWeaponStats()?.type)

fun State.getMeleeWeaponTypes(type: DamageTypeId) = getMeleeWeaponTypeStorage()
    .getAll()
    .filter { it.contains(type) }

fun State.getMeleeWeaponTypes(statistic: StatisticId) = getMeleeWeaponTypeStorage()
    .getAll()
    .filter { it.contains(statistic) }
