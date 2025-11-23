package at.orchaldir.gm.app.html.util.population

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.POPULATION
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.util.population.*
import at.orchaldir.gm.core.model.util.population.PopulationType.Undefined
import at.orchaldir.gm.core.selector.util.getPopulationIndex
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE_PERCENT
import at.orchaldir.gm.utils.math.ZERO
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showPopulation(
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation {
    val population = element.population()

    if (population is UndefinedPopulation) {
        return
    }

    showDetails("Population", true) {
        optionalField("Total", population.getTotalPopulation())
        optionalField("Index", state.getPopulationIndex(element))

        when (population) {
            is PopulationPerRace -> {
                var remaining = Factor.fromPercentage(100)

                table {
                    tr {
                        th { +"Race" }
                        th { +"Percentage" }
                        th { +"Number" }
                    }
                    population.racePercentages
                        .toList()
                        .sortedByDescending { it.second.toPermyriad() }
                        .forEach { (raceId, percentage) ->

                            tr {
                                tdLink(call, state, raceId)
                                showPercentageAndNumber(population.total, percentage)
                            }

                            remaining = remaining - percentage
                        }

                    showRemainingPopulation(population, remaining)
                }
            }

            is TotalPopulation, UndefinedPopulation -> doNothing()
        }
    }
}

private fun TABLE.showRemainingPopulation(
    population: PopulationPerRace,
    remaining: Factor,
) {
    if (remaining.isGreaterZero()) {
        tr {
            tdString("Other")
            showPercentageAndNumber(population.total, remaining)
        }
    }
}

fun TR.showPercentageAndNumber(
    total: Int,
    percentage: Factor,
) {
    tdPercentage(percentage)
    showRaceNumber(total, percentage)
}

private fun TR.showRaceNumber(
    total: Int,
    percentage: Factor,
) {
    tdSkipZero(percentage.apply(total))
}

// edit

fun HtmlBlockTag.editPopulation(
    call: ApplicationCall,
    state: State,
    population: Population,
) {
    showDetails("Population", true) {
        selectValue("Type", POPULATION, PopulationType.entries, population.getType())

        when (population) {
            is TotalPopulation -> selectTotalPopulation(population.total)
            is PopulationPerRace -> {
                selectTotalPopulation(population.total)

                val remaining = population.getUndefinedPercentage()

                table {
                    tr {
                        th { +"Race" }
                        th { +"Percentage" }
                        th { +"Number" }
                    }
                    state.sortRaces().forEach { race ->
                        val percentage = population.getPercentage(race.id)
                        val minValue = if (percentage.isGreaterZero() && population.racePercentages.count() == 1) {
                            ONE_PERCENT
                        } else {
                            ZERO
                        }

                        tr {
                            tdLink(call, state, race)
                            td {
                                selectFactor(
                                    combine(POPULATION, race.id.value),
                                    percentage,
                                    minValue,
                                    FULL.min(percentage + remaining),
                                    ONE_PERCENT,
                                )
                            }
                            showRaceNumber(population.total, percentage)
                        }
                    }

                    showRemainingPopulation(population, remaining)
                }
            }

            UndefinedPopulation -> doNothing()
        }
    }
}

private fun DETAILS.selectTotalPopulation(totalPopulation: Int) {
    selectInt(
        "Total Population",
        totalPopulation,
        0,
        Int.MAX_VALUE,
        1,
        combine(POPULATION, NUMBER),
    )
}

// parse

fun parsePopulation(parameters: Parameters, state: State) = when (parse(parameters, POPULATION, Undefined)) {
    PopulationType.Total -> TotalPopulation(
        parseTotalPopulation(parameters),
    )

    PopulationType.PerRace -> PopulationPerRace(
        parseTotalPopulation(parameters),
        state.getRaceStorage()
            .getAll()
            .associate { race ->
                Pair(race.id, parsePopulationOfRace(parameters, race))
            }
            .filter { it.value.isGreaterZero() }
    )

    Undefined -> UndefinedPopulation
}

private fun parseTotalPopulation(parameters: Parameters): Int = parseInt(parameters, combine(POPULATION, NUMBER), 0)

fun parsePopulationOfRace(parameters: Parameters, race: Race) =
    parseFactor(parameters, combine(POPULATION, race.id.value), ZERO)