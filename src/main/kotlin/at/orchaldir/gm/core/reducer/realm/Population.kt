package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.population.*
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.ZERO
import kotlin.text.compareTo


fun validatePopulation(
    state: State,
    allowedTotalPopulationTypes: Collection<TotalPopulationType>,
    population: Population,
) = when (population) {
    is PopulationWithNumbers -> {
        validateNumberDistribution(state.getCultureStorage(), population.cultures)
        validateNumberDistribution(state.getRaceStorage(), population.races)

        population.income.validate(state)
    }

    is PopulationWithPercentages -> {
        validateTotalPopulation(state, allowedTotalPopulationTypes, population.total)

        validatePercentageDistribution(state.getCultureStorage(), population.cultures)
        validatePercentageDistribution(state.getRaceStorage(), population.races)

        population.income.validate(state)
    }

    is PopulationWithSets -> {
        validateTotalPopulation(state, allowedTotalPopulationTypes, population.total)

        state.getCultureStorage().require(population.cultures)
        state.getRaceStorage().require(population.races)

        population.income.validate(state)
    }

    is PopulationUnitsWithNumbers ->  {
        validatePopulationUnits(state, population.units) { number, population ->
            require(population > 0) { "$number.unit's population must be > 0!" }
        }

        require(population.undefined >= 0) { "Undefined population must not be negative!" }
    }

    is PopulationUnitsWithPercentages -> {
        validateTotalPopulation(state, allowedTotalPopulationTypes, population.total)

        validatePopulationUnits(state, population.units) { number, population ->
            require(population > ZERO) { "$number.unit's population must be > 0%!" }
        }

        require(population.units.map { it.value }.reduce { acc, factor -> acc + factor } <= FULL) {
            "The total percentage of all units is > 100%!"
        }
    }

    UndefinedPopulation -> doNothing()
}

private fun <T> validatePopulationUnits(
    state: State,
    units: List<PopulationUnit<T>>,
    validateValue: (Int, T) -> Unit,
) {
    units.withIndex().forEach { (index, unit) ->
        val number = index + 1

        state.getCultureStorage().require(unit.culture) {
            "$number.unit requires unknown ${unit.culture.print()}!"
        }
        state.getRaceStorage().require(unit.race) {
            "$number.unit requires unknown ${unit.race.print()}!"
        }
        unit.income.validate(state)

        validateValue(number, unit.value)
    }
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
    allowedTotalPopulationTypes: Collection<TotalPopulationType>,
    total: TotalPopulation,
) {
    val type = total.getType()
    require(allowedTotalPopulationTypes.contains(type)) {
        "Total Population Type $type is not supported!"
    }

    when (total) {
        is TotalPopulationAsDensity -> doNothing()
        is TotalPopulationAsNumber -> validateTotalPopulation(total.number)
        is TotalPopulationAsSettlementSize -> state.getSettlementSizeStorage().require(total.id)
    }
}

fun validateTotalPopulation(
    totalPopulation: Int,
) = require(totalPopulation >= 0) { "The total population must be >= 0!" }