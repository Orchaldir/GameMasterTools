package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.population.HasPopulation
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor

data class DistributionEntry<ID : Id<ID>>(
    val id: ID,
    val number: Int,
    val percentage: Factor,
)

fun <ID : Id<ID>, ELEMENT> State.calculateRankingIndex(
    element: ELEMENT,
    calculateValue: (ELEMENT) -> Int?
): Int? where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation {
    return if (calculateValue(element) == null) {
        null
    } else {
        getStorage<ID, ELEMENT>(element.id())
            .getAll()
            .sortedByDescending { calculateValue(it) }
            .indexOfFirst { it.id() == element.id() } + 1
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> calculateRankingIndex(
    storage: Storage<ID, ELEMENT>,
    id: ID,
    calculateTotal: (ID) -> Int?,
): Int? {
    val mapNotNull = storage
        .getAll()
        .mapNotNull {
            val total = calculateTotal(it.id())

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