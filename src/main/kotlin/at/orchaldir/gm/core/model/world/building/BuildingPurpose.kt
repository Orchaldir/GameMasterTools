package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.economy.business.BusinessId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BuildingPurpose {

    fun getType() = when (this) {
        is ApartmentHouse -> BuildingPurposeType.ApartmentHouse
        is MultipleBusiness -> BuildingPurposeType.MultipleBusiness
        is SingleBusiness -> BuildingPurposeType.SingleBusiness
        is SingleFamilyHouse -> BuildingPurposeType.SingleFamilyHouse
    }

}

// business

@Serializable
@SerialName("SingleBusiness")
data class SingleBusiness(val business: BusinessId) : BuildingPurpose()

@Serializable
@SerialName("MultipleBusiness")
data class MultipleBusiness(val businesses: Set<BusinessId>) : BuildingPurpose()

// home

@Serializable
@SerialName("SingleFamilyHouse")
data object SingleFamilyHouse : BuildingPurpose()

@Serializable
@SerialName("ApartmentHouse")
data class ApartmentHouse(val apartments: Int) : BuildingPurpose()



