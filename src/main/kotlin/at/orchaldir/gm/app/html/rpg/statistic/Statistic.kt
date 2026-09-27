package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.SHORT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.showGenericUsage
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.routes.ConfigRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.selector.economy.getJobs
import at.orchaldir.gm.core.selector.rpg.equipment.getEquipmentTypes
import at.orchaldir.gm.core.selector.rpg.getStatisticsBasedOn
import at.orchaldir.gm.core.selector.rpg.statblock.getValuesFor
import at.orchaldir.gm.utils.Id
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
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
    val statblocks = state.getValuesFor(statistic.id)
    val isMusclePowered = state.config.rpg.equipment.musclePoweredStatistic == statistic.id

    showGenericUsage(
        call,
        state,
        listOf(
            state.getJobs(statistic.id),
            state.getEquipmentTypes(statistic.id),
            state.getStatisticsBasedOn(statistic.id),
        ),
        statblocks.isNotEmpty() || isMusclePowered,
    )

    if (isMusclePowered) {
        val dataLink = call.application.href(ConfigRoutes())

        field("Other") {
            ul {
                li {
                    link(dataLink, "Muscle-Powered Statistic")
                }
            }
        }
    }

    showStatblocks(call, state, statistic, statblocks)
}

private fun HtmlBlockTag.showStatblocks(
    call: ApplicationCall,
    state: State,
    statistic: Statistic,
    statblocks: List<Pair<Id<*>, Int>>,
) {
    if (statblocks.isEmpty()) {
        return
    }

    br {}
    table {
        tr {
            th { +"Statblocks" }
            th { +"Type" }
            th { +"Value" }
        }
        statblocks
            .sortedByDescending { it.second }
            .forEach { (statblockId, value) ->
                tr {
                    tdLink(call, state, statblockId)
                    tdString(statblockId.type())
                    tdString(statistic.data.display(value))
                }
            }
    }
}

// edit

fun HtmlBlockTag.editStatistic(
    call: ApplicationCall,
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
fun parseOptionalStatisticId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { StatisticId(it) }

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
