package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.SettlementSizeId
import at.orchaldir.gm.core.selector.util.sortSettlementSizes

fun State.canDeleteSettlementSize(size: SettlementSizeId) = DeleteResult(size)
    .addElements(getSettlements(size))

fun State.getMinPopulation(id: SettlementSizeId): Int {
    var min = 0

    sortSettlementSizes()
        .reversed()
        .forEach {
            if (it.id == id) {
                return min
            }

            min = it.maxPopulation
        }

    error("Didn't find ${id.print()}!")
}

fun State.getSettlementSize(population: Int?): SettlementSizeId? {
    val threshold = population ?: return null

    sortSettlementSizes()
        .reversed()
        .forEach {
            if (threshold <= it.maxPopulation) {
                return it.id
            }
        }

    return null
}