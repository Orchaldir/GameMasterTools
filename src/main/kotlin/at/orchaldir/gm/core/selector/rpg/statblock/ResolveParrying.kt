package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.rpg.combat.*

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
