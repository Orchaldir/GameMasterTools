package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.population.*
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.ONE


fun validatePopulation(
    state: State,
    population: Population,
) = when (population) {
    is AbstractPopulation -> state.getRaceStorage().require(population.races)
    is PopulationDistribution -> {
        validateTotalPopulation(population.total)

        validateElementDistribution(state.getCultureStorage(), population.cultures)
        validateElementDistribution(state.getRaceStorage(), population.races)
    }

    is TotalPopulation -> {
        validateTotalPopulation(population.total)
        state.getRaceStorage().require(population.races)
    }

    UndefinedPopulation -> doNothing()
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> validateElementDistribution(
    storage: Storage<ID, ELEMENT>,
    distribution: ElementDistribution<ID>,
) {
    distribution.map.forEach { (race, percentage) ->
        storage.require(race)
        require(percentage.isGreaterZero()) { "The population of ${race.print()} must be > 0%!" }
        require(percentage.isLessOrEqualOne()) { "The population of ${race.print()} must be <= 100%!" }
    }

    require(distribution.getDefinedPercentages() <= ONE) { "The total population of all ${storage.getPlural()} must be <= 100%!" }
}

fun validateTotalPopulation(
    totalPopulation: Int,
) = require(totalPopulation > 0) { "The total population must be greater than 0!" }