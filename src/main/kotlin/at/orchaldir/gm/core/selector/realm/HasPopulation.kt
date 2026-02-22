package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.realm.population.*
import at.orchaldir.gm.core.selector.util.RankingEntry
import at.orchaldir.gm.core.selector.util.calculateRankOfElement
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor

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
    .addElements(getPopulations(getSettlementStorage(), check))

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
            RankingEntry(element.id(), number, percentage)
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
            is PopulationWithSet -> contains(population)
            UndefinedPopulation -> false
        }
    }

fun State.calculateTotalPopulation(getPopulation: (Population) -> Int?): Int? {
    val districts = getDistrictStorage()
        .getAll()
        .sumOf { getPopulation(it.population) ?: 0 }
    val realms = getRealmStorage()
        .getAll()
        .sumOf { getPopulation(it.population) ?: 0 }
    val settlements = getSettlementStorage()
        .getAll()
        .sumOf { getPopulation(it.population) ?: 0 }
    val total = districts + realms + settlements

    return if (total > 0) {
        total
    } else {
        null
    }
}

fun <ID : Id<ID>, ELEMENT> State.calculateRankOfElementWithPopulation(
    element: ELEMENT,
): Int? where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation = calculateRankOfElement(element) {
    it.population().getTotalPopulation()
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> State.calculateRankBasedOnPopulation(
    element: ELEMENT,
    getPopulation: (Population, ELEMENT) -> Int?,
) = calculateRankOfElement(element) { other ->
    calculateTotalPopulation { population ->
        getPopulation(population, other)
    }
}