package at.orchaldir.gm.core.model.realm.population

import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.job.Income
import at.orchaldir.gm.core.model.economy.job.UndefinedIncome
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.realm.SettlementSizeId
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.ZERO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.max

const val MAX_POPULATION = 100_000_000

enum class PopulationType {
    Numbers,
    Percentages,
    Sets,
    UnitsWithNumbers,
    UnitsWithPercentages,
    Undefined,
}

@Serializable
sealed class Population {

    fun getType() = when (this) {
        is PopulationWithNumbers -> PopulationType.Numbers
        is PopulationWithPercentages -> PopulationType.Percentages
        is PopulationWithSets -> PopulationType.Sets
        is PopulationUnitsWithNumbers -> PopulationType.UnitsWithNumbers
        is PopulationUnitsWithPercentages -> PopulationType.UnitsWithPercentages
        UndefinedPopulation -> PopulationType.Undefined
    }

    fun income() = when (this) {
        is PopulationWithNumbers -> income
        is PopulationWithPercentages -> income
        is PopulationWithSets -> income
        is PopulationUnitsWithNumbers -> null
        is PopulationUnitsWithPercentages -> null
        UndefinedPopulation -> null
    }

    fun getPopulation(culture: CultureId) = when (this) {
        is PopulationWithNumbers -> cultures.getNumber(culture)
        is PopulationWithPercentages -> getNumber(culture)
        is PopulationUnitsWithNumbers -> getNumber(culture)
        is PopulationUnitsWithPercentages -> getNumber(culture)
        else -> null
    }

    fun getPopulation(race: RaceId) = when (this) {
        is PopulationWithNumbers -> races.getNumber(race)
        is PopulationWithPercentages -> getNumber(race)
        is PopulationUnitsWithNumbers -> getNumber(race)
        is PopulationUnitsWithPercentages -> getNumber(race)
        else -> null
    }

    fun getPopulation(standard: StandardOfLivingId): Int? = when (this) {
        is PopulationWithNumbers -> getPopulation(income, standard, calculateTotal())
        is PopulationWithPercentages -> getPopulation(income, standard, total)
        is PopulationWithSets -> getPopulation(income, standard, total)
        else -> null
    }

    private fun getPopulation(income: Income, standard: StandardOfLivingId, total: TotalPopulation) =
        total.getTotal()?.let { getPopulation(income, standard, it) }

    private fun getPopulation(income: Income, standard: StandardOfLivingId, total: Int) =
        if (income.hasStandard(standard)) {
            total
        } else {
            null
        }

    fun getTotalPopulation() = when (this) {
        is PopulationWithSets -> total.getTotal()
        is PopulationWithNumbers -> calculateTotal()
        is PopulationWithPercentages -> total.getTotal()
        is PopulationUnitsWithNumbers -> getTotal()
        is PopulationUnitsWithPercentages -> total.getTotal()
        is UndefinedPopulation -> null
    }

    fun contains(culture: CultureId) = when (this) {
        is PopulationWithNumbers -> cultures.map.containsKey(culture)
        is PopulationWithPercentages -> cultures.map.containsKey(culture)
        is PopulationWithSets -> cultures.contains(culture)
        is PopulationUnitsWithNumbers -> units.any { it.culture == culture }
        is PopulationUnitsWithPercentages -> units.any { it.culture == culture }
        is UndefinedPopulation -> false
    }

    fun contains(race: RaceId) = when (this) {
        is PopulationWithNumbers -> races.map.containsKey(race)
        is PopulationWithPercentages -> races.map.containsKey(race)
        is PopulationWithSets -> races.contains(race)
        is PopulationUnitsWithNumbers -> units.any { it.race == race }
        is PopulationUnitsWithPercentages -> units.any { it.race == race }
        is UndefinedPopulation -> false
    }

    fun cultures() = when (this) {
        is PopulationWithNumbers -> cultures.map.keys
        is PopulationWithPercentages -> cultures.map.keys
        is PopulationWithSets -> cultures
        is PopulationUnitsWithNumbers -> units.map { it.culture }.toSet()
        is PopulationUnitsWithPercentages -> units.map { it.culture }.toSet()
        is UndefinedPopulation -> emptySet()
    }

    fun races() = when (this) {
        is PopulationWithNumbers -> races.map.keys
        is PopulationWithPercentages -> races.map.keys
        is PopulationWithSets -> races
        is PopulationUnitsWithNumbers -> units.map { it.race }.toSet()
        is PopulationUnitsWithPercentages -> units.map { it.race }.toSet()
        is UndefinedPopulation -> emptySet()
    }

    fun isSize(size: SettlementSizeId) = when (this) {
        is PopulationWithNumbers, is PopulationUnitsWithNumbers, is PopulationUnitsWithPercentages, is UndefinedPopulation -> false
        is PopulationWithPercentages -> total.isSize(size)
        is PopulationWithSets -> total.isSize(size)
    }

}

interface IPopulationWithSets {

    fun races(): Set<RaceId>
    fun cultures(): Set<CultureId>

}

@Serializable
@SerialName("Numbers")
data class PopulationWithNumbers(
    val races: NumberDistribution<RaceId> = NumberDistribution(),
    val cultures: NumberDistribution<CultureId> = NumberDistribution(),
    val income: Income = UndefinedIncome,
) : Population() {

    fun calculateTotal() = max(races.calculateTotal(), cultures.calculateTotal())

}

@Serializable
@SerialName("Percentages")
data class PopulationWithPercentages(
    val total: TotalPopulation,
    val races: PercentageDistribution<RaceId> = PercentageDistribution(),
    val cultures: PercentageDistribution<CultureId> = PercentageDistribution(),
    val income: Income = UndefinedIncome,
) : Population() {

    fun getNumber(race: RaceId) = races.getNumber(total.getTotal(), race)
    fun getNumber(culture: CultureId) = cultures.getNumber(total.getTotal(), culture)

}

@Serializable
@SerialName("Set")
data class PopulationWithSets(
    val total: TotalPopulation = TotalPopulationAsDensity(),
    val races: Set<RaceId> = emptySet(),
    val cultures: Set<CultureId> = emptySet(),
    val income: Income = UndefinedIncome,
) : Population(), IPopulationWithSets

@Serializable
@SerialName("UnitsWithNumbers")
data class PopulationUnitsWithNumbers(
    val units: List<PopulationUnit<Int>>,
    val undefined: Int = 0,
) : Population() {

    constructor(unit: PopulationUnit<Int>, undefined: Int = 0): this(listOf(unit), undefined)

    fun getData(culture: CultureId) = getData(getNumber(culture))

    fun getData(race: RaceId) = getData(getNumber(race))

    private fun getData(number: Int): Pair<Int, Factor>? {
        if (number == 0) {
            return null
        }

        val factor = Factor.divideTwoInts(number, getTotal())

        return Pair(number, factor)
    }

    fun getNumber(race: RaceId) = units
        .filter { it.race == race }
        .sumOf { it.value }

    fun getNumber(culture: CultureId) = units
        .filter { it.culture == culture }
        .sumOf { it.value }

    fun getTotal() = getDefinedNumber()  + undefined

    fun getDefinedNumber() = units
        .sumOf { it.value }

    fun getDefinedPercentages(): Factor {
        val defined = getDefinedNumber()

        return Factor.divideTwoInts(defined, defined + undefined)
    }

    fun getUndefinedPercentages() = Factor.divideTwoInts(undefined, getTotal())
}

@Serializable
@SerialName("UnitsWithPercentages")
data class PopulationUnitsWithPercentages(
    val total: TotalPopulation,
    val units: List<PopulationUnit<Factor>>,
) : Population() {

    fun getData(culture: CultureId) = getData(getFactorOrNull(culture))

    fun getData(race: RaceId) = getData(getFactorOrNull(race))

    private fun getData(factorOrNull: Factor?): Pair<Int, Factor>? {
        val factor = factorOrNull ?: return null
        val number = factor.apply(total.getTotalOrZero())

        return Pair(number, factor)
    }

    fun getFactor(race: RaceId) = getFactorOrNull(race) ?: ZERO

    fun getFactor(culture: CultureId) = getFactorOrNull(culture) ?: ZERO

    fun getFactorOrNull(race: RaceId) = units
        .filter { it.race == race }
        .map { it.value }
        .reduceOrNull { acc, factor -> acc + factor }

    fun getFactorOrNull(culture: CultureId) = units
        .filter { it.culture == culture }
        .map { it.value }
        .reduceOrNull { acc, factor -> acc + factor }

    fun getNumber(race: RaceId) = getFactor(race)
        .apply(total.getTotalOrZero())

    fun getNumber(culture: CultureId) = getFactor(culture)
        .apply(total.getTotalOrZero())

    fun getDefinedPercentages() = units
        .map { it.value }
        .reduceOrNull { sum, percentage -> sum + percentage } ?: ZERO

    fun getUndefinedPercentages() = ONE - getDefinedPercentages()
}

@Serializable
@SerialName("Undefined")
data object UndefinedPopulation : Population()
