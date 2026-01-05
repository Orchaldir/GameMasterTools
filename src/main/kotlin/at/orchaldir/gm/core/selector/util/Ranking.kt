package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor

data class RankingEntry<ID : Id<ID>>(
    val id: ID,
    val number: Int,
    val percentage: Factor,
)

fun <ID : Id<ID>, ELEMENT : Element<ID>> State.calculateIndexOfElementWithConcept(
    element: ELEMENT,
    calculateValue: (ELEMENT) -> Int?,
): Int? {
    val value = calculateValue(element)

    return if (value == null) {
        null
    } else {
        getStorage<ID, ELEMENT>(element.id())
            .getAll()
            .map { calculateValue(it) }
            .sortedByDescending { it }
            .indexOfFirst { it == value } + 1
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> calculateIndexOfElementBasedOnConcept(
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