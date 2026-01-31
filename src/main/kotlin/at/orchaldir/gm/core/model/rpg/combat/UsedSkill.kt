package at.orchaldir.gm.core.model.rpg.combat

import  at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class UsedSkillType {
    Simple,
    Undefined,
}

@Serializable
sealed class UsedSkill {

    fun getType() = when (this) {
        is SimpleUsedSkill -> UsedSkillType.Simple
        is UndefinedUsedSkill -> UsedSkillType.Undefined
    }
}

@Serializable
@SerialName("Simple")
data class SimpleUsedSkill(
    val skill: StatisticId,
    val modifier: Int = 0,
) : UsedSkill()

@Serializable
@SerialName("Undefined")
data object UndefinedUsedSkill : UsedSkill()
