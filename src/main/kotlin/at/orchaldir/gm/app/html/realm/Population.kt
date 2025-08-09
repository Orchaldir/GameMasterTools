package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.POPULATION
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.realm.Population
import at.orchaldir.gm.core.model.realm.PopulationType
import at.orchaldir.gm.core.model.realm.PopulationType.Undefined
import at.orchaldir.gm.core.model.realm.SimplePopulation
import at.orchaldir.gm.core.model.realm.UndefinedPopulation
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showPopulation(
    call: ApplicationCall,
    state: State,
    population: Population,
) {
    when (population) {
        is SimplePopulation -> {
            val totalPopulation = population.calculateTotalPopulation() ?: 0
            field("Total Population", totalPopulation)

            table {
                tr {
                    th { +"Race" }
                    th { +"Number" }
                    th { +"Percentage" }
                }
                population.raceMap
                    .toList()
                    .sortedBy { it.second }
                    .forEach { (raceId, number) ->
                        tr {
                            tdLink(call, state, raceId)
                            tdSkipZero(number)
                            tdPercentage(number, totalPopulation)
                        }
                    }
            }
        }

        UndefinedPopulation -> doNothing()
    }
}

// edit

fun FORM.editPopulation(
    call: ApplicationCall,
    state: State,
    population: Population,
) {
    showDetails("Population", true) {
        selectValue("Type", POPULATION, PopulationType.entries, population.getType())

        when (population) {
            is SimplePopulation -> {
                val totalPopulation = population.calculateTotalPopulation() ?: 0
                field("Total Population", totalPopulation)

                table {
                    tr {
                        th { +"Race" }
                        th { +"Number" }
                        th { +"Percentage" }
                    }
                    state.sortRaces()
                        .forEach { race ->
                            val number = population.raceMap.getOrDefault(race.id, 0)
                            tr {
                                tdLink(call, state, race)
                                td {
                                    selectInt(
                                        number,
                                        0,
                                        Int.MAX_VALUE,
                                        1,
                                        combine(POPULATION, race.id.value),
                                    )
                                }
                                tdPercentage(number, totalPopulation)
                            }
                        }
                }
            }

            UndefinedPopulation -> doNothing()
        }
    }
}

// parse

fun parsePopulation(parameters: Parameters, state: State) = when (parse(parameters, POPULATION, Undefined)) {
    PopulationType.Simple -> SimplePopulation(
        state.getRaceStorage()
            .getAll()
            .associate { race ->
                Pair(race.id, parsePopulationOfRace(parameters, race))
            }
            .filter { it.value > 0 }
    )

    Undefined -> UndefinedPopulation
}

fun parsePopulationOfRace(parameters: Parameters, race: Race) =
    parseInt(parameters, combine(POPULATION, race.id.value), 0)