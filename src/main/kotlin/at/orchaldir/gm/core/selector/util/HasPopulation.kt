package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.core.model.util.population.PopulationPerRace
import at.orchaldir.gm.core.model.util.population.UndefinedPopulation
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor

data class PopulationEntry<ID : Id<ID>>(
    val id: ID,
    val number: Int,
    val percentage: Factor,
)

fun State.canDeletePopulationOf(race: RaceId, result: DeleteResult) = result
    .addElements(getPopulations(getDistrictStorage(), race))
    .addElements(getPopulations(getRealmStorage(), race))
    .addElements(getPopulations(getTownStorage(), race))

fun <ID : Id<ID>, ELEMENT> getPopulations(
    storage: Storage<ID, ELEMENT>,
    race: RaceId,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation = storage.getAll()
    .filter { it.population().contains(race) }

fun <ID : Id<ID>, ELEMENT> getPopulationEntries(
    storage: Storage<ID, ELEMENT>,
    race: RaceId,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation = storage
    .getAll()
    .mapNotNull { element ->
        when (val population = element.population()) {
            is PopulationPerRace -> {
                population.racePercentages[race]?.let { percentage ->
                    PopulationEntry(element.id(), percentage.apply(population.total), percentage)
                }
            }

            else -> null
        }
    }

fun State.getTotalPopulation(race: RaceId): Int? {
    val towns = getTownStorage()
        .getAll()
        .filter { it.owner.current == null }
        .sumOf { it.population.getPopulation(race) ?: 0 }
    val realms = getRealmStorage()
        .getAll()
        .filter { it.owner.current == null }
        .sumOf { it.population.getPopulation(race) ?: 0 }
    val total = towns + realms

    return if (total > 0) {
        total
    } else {
        null
    }
}

fun <ID : Id<ID>, ELEMENT> State.getPopulationIndex(
    element: ELEMENT,
): Int? where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation {
    return if (element.population() is UndefinedPopulation) {
        null
    } else {
        getStorage<ID, ELEMENT>(element.id())
            .getAll()
            .sortedByDescending { it.population().getTotalPopulation() }
            .indexOfFirst { it.id() == element.id() } + 1
    }
}

fun State.getPopulationIndex(
    race: RaceId,
) = getRaceStorage()
    .getAll()
    .mapNotNull { other ->
        getTotalPopulation(other.id)
    }
    .map { Pair(race, it) }
    .filter { it.second > 0 }
    .sortedByDescending { getTotalPopulation(race) }
    .indexOfFirst { it.first == race }
    .let {
        if (it >= 0) {
            it + 1
        } else {
            null
        }
    }