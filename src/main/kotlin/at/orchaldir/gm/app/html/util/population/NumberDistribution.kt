package at.orchaldir.gm.app.html.util.population

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.population.NumberDistribution
import at.orchaldir.gm.core.model.util.population.PopulationWithNumbers
import at.orchaldir.gm.core.model.util.population.PopulationWithPercentages
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE_TENTH_PERCENT
import at.orchaldir.gm.utils.math.ZERO
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
    population: PopulationWithNumbers,
    call: ApplicationCall,
    state: State,
    label: String,
    distribution: NumberDistribution<ID>,
) {
    val total = population.calculateTotal()
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
    population: PopulationWithNumbers,
    allElements: List<ELEMENT>,
    distribution: NumberDistribution<ID>,
) {
    val total = population.calculateTotal()
    val remaining = total

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
        }

        showRemainingPopulation(total, remaining)
    }
}

// parse

fun <ID : Id<ID>, ELEMENT : Element<ID>> parseNumberDistribution(
    storage: Storage<ID, ELEMENT>,
    parameters: Parameters,
    param: String,
    parsePopulation: (Parameters, String, ELEMENT) -> Int,
): NumberDistribution<ID> = NumberDistribution(
    storage
        .getAll()
        .associate { element ->
            Pair(element.id(), parsePopulation(parameters, param, element))
        }
        .filter { it.value > 0 }
)
