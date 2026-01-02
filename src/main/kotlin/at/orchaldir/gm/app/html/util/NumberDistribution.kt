package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun <ID : Id<ID>> HtmlBlockTag.showInlineNumberDistribution(
    call: ApplicationCall,
    state: State,
    distribution: NumberDistribution<ID>,
    max: Int = 2,
) {
    val sorted = distribution.map.entries
        .sortedByDescending { it.value }

    showInlineList(sorted, max) { (id, factor) ->
        showTooltip(factor.toString()) {
            link(call, state, id)
        }
    }
}

fun <ID : Id<ID>> DETAILS.showNumberDistribution(
    call: ApplicationCall,
    state: State,
    label: String,
    distribution: NumberDistribution<ID>,
    total: Int,
) {
    var remaining = total

    table {
        tr {
            th { +label }
            th { +"Percentage" }
            th { +"Number" }
        }
        distribution
            .map
            .toList()
            .sortedByDescending { it.second }
            .forEach { (raceId, number) ->

                tr {
                    tdLink(call, state, raceId)
                    showPercentageAndNumber(total, number)
                }

                remaining -= number
            }

        showRemainingPopulation(total, remaining)
        showTotalPopulation(total)
    }
}

private fun TABLE.showRemainingPopulation(
    total: Int,
    remaining: Int,
) {
    if (remaining > 0) {
        tr {
            tdString("Other")
            showPercentageAndNumber(total, remaining)
        }
    }
}

private fun TABLE.showTotalPopulation(total: Int) {
    tr {
        tdString("Total")
        showPercentageAndNumber(total, total)
    }
}

private fun TR.showPercentageAndNumber(
    total: Int,
    number: Int,
) {
    tdPercentage(Factor.divideTwoInts(number, total))
    tdSkipZero(number)
}


// edit

fun <ID : Id<ID>, ELEMENT : Element<ID>> DETAILS.editNumberDistribution(
    call: ApplicationCall,
    state: State,
    label: String,
    param: String,
    allElements: List<ELEMENT>,
    distribution: NumberDistribution<ID>,
    total: Int,
) {
    var remaining = total

    table {
        tr {
            th { +label }
            th { +"Percentage" }
            th { +"Number" }
        }
        allElements.forEach { element ->
            val number = distribution.getNumber(element.id())
            val percentage = Factor.divideTwoInts(number, total)
            val minValue = if (number > 0 && distribution.map.count() == 1) {
                1
            } else {
                0
            }

            tr {
                tdLink(call, state, element)
                tdPercentage(percentage)
                td {
                    selectInt(
                        number,
                        minValue,
                        Int.MAX_VALUE,
                        1,
                        combine(param, element.id().value()),
                    )
                }
            }

            remaining -= number
        }

        tr {
            tdString("Unknown")
            tdPercentage(Factor.divideTwoInts(distribution.unknown, total))
            td {
                selectInt(
                    distribution.unknown,
                    0,
                    Int.MAX_VALUE,
                    1,
                    combine(param, NUMBER),
                )
            }
        }

        remaining -= distribution.unknown

        showRemainingPopulation(total, remaining)
        showTotalPopulation(total)
    }
}

// parse

fun <ID : Id<ID>, ELEMENT : Element<ID>> parseNumberDistribution(
    storage: Storage<ID, ELEMENT>,
    parameters: Parameters,
    param: String,
): NumberDistribution<ID> = NumberDistribution(
    storage
        .getAll()
        .associate { element ->
            Pair(element.id(), parseInt(parameters, combine(param, element.id().value())))
        }
        .filter { it.value > 0 },
    parseInt(parameters, combine(param, NUMBER)),
)
