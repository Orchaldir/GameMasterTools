package at.orchaldir.gm.core.model.world.building

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BuildingPurposeType {
    ApartmentHouse,
    BusinessAndHome,
    SingleBusiness,
    SingleFamilyHouse;

    fun isBusiness() = this == SingleBusiness ||
            this == BusinessAndHome

    fun isHome() = this == BusinessAndHome ||
            this == SingleFamilyHouse
}

@Serializable
sealed class BuildingPurpose {

    fun getType() = when (this) {
        is ApartmentHouse -> BuildingPurposeType.ApartmentHouse
        BusinessAndHome -> BuildingPurposeType.BusinessAndHome
        SingleBusiness -> BuildingPurposeType.SingleBusiness
        SingleFamilyHouse -> BuildingPurposeType.SingleFamilyHouse
    }

    fun isHome() = when (this) {
        BusinessAndHome, SingleFamilyHouse -> true
        else -> false
    }
}

@Serializable
@SerialName("BusinessAndHome")
data object BusinessAndHome : BuildingPurpose()

// business

@Serializable
@SerialName("SingleBusiness")
data object SingleBusiness : BuildingPurpose()

// home

@Serializable
@SerialName("SingleFamilyHouse")
data object SingleFamilyHouse : BuildingPurpose()

@Serializable
@SerialName("ApartmentHouse")
data class ApartmentHouse(val apartments: Int) : BuildingPurpose()



