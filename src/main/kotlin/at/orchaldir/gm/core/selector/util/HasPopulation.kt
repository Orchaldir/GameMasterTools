package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.population.*
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor

data class PopulationEntry<ID : Id<ID>>(
    val id: ID,
    val number: Int,
    val percentage: Factor,
)

fun State.canDeletePopulationOf(culture: CultureId, result: DeleteResult) =
    canDeletePopulationOf(result) { hasPopulation ->
        hasPopulation.population().contains(culture)
    }

fun State.canDeletePopulationOf(race: RaceId, result: DeleteResult) = canDeletePopulationOf(result) { hasPopulation ->
    hasPopulation.population().contains(race)
}

fun State.canDeletePopulationOf(
    result: DeleteResult,
    check: (HasPopulation) -> Boolean,
) = result
    .addElements(getPopulations(getDistrictStorage(), check))
    .addElements(getPopulations(getRealmStorage(), check))
    .addElements(getPopulations(getTownStorage(), check))

fun <ID : Id<ID>, ELEMENT> getPopulations(
    storage: Storage<ID, ELEMENT>,
    check: (HasPopulation) -> Boolean,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation = storage.getAll()
    .filter { check(it) }

fun <ID : Id<ID>, ELEMENT> getPopulationEntries(
    storage: Storage<ID, ELEMENT>,
    getPercentage: (HasPopulation) -> Pair<Int, Factor>?,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation = storage
    .getAll()
    .mapNotNull { element ->
        getPercentage(element)?.let { (number, percentage) ->
            PopulationEntry(element.id(), number, percentage)
        }
    }

fun <ID : Id<ID>, ELEMENT> getAbstractPopulations(
    storage: Storage<ID, ELEMENT>,
    contains: (IPopulationWithSets) -> Boolean,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation = storage
    .getAll()
    .filter { element ->
        when (val population = element.population()) {
            is AbstractPopulation -> contains(population)
            is PopulationDistribution -> false
            is TotalPopulation -> contains(population)
            UndefinedPopulation -> false
        }
    }

fun State.calculateTotalPopulation(getPopulation: (Population) -> Int?): Int? {
    val towns = getTownStorage()
        .getAll()
        .sumOf { getPopulation(it.population) ?: 0 }
    val realms = getRealmStorage()
        .getAll()
        .sumOf { getPopulation(it.population) ?: 0 }
    val total = towns + realms

    return if (total > 0) {
        total
    } else {
        null
    }
}

fun <ID : Id<ID>, ELEMENT> State.calculatePopulationIndex(
    element: ELEMENT,
): Int? where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation {
    return if (element.population().getTotalPopulation() == null) {
        null
    } else {
        getStorage<ID, ELEMENT>(element.id())
            .getAll()
            .sortedByDescending { it.population().getTotalPopulation() }
            .indexOfFirst { it.id() == element.id() } + 1
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> State.calculatePopulationIndex(
    storage: Storage<ID, ELEMENT>,
    id: ID,
    getPopulation: (Population, ID) -> Int?,
): Int? {
    val mapNotNull = storage
        .getAll()
        .mapNotNull {
            val total = calculateTotalPopulation { population ->
                getPopulation(population, it.id())
            }

            if (total == null || total == 0) {
                return@mapNotNull null
            }

            Pair(it.id(), total)
        }
    return mapNotNull
        .sortedByDescending { it.second }
        .indexOfFirst { it.first == id }
        .let {
            if (it >= 0) {
                it + 1
            } else {
                null
            }
        }
}