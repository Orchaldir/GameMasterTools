package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponModifierId

fun State.canDeleteMeleeWeaponModifier(type: MeleeWeaponModifierId) = DeleteResult(type)
