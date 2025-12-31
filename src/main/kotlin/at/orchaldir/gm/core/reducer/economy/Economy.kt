package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.CommonBusinesses
import at.orchaldir.gm.core.model.economy.Economy
import at.orchaldir.gm.core.model.economy.EconomyWithNumbers
import at.orchaldir.gm.core.model.economy.EconomyWithPercentages
import at.orchaldir.gm.core.model.economy.UndefinedEconomy
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.ONE


fun validateEconomy(
    state: State,
    economy: Economy,
) = when (economy) {
    is CommonBusinesses -> state.getBusinessTemplateStorage().require(economy.businesses)
    is EconomyWithNumbers -> validateNumberDistribution(state.getBusinessTemplateStorage(), economy.businesses)
    is EconomyWithPercentages -> {
        validateTotalBusinessNumber(economy.total)

        validatePercentageDistribution(state.getBusinessTemplateStorage(), economy.businesses)
    }

    UndefinedEconomy -> doNothing()
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> validateNumberDistribution(
    storage: Storage<ID, ELEMENT>,
    distribution: NumberDistribution<ID>,
) {
    distribution.map.forEach { (id, number) ->
        storage.require(id)
        require(number > 0) { "The number of ${id.print()} must be > 0!" }
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> validatePercentageDistribution(
    storage: Storage<ID, ELEMENT>,
    distribution: PercentageDistribution<ID>,
) {
    distribution.map.forEach { (id, percentage) ->
        storage.require(id)
        require(percentage.isGreaterZero()) { "The percentage of ${id.print()} must be > 0%!" }
        require(percentage.isLessOrEqualOne()) { "The percentage of ${id.print()} must be <= 100%!" }
    }

    require(distribution.getDefinedPercentages() <= ONE) { "The total percentage of all ${storage.getPlural()} must be <= 100%!" }
}

fun validateTotalBusinessNumber(
    totalEconomy: Int,
) = require(totalEconomy >= 0) { "The total business number must be >= 0!" }