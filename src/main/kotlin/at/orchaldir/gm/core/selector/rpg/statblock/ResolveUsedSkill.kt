package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.ModifySkill
import at.orchaldir.gm.core.model.rpg.combat.ModifiedUsedSkill
import at.orchaldir.gm.core.model.rpg.combat.ResolvedUsedSkill
import at.orchaldir.gm.core.model.rpg.combat.UndefinedUsedSkill
import at.orchaldir.gm.core.model.rpg.combat.UsedSkill
import at.orchaldir.gm.core.model.rpg.statblock.Statblock

// resolve akill with statblock

fun resolveUsedSkill(
    state: State,
    statblock: Statblock,
    skill: UsedSkill,
) = when (skill) {
    is ModifiedUsedSkill -> {
        val base = statblock.resolve(state, skill.skill)
            ?: error("Failed to resolve ${skill.skill.print()} with UsedSkill!")
        ResolvedUsedSkill(skill.skill, base + skill.modifier)
    }
    else -> skill
}

// resolve skill with modifier effects

fun resolveUsedSkill(
    modifier: ModifySkill,
    skill: UsedSkill,
) = when (skill) {
    is ResolvedUsedSkill -> skill.copy(value = skill.value + modifier.amount)
    is ModifiedUsedSkill -> skill.copy(modifier = skill.modifier + modifier.amount)
    UndefinedUsedSkill -> skill
}
