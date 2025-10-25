package at.orchaldir.gm.core.selector.rpg

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.rpg.statistic.StatisticDataType
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.economy.getJobs

fun State.canDeleteStatistic(statistic: StatisticId) = DeleteResult(statistic)
    .addElements(getCharacters(statistic))
    .addElements(getCharacterTemplates(statistic))
    .addElements(getJobs(statistic))

fun State.getAttributes() = getStatistics(StatisticDataType.Attribute)
fun State.getDerivedAttributes() = getStatistics(StatisticDataType.DerivedAttribute)
fun State.getBaseDamageValues() = getStatistics(StatisticDataType.Damage)
fun State.getSkills() = getStatistics(StatisticDataType.Skill)

private fun State.getStatistics(type: StatisticDataType): List<Statistic> = getStatisticStorage()
    .getAll()
    .filter { it.data.getType() == type }

fun State.getStatisticsBasedOn(statistic: StatisticId) = getStatisticStorage()
    .getAll()
    .filter { it.data.isBasedOn(statistic) }

