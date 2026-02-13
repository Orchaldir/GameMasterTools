package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.STATISTIC
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.selectFromRange
import at.orchaldir.gm.app.html.rpg.statistic.parseStatisticId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.selector.util.sortStatistics
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag
import kotlin.math.absoluteValue

// show

fun HtmlBlockTag.fieldUsedSkill(
    call: ApplicationCall,
    state: State,
    skill: UsedSkill,
) {
    field("Used Skill") {
        displayUsedSkill(call, state, skill, true)
    }
}

fun HtmlBlockTag.displayUsedSkill(
    call: ApplicationCall,
    state: State,
    skill: UsedSkill,
    showUndefined: Boolean = false,
) {
    when (skill) {
        is ResolvedUsedSkill -> {
            link(call, state, skill.skill)
            +" ${skill.value}"
        }

        is ModifiedUsedSkill -> {
            link(call, state, skill.skill)

            if (skill.modifier > 0) {
                +" + ${skill.modifier}"
            } else if (skill.modifier < 0) {
                +" - ${skill.modifier.absoluteValue}"
            }
        }

        UndefinedUsedSkill -> if (showUndefined) {
            +"Undefined"
        }

    }
}

// edit

fun HtmlBlockTag.editUsedSkill(
    state: State,
    skill: UsedSkill,
    param: String,
) {
    val skillParam = combine(param, STATISTIC)

    showDetails("Used Skill", true) {
        selectValue(
            "Type",
            combine(skillParam, TYPE),
            UsedSkillType.entries,
            skill.getType(),
        ) { type ->
            when (type) {
                UsedSkillType.Modified, UsedSkillType.Resolved -> state.getStatisticStorage().isEmpty()
                UsedSkillType.Undefined -> false
            }
        }

        when (skill) {
            is ResolvedUsedSkill -> {
                selectUsedSkill(state, skillParam, skill.skill)
                selectInt(
                    "Value",
                    skill.value,
                    0,
                    20,
                    1,
                    combine(skillParam, NUMBER),
                )
            }

            is ModifiedUsedSkill -> {
                selectUsedSkill(state, skillParam, skill.skill)
                selectFromRange(
                    "Modifier",
                    state.data.rpg.equipment.skillModifier,
                    skill.modifier,
                    combine(skillParam, NUMBER),
                )
            }

            UndefinedUsedSkill -> doNothing()
        }
    }
}

private fun DETAILS.selectUsedSkill(
    state: State,
    skillParam: String,
    skill: StatisticId,
) = selectElement(
    state,
    "Skill",
    combine(skillParam, STATISTIC),
    state.sortStatistics(),
    skill,
)

// parse

fun parseUsedSkill(
    parameters: Parameters,
    param: String,
): UsedSkill {
    val skillParam = combine(param, STATISTIC)

    return when (parse(parameters, combine(skillParam, TYPE), UsedSkillType.Undefined)) {
        UsedSkillType.Resolved -> ModifiedUsedSkill(
            parseSkillId(parameters, skillParam),
            parseSkillNumber(parameters, skillParam),
        )

        UsedSkillType.Modified -> ModifiedUsedSkill(
            parseSkillId(parameters, skillParam),
            parseSkillNumber(parameters, skillParam),
        )

        UsedSkillType.Undefined -> UndefinedUsedSkill
    }
}

private fun parseSkillNumber(parameters: Parameters, skillParam: String): Int =
    parseInt(parameters, combine(skillParam, NUMBER))

private fun parseSkillId(
    parameters: Parameters,
    skillParam: String,
) = parseStatisticId(parameters, combine(skillParam, STATISTIC))
