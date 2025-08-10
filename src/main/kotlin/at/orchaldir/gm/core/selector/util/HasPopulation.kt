package at.orchaldir.gm.core.selector.util

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

fun State.hasNoPopulation(race: RaceId) = !hasAnyPopulation(race)
fun State.hasAnyPopulation(race: RaceId) = hasAnyPopulation(getRealmStorage(), race)
        || hasAnyPopulation(getTownStorage(), race)

fun <ID : Id<ID>, ELEMENT> hasAnyPopulation(
    storage: Storage<ID, ELEMENT>,
    race: RaceId,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation = storage.getAll()
    .any { it.population().contains(race) }

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

fun State.getTotalPopulation(race: RaceId): Int {
    val towns = getTownStorage()
        .getAll()
        .filter { it.owner.current == null }
        .sumOf { it.population.getPopulation(race) ?: 0 }

    val realms = getRealmStorage()
        .getAll()
        .filter { it.owner.current == null }
        .sumOf { it.population.getPopulation(race) ?: 0 }

    return towns + realms
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
    .map { other ->
        Pair(race, getTotalPopulation(other.id))
    }
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