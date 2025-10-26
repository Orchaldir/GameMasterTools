package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponId

fun State.canDeleteMeleeWeapon(type: MeleeWeaponId) = DeleteResult(type)

