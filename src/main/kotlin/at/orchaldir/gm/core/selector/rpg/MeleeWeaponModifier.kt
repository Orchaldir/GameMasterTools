package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponModifierId
import at.orchaldir.gm.core.selector.item.getMeleeWeapons

fun State.canDeleteMeleeWeaponModifier(modifier: MeleeWeaponModifierId) = DeleteResult(modifier)
    .addElements(getMeleeWeapons(modifier))
