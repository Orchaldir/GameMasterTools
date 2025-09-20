package at.orchaldir.gm.app.html.character

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
    template: Statistic,
) {
    optionalFieldName("Short", template.short)
    showDataSources(call, state, template.sources)
}

// edit

fun FORM.editStatistic(
    state: State,
    template: Statistic,
) {
    selectName(template.name)
    selectOptionalName("Short", template.short, SHORT)
    editDataSources(state, template.sources)
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
    parseDataSources(parameters),
)
