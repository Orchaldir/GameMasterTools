package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.STATISTIC
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.Statblock
import at.orchaldir.gm.core.model.character.statistic.Statistic
import at.orchaldir.gm.core.model.character.statistic.StatisticId
import at.orchaldir.gm.core.selector.character.getAttributes
import at.orchaldir.gm.core.selector.character.getSkills
import at.orchaldir.gm.core.selector.util.sortStatistics
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

// show

fun HtmlBlockTag.showStatblock(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
) {
    val attributes = state.sortStatistics(state.getAttributes())
    val attributeValues = statblock.resolve(state, attributes)
    val skills = state.sortStatistics(state.getSkills())
    val skillValues = statblock.resolve(state, skills)

    showDetails("Stateblock", true) {
        showStatistics(call, state, attributeValues, "Attributes")
        showStatistics(call, state, skillValues, "Skills")
    }
}

private fun DETAILS.showStatistics(
    call: ApplicationCall,
    state: State,
    values: List<Pair<Statistic, Int>>,
    label: String,
) {
    fieldList(label, values) { (statistic, value) ->
        link(call, state, statistic)
        +": $value"
    }
}

// edit

fun FORM.editStatblock(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
) {
    val attributes = state.sortStatistics(state.getAttributes())
    val skills = state.sortStatistics(state.getSkills())

    showDetails("Stateblock", true) {
        editStatistics(state, call, statblock, attributes, "Attribute")
        editStatistics(state, call, statblock, skills, "Skills")
    }
}

private fun DETAILS.editStatistics(
    state: State,
    call: ApplicationCall,
    statblock: Statblock,
    statistics: List<Statistic>,
    label: String,
) {
    table {
        tr {
            th { +label }
            th { +"Offset" }
            th { +"Result" }
        }
        statistics.forEach { statistic ->
            val offset = statblock.statistics[statistic.id] ?: 0
            val value = statblock.resolve(state, statistic)

            tr {
                tdLink(call, state, statistic)
                td {
                    selectInt(
                        offset,
                        -10,
                        +10,
                        1,
                        combine(STATISTIC, statistic.id.value),
                    )
                }
                tdSkipZero(value)
            }
        }
    }
}

// parse

fun parseStatblock(
    state: State,
    parameters: Parameters,
): Statblock {
    val values = mutableMapOf<StatisticId,Int>()

    state.getStatisticStorage()
        .getAll()
        .forEach { attribute ->
        parseSimpleOptionalInt(parameters, combine(STATISTIC, attribute.id.value))?.let {
            if (it != 0) {
                values[attribute.id] = it
            }
        }
    }

    return Statblock(values)
}
