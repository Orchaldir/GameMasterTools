package at.orchaldir.gm.core.model.world.building

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BuildingPurpose {

    fun getType() = when (this) {
        is ApartmentHouse -> BuildingPurposeType.ApartmentHouse
        is SingleFamilyHouse -> BuildingPurposeType.SingleFamilyHouse
    }

}

@Serializable
@SerialName("SingleFamilyHouse")
data object SingleFamilyHouse : BuildingPurpose()

@Serializable
@SerialName("ApartmentHouse")
data class ApartmentHouse(val apartments: Int) : BuildingPurpose()



