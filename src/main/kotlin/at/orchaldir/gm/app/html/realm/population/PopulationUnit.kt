package at.orchaldir.gm.app.html.realm.population

import at.orchaldir.gm.app.CULTURE
import at.orchaldir.gm.app.INCOME
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.TOTAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.parseCultureId
import at.orchaldir.gm.app.html.economy.displayIncome
import at.orchaldir.gm.app.html.economy.editIncome
import at.orchaldir.gm.app.html.economy.parseIncome
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.realm.population.MAX_POPULATION
import at.orchaldir.gm.core.model.realm.population.PopulationUnit
import at.orchaldir.gm.core.model.realm.population.PopulationUnitsWithNumbers
import at.orchaldir.gm.core.model.realm.population.PopulationUnitsWithPercentages
import at.orchaldir.gm.core.selector.util.sortCultures
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE_PERCENT
import at.orchaldir.gm.utils.math.ONE_TENTH_PERCENT
import at.orchaldir.gm.utils.math.ZERO
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*
import kotlin.collections.List
import kotlin.collections.forEach
import kotlin.collections.sortedByDescending
import kotlin.collections.withIndex

// show

fun HtmlBlockTag.showPopulationUnitsWithNumbers(
    call: ApplicationCall,
    state: State,
    population: PopulationUnitsWithNumbers,
    total: Int,
) {
    table {
        showHeader()
        population.units
            .sortedByDescending { it.value }
            .forEach { unit ->
                tr {
                    tdLink(call, state, unit.race)
                    tdLink(call, state, unit.culture)
                    td {
                        displayIncome(call, state, unit.income)
                    }
                    showPercentageAndNumber(total, unit.value)
                }
            }

        if (population.undefined > 0) {
            tr {
                td {
                    colSpan = "3"
                    +"Other"
                }
                showPercentageAndNumber(total, population.undefined)
            }
        }
    }
}

fun HtmlBlockTag.showPopulationUnitsWithPercentages(
    call: ApplicationCall,
    state: State,
    units: List<PopulationUnit<Factor>>,
    total: Int,
) {
    var remaining = Factor.fromPercentage(100)

    table {
        showHeader()
        units
            .sortedByDescending { it.value.toPermyriad() }
            .forEach { unit ->
                tr {
                    tdLink(call, state, unit.race)
                    tdLink(call, state, unit.culture)
                    td {
                        displayIncome(call, state, unit.income)
                    }
                    showPercentageAndNumber(total, unit.value)
                }

                remaining -= unit.value
            }

        if (remaining.isGreaterZero()) {
            tr {
                td {
                    colSpan = "3"
                    +"Other"
                }
                showPercentageAndNumber(total, remaining)
            }
        }
    }
}

private fun TABLE.showHeader() {
    tr {
        th { +"Race" }
        th { +"Culture" }
        th { +"Income" }
        th { +"Percentage" }
        th { +"Number" }
    }
}

// edit

fun HtmlBlockTag.editPopulationUnitsWithNumbers(
    state: State,
    param: String,
    population: PopulationUnitsWithNumbers,
    total: Int,
) {
    val races = state.sortRaces()
    val cultures = state.sortCultures()

    selectNumberOfUnits(param, population.units.size)

    table {
        showHeader()
        population.units.withIndex().forEach { (index, unit) ->
            val unitParam = combine(param, index)

            tr {
                editUnit(state, unitParam, unit, cultures, races)
                tdPercentage(Factor.divideTwoInts(unit.value, total))
                td {
                    selectInt(
                        unit.value,
                        1,
                        MAX_POPULATION,
                        1,
                        combine(unitParam, NUMBER),
                    )
                }
            }
        }

        tr {
            td {
                colSpan = "2"
                +"Other"
            }
            tdPercentage(Factor.divideTwoInts(population.undefined, total))
            td {
                selectInt(
                    population.undefined,
                    0,
                    MAX_POPULATION,
                    1,
                    combine(param, TOTAL, NUMBER),
                )
            }
        }
    }
}

fun HtmlBlockTag.editPopulationUnitsWithPercentages(
    state: State,
    param: String,
    population: PopulationUnitsWithPercentages,
    total: Int? = null,
) {
    val remaining = population.getUndefinedPercentages()
    val races = state.sortRaces()
    val cultures = state.sortCultures()

    selectNumberOfUnits(param, population.units.size)

    table {
        tr {
            th { +"Race" }
            th { +"Culture" }
            th { +"Income" }
            th { +"Percentage" }
            if (total != null) {
                th { +"Number" }
            }
        }
        population.units.withIndex().forEach { (index, unit) ->
            val unitParam = combine(param, index)
            val minValue = if (unit.value.isGreaterZero() && population.units.size == 1) {
                ONE_TENTH_PERCENT
            } else {
                ZERO
            }

            tr {
                editUnit(state, unitParam, unit, cultures, races)
                td {
                    selectFactor(
                        combine(unitParam, NUMBER),
                        unit.value,
                        minValue,
                        FULL.min(unit.value + remaining),
                        ONE_TENTH_PERCENT,
                    )
                }
                if (total != null) {
                    showElementNumber(total, unit.value)
                }
            }
        }

        if (remaining.isGreaterZero()) {
            tr {
                td {
                    colSpan = "2"
                    +"Other"
                }
                if (total != null) {
                    showPercentageAndNumber(total, remaining)
                } else {
                    tdPercentage(remaining)
                }
            }

        }
    }
}

private fun HtmlBlockTag.selectNumberOfUnits(
    param: String,
    number: Int,
) {
    selectInt(
        "Units",
        number,
        1,
        100,
        1,
        combine(param, NUMBER),
    )
}

private fun <T> TR.editUnit(
    state: State,
    unitParam: String,
    unit: PopulationUnit<T>,
    cultures: List<Culture>,
    races: List<Race>,
) {
    td {
        selectElement(
            state,
            combine(unitParam, RACE),
            races,
            unit.race,
        )
    }
    td {
        selectElement(
            state,
            combine(unitParam, CULTURE),
            cultures,
            unit.culture,
        )
    }
    td {
        editIncome(state, unit.income, combine(unitParam, INCOME))
    }
}

// parse

fun parsePopulationUnitsWithNumbers(
    state: State,
    parameters: Parameters,
    param: String,
) = PopulationUnitsWithNumbers(
    parseList(
        parameters,
        param,
        1,
    ) { _, unitParam ->
        PopulationUnit(
            parseInt(parameters, combine(unitParam, NUMBER), 100),
            parseRaceId(parameters, combine(unitParam, RACE)),
            parseCultureId(parameters, combine(unitParam, CULTURE)),
            parseIncome(state, parameters, combine(unitParam, INCOME))
        )
    }
)

fun parsePopulationUnitsWithPercentages(
    state: State,
    parameters: Parameters,
    param: String,
) = parseList(
    parameters,
    param,
    1,
) { _, unitParam ->
    PopulationUnit(
        parseFactor(parameters, combine(unitParam, NUMBER), ONE_PERCENT),
        parseRaceId(parameters, combine(unitParam, RACE)),
        parseCultureId(parameters, combine(unitParam, CULTURE)),
        parseIncome(state, parameters, combine(unitParam, INCOME))
    )
}