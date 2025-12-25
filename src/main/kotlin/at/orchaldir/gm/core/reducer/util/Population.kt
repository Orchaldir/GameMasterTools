package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.population.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.ONE


fun validatePopulation(
    state: State,
    population: Population,
) = when (population) {
    is AbstractPopulation -> state.getRaceStorage().require(population.races)
    is PopulationDistribution -> {
        validateTotalPopulation(population.total)

        population.races.map.forEach { (race, percentage) ->
            state.getRaceStorage().require(race)
            require(percentage.isGreaterZero()) { "The population of ${race.print()} must be > 0%!" }
            require(percentage.isLessOrEqualOne()) { "The population of ${race.print()} must be <= 100%!" }
        }

        require(population.races.getDefinedPercentages() <= ONE) { "The total population of all Races must be <= 100%!" }
    }

    is TotalPopulation -> {
        validateTotalPopulation(population.total)
        state.getRaceStorage().require(population.races)
    }

    UndefinedPopulation -> doNothing()
}

fun validateTotalPopulation(
    totalPopulation: Int,
) = require(totalPopulation > 0) { "The total population must be greater than 0!" }