package at.orchaldir.gm.app.html.realm.population

import at.orchaldir.gm.app.CULTURE
import at.orchaldir.gm.app.INCOME
import at.orchaldir.gm.app.POPULATION
import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.parseCultureId
import at.orchaldir.gm.app.html.economy.editIncome
import at.orchaldir.gm.app.html.economy.parseIncome
import at.orchaldir.gm.app.html.economy.showIncome
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.realm.population.*
import at.orchaldir.gm.core.model.realm.population.PopulationType.Undefined
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.realm.calculateRankOfElementWithPopulation
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

fun HtmlBlockTag.displayPopulation(
    call: ApplicationCall,
    state: State,
    population: Population,
) {
    when (population) {
        is PopulationWithNumbers -> +population.calculateTotal().toString()
        is PopulationWithPercentages -> displayTotalPopulation(call, state, population.total)
        is PopulationWithSets -> displayTotalPopulation(call, state, population.total)
        UndefinedPopulation -> doNothing()
    }
}

fun HtmlBlockTag.showCulturesOfPopulation(
    call: ApplicationCall,
    state: State,
    population: Population,
    max: Int = 2,
) = when (population) {
    is PopulationWithNumbers -> showInlineNumberDistribution(call, state, population.cultures, max)
    is PopulationWithPercentages -> showInlinePercentageDistribution(call, state, population.cultures, max)
    UndefinedPopulation -> doNothing()
    else -> showInlineIds(call, state, population.cultures(), max)
}

fun HtmlBlockTag.showRacesOfPopulation(
    call: ApplicationCall,
    state: State,
    population: Population,
    max: Int = 2,
) = when (population) {
    is PopulationWithNumbers -> showInlineNumberDistribution(call, state, population.races, max)
    is PopulationWithPercentages -> showInlinePercentageDistribution(call, state, population.races, max)
    UndefinedPopulation -> doNothing()
    else -> showInlineIds(call, state, population.races(), max)
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
    val total = population.getTotalPopulation()
    val totalOrZero = total ?: 0

    showDetails("Population", true) {
        optionalField("Total", population.getTotalPopulation())
        optionalField("Rank", state.calculateRankOfElementWithPopulation(element))

        when (population) {
            is PopulationWithNumbers -> {
                showIncome(call, state, population.income)
                showNumberDistribution(call, state, "Race", population.races, totalOrZero)
                br { }
                showNumberDistribution(call, state, "Culture", population.cultures, totalOrZero)
            }

            is PopulationWithPercentages -> {
                fieldTotalPopulation(call, state, population.total)
                showIncome(call, state, population.income)
                showPercentageDistribution(call, state, "Race", population.races, totalOrZero)
                br { }
                showPercentageDistribution(call, state, "Culture", population.cultures, totalOrZero)
            }

            is PopulationWithSets -> {
                fieldTotalPopulation(call, state, population.total)
                showIncome(call, state, population.income)
                fieldIds(call, state, population.races)
                fieldIds(call, state, population.cultures)
            }

            UndefinedPopulation -> doNothing()
        }

        fieldElements(call, state, state.getCharactersLivingIn(element.id()))
    }
}

// edit

fun HtmlBlockTag.editPopulation(
    call: ApplicationCall,
    state: State,
    population: Population,
    allowedTotalPopulationTypes: Collection<TotalPopulationType>,
    param: String = POPULATION,
) {
    val total = population.getTotalPopulation() ?: 0

    showDetails("Population", true) {
        selectValue("Type", param, PopulationType.entries, population.getType())

        when (population) {
            is PopulationWithNumbers -> {
                editIncome(state, population.income, combine(param, INCOME))

                editNumberDistribution(
                    call,
                    state,
                    "Race",
                    combine(param, RACE),
                    state.sortRaces(),
                    population.races,
                    total,
                )
                br { }
                editNumberDistribution(
                    call,
                    state,
                    "Culture",
                    combine(param, CULTURE),
                    state.sortCultures(),
                    population.cultures,
                    total,
                )
            }

            is PopulationWithPercentages -> {
                editTotalPopulation(state, population.total, param, allowedTotalPopulationTypes)
                editIncome(state, population.income, combine(param, INCOME))

                editPercentageDistribution(
                    call,
                    state,
                    "Race",
                    combine(param, RACE),
                    state.sortRaces(),
                    population.races,
                    total,
                )
                br { }
                editPercentageDistribution(
                    call,
                    state,
                    "Culture",
                    combine(param, CULTURE),
                    state.sortCultures(),
                    population.cultures,
                    total,
                )
            }

            is PopulationWithSets -> {
                editTotalPopulation(state, population.total, param, allowedTotalPopulationTypes)
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


// parse

fun parsePopulation(
    parameters: Parameters,
    state: State,
    param: String = POPULATION,
) = when (parse(parameters, param, Undefined)) {
    PopulationType.Numbers -> PopulationWithNumbers(
        parseNumberDistribution(
            state.getRaceStorage(),
            parameters,
            combine(param, RACE),
        ),
        parseNumberDistribution(
            state.getCultureStorage(),
            parameters,
            combine(param, CULTURE),
        ),
        parseIncome(state, parameters, combine(param, INCOME)),
    )

    PopulationType.Percentages -> PopulationWithPercentages(
        parseTotalPopulation(parameters, param),
        parsePercentageDistribution(
            state.getRaceStorage(),
            parameters,
            param,
            ::parsePercentageOfRace,
        ),
        parsePercentageDistribution(
            state.getCultureStorage(),
            parameters,
            param,
            ::parsePercentageOfCulture,
        ),
        parseIncome(state, parameters, combine(param, INCOME)),
    )

    PopulationType.Sets -> PopulationWithSets(
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

fun parsePercentageOfCulture(parameters: Parameters, param: String, culture: Culture) =
    parseFactor(parameters, combine(param, CULTURE, culture.id.value), ZERO)

fun parsePercentageOfRace(parameters: Parameters, param: String, race: Race) =
    parseFactor(parameters, combine(param, RACE, race.id.value), ZERO)