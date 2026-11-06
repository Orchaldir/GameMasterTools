package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.CHARACTER
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
    h3 { +"Statistics" }

    showStatisticLimits(config.raceLimits, "Race Limits")
    showStatisticLimits(config.characterLimits, "Character Limits")
}

// edit

fun HtmlBlockTag.editStatisticConfig(
    config: StatisticConfig,
) {
    h3 { +"Statistics" }

    editStatisticLimits("Race Limits", config.raceLimits, combine(STATISTIC, RACE))
    editStatisticLimits("Character Limits", config.characterLimits, combine(STATISTIC, CHARACTER))
}

// parse

fun parseStatisticConfig(
    parameters: Parameters,
) = StatisticConfig(
    parseStatisticLimits(parameters, combine(STATISTIC, RACE)),
    parseStatisticLimits(parameters, combine(STATISTIC, CHARACTER)),
)
