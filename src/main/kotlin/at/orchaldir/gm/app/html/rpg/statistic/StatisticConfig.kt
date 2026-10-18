package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.STATISTIC
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.core.model.rpg.statistic.StatisticConfig
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h3

// show

fun HtmlBlockTag.showStatisticConfig(
    config: StatisticConfig,
) {
    h3 { +"Statists" }

    showStatisticLimits(config.raceLimits, "Race Limits")
}

// edit

fun HtmlBlockTag.editStatisticConfig(
    config: StatisticConfig,
) {
    h3 { +"Statists" }

    editStatisticLimits("Race Limits", config.raceLimits, combine(STATISTIC,RACE))
}

// parse

fun parseStatisticConfig(
    parameters: Parameters,
) = StatisticConfig(
    parseStatisticLimits(parameters, combine(STATISTIC,RACE)),
)
