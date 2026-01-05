package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Factor

data class RankingEntry<ID : Id<ID>>(
    val id: ID,
    val number: Int,
    val percentage: Factor,
)

fun <ID : Id<ID>, ELEMENT : Element<ID>> State.calculateRankOfElement(
    element: ELEMENT,
    calculateValue: (ELEMENT) -> Int?,
): Int? {
    val value = calculateValue(element)

    return if (value == null || value == 0) {
        null
    } else {
        getStorage<ID, ELEMENT>(element.id())
            .getAll()
            .map { calculateValue(it) }
            .sortedByDescending { it }
            .indexOfFirst { it == value } + 1
    }
}
