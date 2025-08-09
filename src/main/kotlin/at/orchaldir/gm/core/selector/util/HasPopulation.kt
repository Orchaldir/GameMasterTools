package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.core.model.util.population.PopulationPerRace
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor

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
