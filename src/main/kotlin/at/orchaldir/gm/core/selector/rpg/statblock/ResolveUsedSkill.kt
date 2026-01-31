package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.rpg.combat.ModifySkill
import at.orchaldir.gm.core.model.rpg.combat.SimpleUsedSkill
import at.orchaldir.gm.core.model.rpg.combat.UndefinedUsedSkill
import at.orchaldir.gm.core.model.rpg.combat.UsedSkill

// resolve skill with modifier effects

fun resolveUsedSkill(
    modifier: ModifySkill,
    skill: UsedSkill,
) = when (skill) {
    is SimpleUsedSkill -> skill.copy(modifier = skill.modifier + modifier.amount)
    UndefinedUsedSkill -> skill
}
