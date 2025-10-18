package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.STATISTIC
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.tdInt
import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.Statblock
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.selector.rpg.getAttributes
import at.orchaldir.gm.core.selector.rpg.getBaseDamageValues
import at.orchaldir.gm.core.selector.rpg.getDerivedAttributes
import at.orchaldir.gm.core.selector.rpg.getSkills
import at.orchaldir.gm.core.selector.util.sortStatistics
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag
import kotlinx.html.TABLE
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

fun HtmlBlockTag.showStatblock(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
) {
    val attributes = state.sortStatistics(state.getAttributes())
    val attributeValues = statblock.resolve(state, attributes)
    val derivedAttributes = state.sortStatistics(state.getDerivedAttributes())
    val derivedValues = statblock.resolve(state, derivedAttributes)
    val baseDamages = state.sortStatistics(state.getBaseDamageValues())
    val baseDamageValues = statblock.resolve(state, baseDamages)
    val skills = state.sortStatistics(state.getSkills())
    val skillValues = statblock.resolve(state, skills)

    showDetails("Stateblock", true) {
        showStatistics(call, state, attributeValues, "Attributes")
        showStatistics(call, state, derivedValues, "Derived Attributes")
        showStatistics(call, state, baseDamageValues, "Base Damage Values")
        showStatistics(call, state, skillValues, "Skills")
        field("Cost", statblock.calculateCost(state))
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
        +": "
        +statistic.data.display(value)
    }
}

fun HtmlBlockTag.editStatblock(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
) {
    val attributes = state.sortStatistics(state.getAttributes())
    val derivedAttributes = state.sortStatistics(state.getDerivedAttributes())
    val damageValues = state.sortStatistics(state.getBaseDamageValues())
    val skills = state.sortStatistics(state.getSkills())

    showDetails("Stateblock", true) {
        table {
            editStatistics(state, call, statblock, attributes, "Attribute")
            editStatistics(state, call, statblock, derivedAttributes, "Derived Attribute")
            editStatistics(state, call, statblock, damageValues, "Base Damage Value")
            editStatistics(state, call, statblock, skills, "Skills")
        }
        field("Cost", statblock.calculateCost(state))
    }
}

private fun TABLE.editStatistics(
    state: State,
    call: ApplicationCall,
    statblock: Statblock,
    statistics: List<Statistic>,
    label: String,
) {
    tr {
        th { +label }
        th { +"Offset" }
        th { +"Result" }
        th { +"Cost" }
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
            tdInt(statistic.data.cost().calculate(offset))
        }
    }
}

fun parseStatblock(
    state: State,
    parameters: Parameters,
): Statblock {
    val values = mutableMapOf<StatisticId, Int>()

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