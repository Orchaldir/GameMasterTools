package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.StatisticId
import at.orchaldir.gm.core.selector.economy.getJobs

fun State.canDeleteStatistic(statistic: StatisticId) = DeleteResult(statistic)
    .addElements(getJobs(statistic))
