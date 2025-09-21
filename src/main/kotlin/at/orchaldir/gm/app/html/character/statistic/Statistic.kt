package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.SHORT
import at.orchaldir.gm.app.STATISTIC
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.Statistic
import at.orchaldir.gm.core.model.character.statistic.StatisticId
import at.orchaldir.gm.core.selector.character.getStatblocksWith
import at.orchaldir.gm.core.selector.character.getStatisticsBasedOn
import at.orchaldir.gm.core.selector.economy.getJobs
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

// show

fun HtmlBlockTag.showStatistic(
    call: ApplicationCall,
    state: State,
    statistic: Statistic,
) {
    optionalFieldName("Short", statistic.short)
    showStatisticData(call, state, statistic.data)
    showDataSources(call, state, statistic.sources)
    showUsage(call, state, statistic)
}

private fun HtmlBlockTag.showUsage(
    call: ApplicationCall,
    state: State,
    statistic: Statistic,
) {
    val jobs = state.getJobs(statistic.id)
    val statblocks = state.getStatblocksWith(statistic.id)
    val statistics = state.getStatisticsBasedOn(statistic.id)

    if (jobs.isEmpty() && statblocks.isEmpty() && statistics.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, jobs)
    fieldElements(call, state, statistics)



    table {
        tr {
            th { +"Statblocks" }
            th { +"Value" }
        }
        statblocks.forEach { (id, statblock) ->
            val value = statblock.resolve(state, statistic)

            tr {
                tdLink(call, state, id)
                tdSkipZero(value)
            }
        }
    }
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
fun parseStatisticId(value: String) = StatisticId(value.toInt())

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
