package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statblock.*

// resolve protection map with statblock

fun resolveProtectionMap(
    state: State,
    statblock: StatblockLookup,
    protectionMap: Map<Equipment, Protection>,
) = protectionMap

// resolve protection with modifier effects

fun resolveProtection(
    effects: List<EquipmentModifierEffect>,
    protection: Protection,
): Protection {
    var resolved = protection

    effects.forEach { effect ->
        resolved = resolveProtection(effect, resolved)
    }

    return resolved
}

fun resolveProtection(
    effect: EquipmentModifierEffect,
    protection: Protection,
) = when (effect) {
    is ModifyDamageResistance -> when (protection) {
        is DamageResistance -> DamageResistance(protection.amount + effect.amount)
        is DamageResistances -> protection.copy(amount = protection.amount + effect.amount)
        else -> protection
    }

    is ModifyDefenseBonus -> if (protection is DefenseBonus) {
        DefenseBonus(protection.bonus + effect.amount)
    } else {
        protection
    }

    is ModifyDamage -> protection
}