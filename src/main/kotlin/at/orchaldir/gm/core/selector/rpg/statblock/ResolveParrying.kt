package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.rpg.combat.ModifyParrying
import at.orchaldir.gm.core.model.rpg.combat.NoParrying
import at.orchaldir.gm.core.model.rpg.combat.NormalParrying
import at.orchaldir.gm.core.model.rpg.combat.Parrying
import at.orchaldir.gm.core.model.rpg.combat.UnbalancedParrying
import at.orchaldir.gm.core.model.rpg.combat.UndefinedParrying

// resolve parrying with modifier effects

fun resolveParrying(
    modifier: ModifyParrying,
    parrying: Parrying,
) = when (parrying) {
    is NormalParrying -> NormalParrying(
        parrying.modifier + modifier.amount
    )
    NoParrying -> parrying
    is UnbalancedParrying -> NormalParrying(
        parrying.modifier + modifier.amount
    )
    UndefinedParrying -> parrying
}
