package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.*
import at.orchaldir.gm.core.model.economy.business.BusinessTemplateId
import at.orchaldir.gm.core.selector.util.RankingEntry
import at.orchaldir.gm.core.selector.util.calculateRankingIndex
import at.orchaldir.gm.core.selector.util.calculateIndexOfElementWithConcept
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor

fun State.canDeleteEconomyOf(business: BusinessTemplateId, result: DeleteResult) =
    canDeleteEconomyOf(result) { hasEconomy ->
        hasEconomy.economy().contains(business)
    }

fun State.canDeleteEconomyOf(
    result: DeleteResult,
    check: (HasEconomy) -> Boolean,
) = result
    .addElements(getEconomies(getDistrictStorage(), check))
    .addElements(getEconomies(getRealmStorage(), check))
    .addElements(getEconomies(getTownStorage(), check))

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
            RankingEntry(element.id(), number, percentage)
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
    val realms = getRealmStorage()
        .getAll()
        .sumOf { getNumber(it.economy) ?: 0 }
    val towns = getTownStorage()
        .getAll()
        .sumOf { getNumber(it.economy) ?: 0 }
    val total = districts + realms + towns

    return if (total > 0) {
        total
    } else {
        null
    }
}

fun <ID : Id<ID>, ELEMENT> State.calculateIndexOfElementWithEconomy(
    element: ELEMENT,
): Int? where
        ELEMENT : Element<ID>,
        ELEMENT : HasEconomy = calculateIndexOfElementWithConcept(element) {
    it.economy().getNumberOfBusinesses()
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> State.calculateEconomyIndex(
    storage: Storage<ID, ELEMENT>,
    id: ID,
    getEconomy: (Economy, ID) -> Int?,
) = calculateRankingIndex(storage, id) {
    calculateTotalNumberInEconomy { population ->
        getEconomy(population, it)
    }
}