package at.orchaldir.gm.core.model.rpg.combat

import  at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class UsedSkillType {
    Resolved,
    Modified,
    Undefined,
}

@Serializable
sealed class UsedSkill {

    fun getType() = when (this) {
        is ResolvedUsedSkill -> UsedSkillType.Resolved
        is ModifiedUsedSkill -> UsedSkillType.Modified
        is UndefinedUsedSkill -> UsedSkillType.Undefined
    }

    fun contains(statistic: StatisticId) = when (this) {
        is ResolvedUsedSkill -> skill == statistic
        is ModifiedUsedSkill -> skill == statistic
        is UndefinedUsedSkill -> false
    }
}

@Serializable
@SerialName("Resolved")
data class ResolvedUsedSkill(
    val skill: StatisticId,
    val value: Int = 0,
) : UsedSkill()

@Serializable
@SerialName("Modified")
data class ModifiedUsedSkill(
    val skill: StatisticId,
    val modifier: Int = 0,
) : UsedSkill()

@Serializable
@SerialName("Undefined")
data object UndefinedUsedSkill : UsedSkill()
