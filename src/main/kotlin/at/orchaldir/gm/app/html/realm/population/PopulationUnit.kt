package at.orchaldir.gm.app.html.realm.population

import at.orchaldir.gm.app.CULTURE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.parseCultureId
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.population.PopulationUnit
import at.orchaldir.gm.core.model.realm.population.PopulationUnitsWithPercentages
import at.orchaldir.gm.core.selector.util.sortCultures
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE_TENTH_PERCENT
import at.orchaldir.gm.utils.math.ZERO
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showPopulationUnitsWithPercentages(
    call: ApplicationCall,
    state: State,
    units: List<PopulationUnit<Factor>>,
    total: Int,
) {
    var remaining = Factor.fromPercentage(100)

    table {
        tr {
            th { +"Race" }
            th { +"Culture" }
            th { +"Percentage" }
            th { +"Number" }
        }
        units
            .sortedByDescending { it.value.toPermyriad() }
            .forEach { unit ->
                tr {
                    tdLink(call, state, unit.race)
                    tdLink(call, state, unit.culture)
                    showPercentageAndNumber(total, unit.value)
                }

                remaining -= unit.value
            }

        if (remaining.isGreaterZero()) {
            tr {
                td {
                    rowSpan = "2"
                    +"Other"
                }
                showPercentageAndNumber(total, remaining)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editPopulationUnitsWithPercentages(
    state: State,
    param: String,
    population: PopulationUnitsWithPercentages,
    total: Int? = null,
) {
    val remaining = population.getUndefinedPercentages()
    val races = state.sortRaces()
    val cultures = state.sortCultures()

    selectInt(
        "Units",
        population.units.size,
        1,
        100,
        1,
        combine(param, NUMBER),
    )

    table {
        tr {
            th { +"Race" }
            th { +"Culture" }
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
                    rowSpan = "2"
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

// parse

fun parsePopulationUnitsWithPercentages(
    parameters: Parameters,
    param: String,
) = parseList(
    parameters,
    param,
    1,
) { _, unitParam ->
    PopulationUnit(
        parseFactor(parameters, combine(unitParam, NUMBER)),
        parseRaceId(parameters, combine(unitParam, RACE)),
        parseCultureId(parameters, combine(unitParam, CULTURE)),
    )
}