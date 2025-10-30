package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponId
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId

fun State.canDeleteMeleeWeapon(type: MeleeWeaponId) = DeleteResult(type)

fun State.getMeleeWeapons(statistic: StatisticId) = getMeleeWeaponStorage()
    .getAll()
    .filter { it.contains(statistic) }
