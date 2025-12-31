package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.CommonBusinesses
import at.orchaldir.gm.core.model.economy.Economy
import at.orchaldir.gm.core.model.economy.EconomyWithNumbers
import at.orchaldir.gm.core.model.economy.EconomyWithPercentages
import at.orchaldir.gm.core.model.economy.HasEconomy
import at.orchaldir.gm.core.model.economy.IAbstractEconomy
import at.orchaldir.gm.core.model.economy.UndefinedEconomy
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor

data class EconomyEntry<ID : Id<ID>>(
    val id: ID,
    val number: Int,
    val percentage: Factor,
)

fun <ID : Id<ID>, ELEMENT> getEconomies(
    storage: Storage<ID, ELEMENT>,
    check: (HasEconomy) -> Boolean,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasEconomy = storage
    .getAll()
    .filter { check(it) }

fun <ID : Id<ID>, ELEMENT> getEconomyEntries(
    storage: Storage<ID, ELEMENT>,
    getPercentage: (HasEconomy) -> Pair<Int, Factor>?,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasEconomy = storage
    .getAll()
    .mapNotNull { element ->
        getPercentage(element)?.let { (number, percentage) ->
            EconomyEntry(element.id(), number, percentage)
        }
    }

fun <ID : Id<ID>, ELEMENT> getAbstractEconomies(
    storage: Storage<ID, ELEMENT>,
    contains: (IAbstractEconomy) -> Boolean,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasEconomy = storage
    .getAll()
    .filter { element ->
        when (val economy = element.economy()) {
            is CommonBusinesses -> contains(economy)
            is EconomyWithNumbers -> false
            is EconomyWithPercentages -> false
            UndefinedEconomy -> false
        }
    }

fun State.calculateTotalNumberInEconomy(getNumber: (Economy) -> Int?): Int? {
    val districts = getDistrictStorage()
        .getAll()
        .sumOf { getNumber(it.economy) ?: 0 }
    val total = districts

    return if (total > 0) {
        total
    } else {
        null
    }
}

fun <ID : Id<ID>, ELEMENT> State.calculateEconomyIndex(
    element: ELEMENT,
): Int? where
        ELEMENT : Element<ID>,
        ELEMENT : HasEconomy {
    return if (element.economy().getNumberOfBusinesses() == null) {
        null
    } else {
        getStorage<ID, ELEMENT>(element.id())
            .getAll()
            .sortedByDescending { it.economy().getNumberOfBusinesses() }
            .indexOfFirst { it.id() == element.id() } + 1
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> State.calculateEconomyIndex(
    storage: Storage<ID, ELEMENT>,
    id: ID,
    getEconomy: (Economy, ID) -> Int?,
): Int? {
    val mapNotNull = storage
        .getAll()
        .mapNotNull {
            val total = calculateTotalNumberInEconomy { population ->
                getEconomy(population, it.id())
            }

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