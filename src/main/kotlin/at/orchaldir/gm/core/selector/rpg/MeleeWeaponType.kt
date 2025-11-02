package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponTypeId
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId

fun State.canDeleteMeleeWeaponType(type: MeleeWeaponTypeId) = DeleteResult(type)

fun State.getMeleeWeaponTypes(type: DamageTypeId) = getMeleeWeaponTypeStorage()
    .getAll()
    .filter { it.contains(type) }

fun State.getMeleeWeaponTypes(statistic: StatisticId) = getMeleeWeaponTypeStorage()
    .getAll()
    .filter { it.contains(statistic) }
