package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.SHORT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.Statistic
import at.orchaldir.gm.core.model.character.statistic.StatisticId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStatistic(
    call: ApplicationCall,
    state: State,
    statistic: Statistic,
) {
    optionalFieldName("Short", statistic.short)
    showStatisticData(call, state, statistic.data)
    showDataSources(call, state, statistic.sources)
}

// edit

fun FORM.editStatistic(
    state: State,
    statistic: Statistic,
) {
    selectName(statistic.name)
    selectOptionalName("Short", statistic.short, SHORT)
    editStatisticData(state, statistic.id, statistic.data)
    editDataSources(state, statistic.sources)
}

// parse

fun parseStatisticId(parameters: Parameters, param: String) = StatisticId(parseInt(parameters, param))

fun parseStatistic(
    parameters: Parameters,
    id: StatisticId,
) = Statistic(
    id,
    parseName(parameters),
    parseOptionalName(parameters, SHORT),
    parseStatisticData(parameters),
    parseDataSources(parameters),
)
