package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.selectFromRange
import at.orchaldir.gm.app.html.rpg.statistic.parseStatisticId
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.selector.util.sortStatistics
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
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
        is SimpleUsedSkill -> {
            link(call, state, skill.skill)

            if (skill.modifier > 0) {
                +" + ${skill.modifier}"
            }
            else if (skill.modifier < 0) {
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
                UsedSkillType.Simple -> state.getStatisticStorage().isEmpty()
                UsedSkillType.Undefined -> false
            }
        }

        when (skill) {
            is SimpleUsedSkill -> {
                selectElement(
                    state,
                    "Skill",
                    combine(skillParam, STATISTIC),
                    state.sortStatistics(),
                    skill.skill,
                )
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

// parse

fun parseUsedSkill(
    parameters: Parameters,
    param: String,
): UsedSkill {
    val skillParam = combine(param, STATISTIC)

    return when (parse(parameters, combine(skillParam, TYPE), UsedSkillType.Undefined)) {
        UsedSkillType.Simple -> SimpleUsedSkill(
            parseStatisticId(parameters, combine(skillParam, STATISTIC)),
            parseInt(parameters, combine(skillParam, NUMBER)),
        )
        UsedSkillType.Undefined -> UndefinedUsedSkill
    }
}
