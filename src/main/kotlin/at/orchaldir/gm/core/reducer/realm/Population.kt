package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.population.*
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.ONE


fun validatePopulation(
    state: State,
    population: Population,
) = when (population) {
    is PopulationWithNumbers -> {
        validateNumberDistribution(state.getCultureStorage(), population.cultures)
        validateNumberDistribution(state.getRaceStorage(), population.races)

        population.income.validate(state)
    }

    is PopulationWithPercentages -> {
        validateTotalPopulation(state, population.total)

        validatePercentageDistribution(state.getCultureStorage(), population.cultures)
        validatePercentageDistribution(state.getRaceStorage(), population.races)

        population.income.validate(state)
    }

    is PopulationWithSets -> {
        validateTotalPopulation(state, population.total)

        state.getCultureStorage().require(population.cultures)
        state.getRaceStorage().require(population.races)

        population.income.validate(state)
    }

    UndefinedPopulation -> doNothing()
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> validateNumberDistribution(
    storage: Storage<ID, ELEMENT>,
    distribution: NumberDistribution<ID>,
) {
    distribution.map.forEach { (id, number) ->
        storage.require(id)
        require(number > 0) { "The population of ${id.print()} must be > 0!" }
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> validatePercentageDistribution(
    storage: Storage<ID, ELEMENT>,
    distribution: PercentageDistribution<ID>,
) {
    distribution.map.forEach { (id, percentage) ->
        storage.require(id)
        require(percentage.isGreaterZero()) { "The population of ${id.print()} must be > 0%!" }
        require(percentage.isLessOrEqualOne()) { "The population of ${id.print()} must be <= 100%!" }
    }

    require(distribution.getDefinedPercentages() <= ONE) { "The total population of all ${storage.getPlural()} must be <= 100%!" }
}

fun validateTotalPopulation(
    state: State,
    total: TotalPopulation,
) = when(total) {
    is TotalPopulationAsDensity -> doNothing()
    is TotalPopulationAsNumber -> validateTotalPopulation(total.number)
    is TotalPopulationAsSettlementSize -> state.getSettlementSizeStorage().remove(total.id)
}

fun validateTotalPopulation(
    totalPopulation: Int,
) = require(totalPopulation >= 0) { "The total population must be >= 0!" }