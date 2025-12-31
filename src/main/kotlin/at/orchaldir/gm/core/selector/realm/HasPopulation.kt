package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.realm.population.AbstractPopulation
import at.orchaldir.gm.core.model.realm.population.HasPopulation
import at.orchaldir.gm.core.model.realm.population.IPopulationWithSets
import at.orchaldir.gm.core.model.realm.population.Population
import at.orchaldir.gm.core.model.realm.population.PopulationWithNumbers
import at.orchaldir.gm.core.model.realm.population.PopulationWithPercentages
import at.orchaldir.gm.core.model.realm.population.TotalPopulation
import at.orchaldir.gm.core.model.realm.population.UndefinedPopulation
import at.orchaldir.gm.core.selector.util.calculateRankingIndex
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

fun <ID : Id<ID>, ELEMENT> getPopulationsWith(
    storage: Storage<ID, ELEMENT>,
    standard: StandardOfLivingId,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation = getPopulations(storage) {
    it.population().income()?.hasStandard(standard) ?: false
}

fun <ID : Id<ID>, ELEMENT> getPopulations(
    storage: Storage<ID, ELEMENT>,
    check: (HasPopulation) -> Boolean,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation = storage
    .getAll()
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
            is PopulationWithNumbers -> false
            is PopulationWithPercentages -> false
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
        ELEMENT : HasPopulation = calculateRankingIndex(element) {
    it.population().getTotalPopulation()
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> State.calculatePopulationIndex(
    storage: Storage<ID, ELEMENT>,
    id: ID,
    getPopulation: (Population, ID) -> Int?,
): Int? = calculateRankingIndex(storage, id) {
    calculateTotalPopulation { population ->
        getPopulation(population, it)
    }
}