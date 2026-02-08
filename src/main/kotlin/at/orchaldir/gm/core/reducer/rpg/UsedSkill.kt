package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.ModifiedUsedSkill
import at.orchaldir.gm.core.model.rpg.combat.ResolvedUsedSkill
import at.orchaldir.gm.core.model.rpg.combat.UndefinedUsedSkill
import at.orchaldir.gm.core.model.rpg.combat.UsedSkill
import at.orchaldir.gm.utils.doNothing

fun validateUsedSkill(
    state: State,
    skill: UsedSkill,
) {
    when (skill) {
        is ResolvedUsedSkill -> {
            state.getStatisticStorage().require(skill.skill)
        }
        is ModifiedUsedSkill -> {
            state.getStatisticStorage().require(skill.skill)
            validateIsInside(skill.modifier, "Used Skill Modifier", state.data.rpg.equipment.skillModifier)
        }

        UndefinedUsedSkill -> doNothing()
    }
}
