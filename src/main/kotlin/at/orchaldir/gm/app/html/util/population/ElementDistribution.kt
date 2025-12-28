package at.orchaldir.gm.app.html.util.population

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.population.ElementDistribution
import at.orchaldir.gm.core.model.util.population.PopulationDistribution
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

fun <ID : Id<ID>> HtmlBlockTag.showInlineElementDistribution(
    call: ApplicationCall,
    state: State,
    distribution: ElementDistribution<ID>,
    max: Int = 2,
) {
    val sorted = distribution.map.entries
        .sortedByDescending { it.value.toPermyriad() }

    showInlineList(sorted, max) { (id, factor) ->
        showTooltip(factor.toString()) {
            link(call, state, id)
        }
    }
}

fun <ID : Id<ID>> DETAILS.showElementDistribution(
    population: PopulationDistribution,
    call: ApplicationCall,
    state: State,
    label: String,
    distribution: Map<ID, Factor>,
) {
    var remaining = Factor.fromPercentage(100)

    table {
        tr {
            th { +label }
            th { +"Percentage" }
            th { +"Number" }
        }
        distribution
            .toList()
            .sortedByDescending { it.second.toPermyriad() }
            .forEach { (raceId, percentage) ->

                tr {
                    tdLink(call, state, raceId)
                    showPercentageAndNumber(population.total, percentage)
                }

                remaining -= percentage
            }

        showRemainingPopulation(population, remaining)
    }
}

private fun TABLE.showRemainingPopulation(
    population: PopulationDistribution,
    remaining: Factor,
) {
    if (remaining.isGreaterZero()) {
        tr {
            tdString("Other")
            showPercentageAndNumber(population.total, remaining)
        }
    }
}

private fun TR.showPercentageAndNumber(
    total: Int,
    percentage: Factor,
) {
    tdPercentage(percentage)
    showElementNumber(total, percentage)
}

private fun TR.showElementNumber(
    total: Int,
    percentage: Factor,
) {
    tdSkipZero(percentage.apply(total))
}

// edit

fun <ID : Id<ID>, ELEMENT : Element<ID>> DETAILS.editElementDistribution(
    call: ApplicationCall,
    state: State,
    label: String,
    param: String,
    population: PopulationDistribution,
    allElements: List<ELEMENT>,
    distribution: ElementDistribution<ID>,
) {
    val remaining = distribution.getUndefinedPercentages()

    table {
        tr {
            th { +label }
            th { +"Percentage" }
            th { +"Number" }
        }
        allElements.forEach { element ->
            val percentage = distribution.getPercentage(element.id())
            val minValue = if (percentage.isGreaterZero() && distribution.map.count() == 1) {
                ONE_TENTH_PERCENT
            } else {
                ZERO
            }

            tr {
                tdLink(call, state, element)
                td {
                    selectFactor(
                        combine(param, element.id().value()),
                        percentage,
                        minValue,
                        FULL.min(percentage + remaining),
                        ONE_TENTH_PERCENT,
                    )
                }
                showElementNumber(population.total, percentage)
            }
        }

        showRemainingPopulation(population, remaining)
    }
}

// parse

fun <ID : Id<ID>, ELEMENT : Element<ID>> parseElementDistribution(
    storage: Storage<ID, ELEMENT>,
    parameters: Parameters,
    param: String,
    parsePopulation: (Parameters, String, ELEMENT) -> Factor,
): ElementDistribution<ID> = ElementDistribution(
    storage
        .getAll()
        .associate { element ->
            Pair(element.id(), parsePopulation(parameters, param, element))
        }
        .filter { it.value.isGreaterZero() }
)
