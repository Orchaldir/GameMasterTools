package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.population.Population
import at.orchaldir.gm.core.model.util.population.PopulationPerRace
import at.orchaldir.gm.core.model.util.population.TotalPopulation
import at.orchaldir.gm.core.model.util.population.UndefinedPopulation
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.ONE


fun State.validatePopulation(
    population: Population,
) = when (population) {
    is TotalPopulation -> validateTotalPopulation(population.total)
    is PopulationPerRace -> {
        validateTotalPopulation(population.total)

        population.racePercentages.forEach { (race, percentage) ->
            getRaceStorage().require(race)
            require(percentage.isGreaterZero()) { "The population of ${race.print()} must be > 0%!" }
            require(percentage.isLessOrEqualOne()) { "The population of ${race.print()} must be <= 100%!" }
        }

        require(population.getDefinedPercentage() <= ONE) { "The population of all Races must be <= 100%!" }
    }

    UndefinedPopulation -> doNothing()
}

fun validateTotalPopulation(
    totalPopulation: Int,
) = require(totalPopulation > 0) { "The total population must be greater than 0!" }