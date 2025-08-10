package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.core.model.util.population.PopulationPerRace
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.core.model.State

data class PopulationEntry<ID : Id<ID>>(
    val id: ID,
    val number: Int,
    val percentage: Factor,
)

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
