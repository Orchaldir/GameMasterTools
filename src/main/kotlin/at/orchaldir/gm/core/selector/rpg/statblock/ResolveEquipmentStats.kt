package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statblock.*

// resolve melee attack with statblock

fun resolveMeleeAttackMap(
    state: State,
    lookup: StatblockLookup,
    attackMap: Map<Equipment, List<MeleeAttack>>,
) = when (lookup) {
    UndefinedStatblockLookup -> attackMap
    is UniqueStatblock -> resolveMeleeAttackMap(state, lookup.statblock, attackMap)
    is UseStatblockOfTemplate -> {
        val statblock = state.getStatblock(lookup.template)

        resolveMeleeAttackMap(state, statblock, attackMap)
    }

    is ModifyStatblockOfTemplate -> {
        val statblock = state.getStatblock(lookup.template)
        val resolvedStatblock = lookup.update.resolve(statblock)

        resolveMeleeAttackMap(state, resolvedStatblock, attackMap)
    }
}

fun resolveMeleeAttackMap(
    state: State,
    statblock: Statblock,
    attackMap: Map<Equipment, List<MeleeAttack>>,
) = attackMap.mapValues { (_, attacks) ->
    attacks.map { attack ->
        resolveMeleeAttack(state, statblock, attack)
    }
}

fun resolveMeleeAttack(
    state: State,
    statblock: Statblock,
    attack: MeleeAttack,
) = attack.copy(effect = resolveAttackEffect(state, statblock, attack.effect))

fun resolveAttackEffect(
    state: State,
    statblock: Statblock,
    effect: AttackEffect,
) = when (effect) {
    is Damage -> effect.copy(amount = resolveDamageAmount(state, statblock, effect.amount))
    UndefinedAttackEffect -> effect
}

fun resolveDamageAmount(
    state: State,
    statblock: Statblock,
    amount: DamageAmount,
) = when (amount) {
    is SimpleRandomDamage -> amount
    is StatisticBasedDamage -> {
        val statistic = state.getStatisticStorage().getOrThrow(amount.base)
        val value =
            statblock.resolve(state, statistic) ?: error("Failed to resolve ${amount.base.print()} with statblock!")

        SimpleRandomDamage(statistic.data.resolveDamage(value) + amount.modifier)
    }
}

// resolve melee attack with modifier effects

fun resolveMeleeAttacks(effects: List<EquipmentModifierEffect>, attacks: List<MeleeAttack>) = attacks.map { attack ->
    resolveMeleeAttack(effects, attack)
}

fun resolveMeleeAttack(
    effects: List<EquipmentModifierEffect>,
    attack: MeleeAttack,
): MeleeAttack {
    var resolved = attack

    effects.forEach { effect ->
        resolved = resolveMeleeAttack(effect, resolved)
    }

    return resolved
}

fun resolveMeleeAttack(
    effect: EquipmentModifierEffect,
    attack: MeleeAttack,
) = when (effect) {
    is ModifyDamageResistance, is ModifyDefenseBonus -> attack
    is ModifyDamage -> attack.copy(effect = resolveAttackEffect(effect, attack.effect))
}

fun resolveAttackEffect(
    modifyDamage: ModifyDamage,
    attackEffect: AttackEffect,
) = when (attackEffect) {
    is Damage -> attackEffect.copy(amount = attackEffect.amount.apply(modifyDamage))
    UndefinedAttackEffect -> attackEffect
}

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