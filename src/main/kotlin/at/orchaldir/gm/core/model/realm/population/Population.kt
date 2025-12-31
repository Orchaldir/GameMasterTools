package at.orchaldir.gm.core.model.realm.population

import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.job.Income
import at.orchaldir.gm.core.model.economy.job.UndefinedIncome
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.model.util.PercentageDistribution
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.max

enum class PopulationType {
    Abstract,
    Numbers,
    Percentages,
    Total,
    Undefined,
}

@Serializable
sealed class Population {

    fun getType() = when (this) {
        is AbstractPopulation -> PopulationType.Abstract
        is PopulationWithNumbers -> PopulationType.Numbers
        is PopulationWithPercentages -> PopulationType.Percentages
        is TotalPopulation -> PopulationType.Total
        UndefinedPopulation -> PopulationType.Undefined
    }

    fun income() = when (this) {
        is AbstractPopulation -> income
        is PopulationWithNumbers -> income
        is PopulationWithPercentages -> income
        is TotalPopulation -> income
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

    fun getTotalPopulation() = when (this) {
        is TotalPopulation -> total
        is PopulationWithNumbers -> calculateTotal()
        is PopulationWithPercentages -> total
        is AbstractPopulation, UndefinedPopulation -> null
    }

    fun contains(culture: CultureId) = when (this) {
        is AbstractPopulation -> cultures.contains(culture)
        is PopulationWithNumbers -> cultures.map.containsKey(culture)
        is PopulationWithPercentages -> cultures.map.containsKey(culture)
        is TotalPopulation -> cultures.contains(culture)
        else -> false
    }

    fun contains(race: RaceId) = when (this) {
        is AbstractPopulation -> races.contains(race)
        is PopulationWithNumbers -> races.map.containsKey(race)
        is PopulationWithPercentages -> races.map.containsKey(race)
        is TotalPopulation -> races.contains(race)
        else -> false
    }

    fun cultures() = when (this) {
        is AbstractPopulation -> cultures
        is PopulationWithNumbers -> cultures.map.keys
        is PopulationWithPercentages -> cultures.map.keys
        is TotalPopulation -> cultures
        else -> emptySet()
    }

    fun races() = when (this) {
        is AbstractPopulation -> races
        is PopulationWithNumbers -> races.map.keys
        is PopulationWithPercentages -> races.map.keys
        is TotalPopulation -> races
        else -> emptySet()
    }

}

interface IPopulationWithSets {

    fun races(): Set<RaceId>
    fun cultures(): Set<CultureId>

}

@Serializable
@SerialName("Abstract")
data class AbstractPopulation(
    val density: Size = Size.Medium,
    val races: Set<RaceId> = emptySet(),
    val cultures: Set<CultureId> = emptySet(),
    val income: Income = UndefinedIncome,
) : Population(), IPopulationWithSets

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
    val total: Int,
    val races: PercentageDistribution<RaceId> = PercentageDistribution(),
    val cultures: PercentageDistribution<CultureId> = PercentageDistribution(),
    val income: Income = UndefinedIncome,
) : Population() {

    fun getNumber(race: RaceId) = races.getNumber(total, race)
    fun getNumber(culture: CultureId) = cultures.getNumber(total, culture)

}

@Serializable
@SerialName("Total")
data class TotalPopulation(
    val total: Int,
    val races: Set<RaceId> = emptySet(),
    val cultures: Set<CultureId> = emptySet(),
    val income: Income = UndefinedIncome,
) : Population(), IPopulationWithSets

@Serializable
@SerialName("Undefined")
data object UndefinedPopulation : Population()
