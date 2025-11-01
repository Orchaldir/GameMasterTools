package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.SHORT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.selector.economy.getJobs
import at.orchaldir.gm.core.selector.rpg.getMeleeWeapons
import at.orchaldir.gm.core.selector.rpg.getStatblocksWith
import at.orchaldir.gm.core.selector.rpg.getStatisticsBasedOn
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

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
    val meleeWeapons = state.getMeleeWeapons(statistic.id)
    val statblocks = state.getStatblocksWith(statistic.id)
    val statistics = state.getStatisticsBasedOn(statistic.id)

    if (jobs.isEmpty() && meleeWeapons.isEmpty() && statblocks.isEmpty() && statistics.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, jobs)
    fieldElements(call, state, meleeWeapons)
    fieldElements(call, state, statistics)

    table {
        tr {
            th { +"Statblocks" }
            th { +"Value" }
        }
        statblocks
            .sortedByDescending { it.second }
            .forEach { (statblockId, value) ->
                tr {
                    tdLink(call, state, statblockId)
                    tdString(statistic.data.display(value))
                }
            }
    }
}

// edit

fun HtmlBlockTag.editStatistic(
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
    state: State,
    parameters: Parameters,
    id: StatisticId,
) = Statistic(
    id,
    parseName(parameters),
    parseOptionalName(parameters, SHORT),
    parseStatisticData(parameters),
    parseDataSources(parameters),
)
