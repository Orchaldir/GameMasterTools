package at.orchaldir.gm.app.html.util.population

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.core.model.util.population.IPopulationWithSets
import at.orchaldir.gm.core.model.util.population.Population
import at.orchaldir.gm.core.model.util.population.PopulationDistribution
import at.orchaldir.gm.core.selector.util.calculatePopulationIndex
import at.orchaldir.gm.core.selector.util.calculateTotalPopulation
import at.orchaldir.gm.core.selector.util.getAbstractPopulations
import at.orchaldir.gm.core.selector.util.getPopulationEntries
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showPopulationOfCulture(
    call: ApplicationCall,
    state: State,
    culture: CultureId,
) = showPopulationOfElement(
    call,
    state,
    state.getCultureStorage(),
    culture,
    { it.cultures().contains(culture) },
    { when (val population = it.population()) {
        is PopulationDistribution -> population.cultures.getData(culture, population.total)
        else -> null
    } },
    { it.getPopulation(culture)},
)

fun HtmlBlockTag.showPopulationOfRace(
    call: ApplicationCall,
    state: State,
    race: RaceId,
) = showPopulationOfElement(
    call,
    state,
    state.getRaceStorage(),
    race,
    { it.races().contains(race) },
    { when (val population = it.population()) {
        is PopulationDistribution -> population.races.getData(race, population.total)
        else -> null
    } },
    { it.getPopulation(race)},
)

fun <ID : Id<ID>, ELEMENT: Element<ID>> HtmlBlockTag.showPopulationOfElement(
    call: ApplicationCall,
    state: State,
    storage: Storage<ID, ELEMENT>,
    id: ID,
    contains: (IPopulationWithSets) -> Boolean,
    getPercentage: (HasPopulation) -> Pair<Int, Factor>?,
    getPopulation: (Population) -> Int?
) {
    h2 { +"Population" }

    optionalField("Total", state.calculateTotalPopulation(getPopulation))
    optionalField("Index", state.calculatePopulationIndex(storage, id, getPopulation))

    showPopulationOfRace(call, state, getPercentage, state.getDistrictStorage(), contains)
    showPopulationOfRace(call, state, getPercentage, state.getRealmStorage(), contains)
    showPopulationOfRace(call, state, getPercentage, state.getTownStorage(), contains)
}

private fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showPopulationOfRace(
    call: ApplicationCall,
    state: State,
    getPercentage: (HasPopulation) -> Pair<Int, Factor>?,
    storage: Storage<ID, ELEMENT>,
    contains: (IPopulationWithSets) -> Boolean,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation {
    val elementsWithAbstractPopulation = getAbstractPopulations(storage, contains)
    val entries = getPopulationEntries(storage, getPercentage)
    val total = entries.sumOf { it.number }

    if (elementsWithAbstractPopulation.isEmpty() && entries.isEmpty()) {
        return
    }

    h3 { +storage.getPlural() }

    if (entries.isNotEmpty()) {
        val id = entries.first().id

        table {
            tr {
                th { +id.plural() }
                thMultiLines(listOf("Percentage", "of", "Total"))
                thMultiLines(listOf("Percentage", "of", id.type()))
                th { +"Number" }
            }
            entries
                .sortedByDescending { it.number }
                .forEach {
                    val percentageOfTotal = Factor.fromNumber(it.number / total.toFloat())

                    tr {
                        tdLink(call, state, it.id)
                        tdPercentage(percentageOfTotal)
                        tdPercentage(it.percentage)
                        tdSkipZero(it.number)
                    }
                }
        }
    }

    fieldElements(call, state, "Abstract Population", elementsWithAbstractPopulation)
}
