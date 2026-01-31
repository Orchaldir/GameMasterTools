package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ZERO

fun validateUsedSkill(
    state: State,
    skill: UsedSkill,
) {
    when (skill) {
        is SimpleUsedSkill -> {
            state.getStatisticStorage().require(skill.skill)
            validateIsInside(skill.modifier, "Used Skill Modifier", state.data.rpg.equipment.skillModifier)
        }
        UndefinedUsedSkill -> doNothing()
    }
}
