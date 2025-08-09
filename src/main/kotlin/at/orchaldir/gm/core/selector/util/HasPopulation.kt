package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ID : Id<ID>, ELEMENT> getElementsWithPopulation(
    storage: Storage<ID, ELEMENT>,
    race: RaceId,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation = storage
    .getAll()
    .mapNotNull {
        it.population().getPopulation(race)?.let { number -> Pair(it.id(), number) }
    }
