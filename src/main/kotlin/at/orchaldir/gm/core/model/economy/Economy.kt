package at.orchaldir.gm.core.model.economy

import at.orchaldir.gm.core.model.economy.business.BusinessTemplateId
import at.orchaldir.gm.core.model.util.NumberDistribution
import at.orchaldir.gm.core.model.util.PercentageDistribution
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EconomyType {
    Businesses,
    Numbers,
    Percentages,
    Undefined,
}

@Serializable
sealed class Economy {

    fun getType() = when (this) {
        is CommonBusinesses -> EconomyType.Businesses
        is EconomyWithNumbers -> EconomyType.Numbers
        is EconomyWithPercentages -> EconomyType.Percentages
        UndefinedEconomy -> EconomyType.Undefined
    }

    fun getNumber(id: BusinessTemplateId) = when (this) {
        is EconomyWithNumbers -> businesses.getNumber(id)
        is EconomyWithPercentages -> businesses.getNumber(total, id)
        else -> null
    }

    fun getTotalEconomy() = when (this) {
        is EconomyWithNumbers -> businesses.calculateTotal()
        is EconomyWithPercentages -> total
        is CommonBusinesses, UndefinedEconomy -> null
    }

    fun contains(id: BusinessTemplateId) = when (this) {
        is CommonBusinesses -> businesses.contains(id)
        is EconomyWithNumbers -> businesses.map.containsKey(id)
        is EconomyWithPercentages -> businesses.map.containsKey(id)
        UndefinedEconomy -> false
    }

    fun businesses() = when (this) {
        is CommonBusinesses -> businesses
        is EconomyWithNumbers -> businesses.map.keys
        is EconomyWithPercentages -> businesses.map.keys
        UndefinedEconomy -> emptySet()
    }

}

@Serializable
@SerialName("Businesses")
data class CommonBusinesses(
    val businesses: Set<BusinessTemplateId> = emptySet(),
) : Economy()

@Serializable
@SerialName("Numbers")
data class EconomyWithNumbers(
    val businesses: NumberDistribution<BusinessTemplateId> = NumberDistribution(),
) : Economy()

@Serializable
@SerialName("Percentages")
data class EconomyWithPercentages(
    val total: Int,
    val businesses: PercentageDistribution<BusinessTemplateId> = PercentageDistribution()
) : Economy()

@Serializable
@SerialName("Undefined")
data object UndefinedEconomy : Economy()
