package at.orchaldir.gm.app.html.util.population

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.parseCultureId
import at.orchaldir.gm.app.html.economy.editIncome
import at.orchaldir.gm.app.html.economy.parseIncome
import at.orchaldir.gm.app.html.economy.showIncome
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.population.*
import at.orchaldir.gm.core.model.util.population.PopulationType.Undefined
import at.orchaldir.gm.core.selector.util.calculatePopulationIndex
import at.orchaldir.gm.core.selector.util.sortCultures
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.ZERO
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag
import kotlinx.html.br

// show

fun HtmlBlockTag.showPopulation(population: Population) {
    when (population) {
        is AbstractPopulation -> +population.density.toString()
        is PopulationDistribution -> +population.total.toString()
        is TotalPopulation -> +population.total.toString()
        UndefinedPopulation -> doNothing()
    }
}

fun HtmlBlockTag.showCulturesOfPopulation(
    call: ApplicationCall,
    state: State,
    population: Population,
) = showInlineIds(call, state, population.cultures(), 2)

fun HtmlBlockTag.showRacesOfPopulation(
    call: ApplicationCall,
    state: State,
    population: Population,
    max: Int = 2,
) = when (population) {
    is PopulationDistribution -> {
        val sorted = population.races.map.entries
            .sortedByDescending { it.value.toPermyriad() }
        showInlineList(sorted, max) { (id, factor) ->
            showTooltip(factor.toString()) {
                link(call, state, id)
            }
        }
    }
    UndefinedPopulation -> doNothing()
    else -> showInlineIds(call, state, population.races(), 2)
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
                showIncome(call, state, population.income)
                field("Density", population.density)
                fieldIds(call, state, population.races)
                fieldIds(call, state, population.cultures)
            }

            is PopulationDistribution -> {
                showIncome(call, state, population.income)
                showElementDistribution(population, call, state, "Race", population.races.map)
                br { }
                showElementDistribution(population, call, state, "Culture", population.cultures.map)
            }

            is TotalPopulation -> {
                showIncome(call, state, population.income)
                fieldIds(call, state, population.races)
                fieldIds(call, state, population.cultures)
            }

            UndefinedPopulation -> doNothing()
        }
    }
}

// edit

fun HtmlBlockTag.editPopulation(
    call: ApplicationCall,
    state: State,
    population: Population,
    param: String = POPULATION,
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
                editIncome(state, population.income, combine(param, INCOME))
                selectRaceSet(state, param, population.races)
                selectCultureSet(state, param, population.cultures)
            }

            is PopulationDistribution -> {
                selectTotalPopulation(param, population.total)
                editIncome(state, population.income, combine(param, INCOME))

                editElementDistribution(
                    call,
                    state,
                    "Race",
                    combine(param, RACE),
                    population,
                    state.sortRaces(),
                    population.races,
                )
                br { }
                editElementDistribution(
                    call,
                    state,
                    "Culture",
                    combine(param, CULTURE),
                    population,
                    state.sortCultures(),
                    population.cultures,
                )
            }

            is TotalPopulation -> {
                selectTotalPopulation(param, population.total)
                editIncome(state, population.income, combine(param, INCOME))
                selectRaceSet(state, param, population.races)
                selectCultureSet(state, param, population.cultures)
            }

            UndefinedPopulation -> doNothing()
        }
    }
}

private fun DETAILS.selectCultureSet(
    state: State,
    param: String,
    cultures: Set<CultureId>,
) {
    selectElements(
        state,
        "Cultures",
        combine(param, CULTURE),
        state.sortCultures(),
        cultures,
    )
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
        parseRaceSet(parameters, param),
        parseCultureSet(parameters, param),
        parseIncome(state, parameters, combine(param, INCOME)),
    )

    PopulationType.Distribution -> PopulationDistribution(
        parseTotalPopulation(parameters, param),
        parseElementDistribution(
            state.getRaceStorage(),
            parameters,
            param,
            ::parsePopulationOfRace,
        ),
        parseElementDistribution(
            state.getCultureStorage(),
            parameters,
            param,
            ::parsePopulationOfCulture,
        ),
        parseIncome(state, parameters, combine(param, INCOME)),
    )

    PopulationType.Total -> TotalPopulation(
        parseTotalPopulation(parameters, param),
        parseRaceSet(parameters, param),
        parseCultureSet(parameters, param),
        parseIncome(state, parameters, combine(param, INCOME)),
    )

    Undefined -> UndefinedPopulation
}

private fun parseCultureSet(parameters: Parameters, param: String) =
    parseElements(parameters, combine(param, CULTURE), ::parseCultureId)

private fun parseRaceSet(parameters: Parameters, param: String) =
    parseElements(parameters, combine(param, RACE), ::parseRaceId)

private fun parseTotalPopulation(parameters: Parameters, param: String): Int =
    parseInt(parameters, combine(param, NUMBER), 0)

fun parsePopulationOfCulture(parameters: Parameters, param: String, culture: Culture) =
    parseFactor(parameters, combine(param, CULTURE, culture.id.value), ZERO)

fun parsePopulationOfRace(parameters: Parameters, param: String, race: Race) =
    parseFactor(parameters, combine(param, RACE, race.id.value), ZERO)