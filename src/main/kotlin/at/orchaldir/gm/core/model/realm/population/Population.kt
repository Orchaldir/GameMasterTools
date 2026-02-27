package at.orchaldir.gm.core.model.realm.population

import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.job.Income
import at.orchaldir.gm.core.model.economy.job.UndefinedIncome
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.realm.SettlementSizeId
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.model.util.PercentageDistribution
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.max

enum class PopulationType {
    Numbers,
    Percentages,
    Sets,
    Undefined,
}

@Serializable
sealed class Population {

    fun getType() = when (this) {
        is PopulationWithNumbers -> PopulationType.Numbers
        is PopulationWithPercentages -> PopulationType.Percentages
        is PopulationWithSets -> PopulationType.Sets
        UndefinedPopulation -> PopulationType.Undefined
    }

    fun income() = when (this) {
        is PopulationWithNumbers -> income
        is PopulationWithPercentages -> income
        is PopulationWithSets -> income
        UndefinedPopulation -> null
    }

    fun getPopulation(culture: CultureId) = when (this) {
        is PopulationWithNumbers -> cultures.getNumber(culture)
        is PopulationWithPercentages -> getNumber(culture)
        else -> null
    }

    fun getPopulation(race: RaceId) = when (this) {
        is PopulationWithNumbers -> races.getNumber(race)
        is PopulationWithPercentages -> getNumber(race)
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
        is UndefinedPopulation -> null
    }

    fun contains(culture: CultureId) = when (this) {
        is PopulationWithNumbers -> cultures.map.containsKey(culture)
        is PopulationWithPercentages -> cultures.map.containsKey(culture)
        is PopulationWithSets -> cultures.contains(culture)
        else -> false
    }

    fun contains(race: RaceId) = when (this) {
        is PopulationWithNumbers -> races.map.containsKey(race)
        is PopulationWithPercentages -> races.map.containsKey(race)
        is PopulationWithSets -> races.contains(race)
        else -> false
    }

    fun cultures() = when (this) {
        is PopulationWithNumbers -> cultures.map.keys
        is PopulationWithPercentages -> cultures.map.keys
        is PopulationWithSets -> cultures
        else -> emptySet()
    }

    fun races() = when (this) {
        is PopulationWithNumbers -> races.map.keys
        is PopulationWithPercentages -> races.map.keys
        is PopulationWithSets -> races
        else -> emptySet()
    }

    fun isSize(size: SettlementSizeId) = when (this) {
        is PopulationWithPercentages -> total.isSize(size)
        is PopulationWithSets -> total.isSize(size)
        else -> false
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
@SerialName("Undefined")
data object UndefinedPopulation : Population()
