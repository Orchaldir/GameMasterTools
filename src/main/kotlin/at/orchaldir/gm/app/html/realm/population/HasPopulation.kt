package at.orchaldir.gm.app.html.realm.population

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.realm.population.HasPopulation
import at.orchaldir.gm.core.model.realm.population.IPopulationWithSets
import at.orchaldir.gm.core.model.realm.population.Population
import at.orchaldir.gm.core.model.realm.population.PopulationWithNumbers
import at.orchaldir.gm.core.model.realm.population.PopulationWithPercentages
import at.orchaldir.gm.core.selector.realm.calculatePopulationIndex
import at.orchaldir.gm.core.selector.realm.calculateTotalPopulation
import at.orchaldir.gm.core.selector.realm.getAbstractPopulations
import at.orchaldir.gm.core.selector.realm.getPopulationEntries
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
    {
        when (val population = it.population()) {
            is PopulationWithNumbers -> population.cultures.getData(culture)
            is PopulationWithPercentages -> population.cultures.getData(culture, population.total)
            else -> null
        }
    },
    { hasPopulation, id -> hasPopulation.getPopulation(id) },
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
    {
        when (val population = it.population()) {
            is PopulationWithNumbers -> population.races.getData(race)
            is PopulationWithPercentages -> population.races.getData(race, population.total)
            else -> null
        }
    },
    { hasPopulation, id -> hasPopulation.getPopulation(id) },
)

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.showPopulationOfElement(
    call: ApplicationCall,
    state: State,
    storage: Storage<ID, ELEMENT>,
    id: ID,
    contains: (IPopulationWithSets) -> Boolean,
    getPercentage: (HasPopulation) -> Pair<Int, Factor>?,
    getPopulation: (Population, ID) -> Int?,
) {
    h2 { +"Population" }

    val total = state.calculateTotalPopulation { population ->
        getPopulation(population, id)
    }
    val totalOrZero = total ?: 0
    optionalField("Total", total)
    optionalField("Index", state.calculatePopulationIndex(storage, id, getPopulation))

    showPopulationOfElement(call, state, getPercentage, state.getDistrictStorage(), totalOrZero, contains)
    showPopulationOfElement(call, state, getPercentage, state.getRealmStorage(), totalOrZero, contains)
    showPopulationOfElement(call, state, getPercentage, state.getTownStorage(), totalOrZero, contains)
}

private fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showPopulationOfElement(
    call: ApplicationCall,
    state: State,
    getPercentage: (HasPopulation) -> Pair<Int, Factor>?,
    storage: Storage<ID, ELEMENT>,
    total: Int,
    contains: (IPopulationWithSets) -> Boolean,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation {
    val elementsWithAbstractPopulation = getAbstractPopulations(storage, contains)
    val entries = getPopulationEntries(storage, getPercentage)

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

    fieldElements(call, state, "Abstract Population In", elementsWithAbstractPopulation)
}
