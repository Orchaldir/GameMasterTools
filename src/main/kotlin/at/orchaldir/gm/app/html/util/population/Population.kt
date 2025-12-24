package at.orchaldir.gm.app.html.util.population

import at.orchaldir.gm.app.DENSITY
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.POPULATION
import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.population.*
import at.orchaldir.gm.core.model.util.population.PopulationType.Undefined
import at.orchaldir.gm.core.selector.util.calculatePopulationIndex
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

fun HtmlBlockTag.showPopulation(population: Population) {
    when (population) {
        is AbstractPopulation -> +population.density.toString()
        is PopulationPerRace -> +population.total.toString()
        is TotalPopulation -> +population.total.toString()
        UndefinedPopulation -> doNothing()
    }
}

fun HtmlBlockTag.showRacesPopulation(
    call: ApplicationCall,
    state: State,
    population: Population,
) {
    when (population) {
        is AbstractPopulation -> showInlineIds(call, state, population.races)
        is PopulationPerRace -> showInlineIds(call, state, population.racePercentages.keys)
        is TotalPopulation -> showInlineIds(call, state, population.races)
        UndefinedPopulation -> doNothing()
    }
}

fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showPopulationDetails(
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
        optionalField("Index", state.calculatePopulationIndex(element))

        when (population) {
            is AbstractPopulation -> {
                field("Density", population.density)
                fieldIds(call, state, population.races)
            }
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

            is TotalPopulation -> fieldIds(call, state, population.races)
            UndefinedPopulation -> doNothing()
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
    param: String = POPULATION
) {
    showDetails("Population", true) {
        selectValue("Type", param, PopulationType.entries, population.getType())

        when (population) {
            is AbstractPopulation -> {
                selectValue(
                    "Density",
                    combine(param, DENSITY),
                    Size.entries,
                    population.density,
                )
                selectRaceSet(state, param, population.races)
            }
            is PopulationPerRace -> {
                selectTotalPopulation(param, population.total)

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
                                    combine(param, race.id.value),
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
            is TotalPopulation -> {
                selectTotalPopulation(param, population.total)
                selectRaceSet(state, param, population.races)
            }

            UndefinedPopulation -> doNothing()
        }
    }
}

private fun DETAILS.selectRaceSet(
    state: State,
    param: String,
    races: Set<RaceId>,
) {
    selectElements(
        state,
        "Races",
        combine(param, RACE),
        state.sortRaces(),
        races,
    )
}

private fun DETAILS.selectTotalPopulation(param: String, totalPopulation: Int) {
    selectInt(
        "Total Population",
        totalPopulation,
        0,
        Int.MAX_VALUE,
        1,
        combine(param, NUMBER),
    )
}

// parse

fun parsePopulation(
    parameters: Parameters,
    state: State,
    param: String = POPULATION,
) = when (parse(parameters, param, Undefined)) {
    PopulationType.Abstract -> AbstractPopulation(
        parse(parameters, combine(param, DENSITY), Size.Medium),
        parseRaceSet(parameters, param)
    )

    PopulationType.PerRace -> PopulationPerRace(
        parseTotalPopulation(parameters, param),
        state.getRaceStorage()
            .getAll()
            .associate { race ->
                Pair(race.id, parsePopulationOfRace(parameters, param, race))
            }
            .filter { it.value.isGreaterZero() }
    )
    PopulationType.Total -> TotalPopulation(
        parseTotalPopulation(parameters, param),
        parseRaceSet(parameters, param)
    )

    Undefined -> UndefinedPopulation
}

private fun parseRaceSet(parameters: Parameters, param: String) =
    parseElements(parameters, combine(param, RACE), ::parseRaceId)

private fun parseTotalPopulation(parameters: Parameters, param: String): Int =
    parseInt(parameters, combine(param, NUMBER), 0)

fun parsePopulationOfRace(parameters: Parameters, param: String, race: Race) =
    parseFactor(parameters, combine(param, race.id.value), ZERO)