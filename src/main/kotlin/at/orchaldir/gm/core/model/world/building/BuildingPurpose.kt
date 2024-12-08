package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.economy.business.BusinessId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BuildingPurposeType {
    ApartmentHouse,
    BusinessAndHome,
    SingleBusiness,
    SingleFamilyHouse;

    fun isBusiness() = this == SingleBusiness
}

@Serializable
sealed class BuildingPurpose {

    fun getType() = when (this) {
        is ApartmentHouse -> BuildingPurposeType.ApartmentHouse
        is BusinessAndHome -> BuildingPurposeType.BusinessAndHome
        is SingleBusiness -> BuildingPurposeType.SingleBusiness
        is SingleFamilyHouse -> BuildingPurposeType.SingleFamilyHouse
    }

    fun contains(business: BusinessId) = when (this) {
        is BusinessAndHome -> this.business == business
        is SingleBusiness -> this.business == business
        else -> false
    }

    fun getBusinesses() = when (this) {
        is BusinessAndHome -> setOf(business)
        is SingleBusiness -> setOf(business)
        else -> emptySet()
    }
}

@Serializable
@SerialName("BusinessAndHome")
data class BusinessAndHome(val business: BusinessId) : BuildingPurpose()

// business

@Serializable
@SerialName("SingleBusiness")
data class SingleBusiness(val business: BusinessId) : BuildingPurpose()

// home

@Serializable
@SerialName("SingleFamilyHouse")
data object SingleFamilyHouse : BuildingPurpose()

@Serializable
@SerialName("ApartmentHouse")
data class ApartmentHouse(val apartments: Int) : BuildingPurpose()



