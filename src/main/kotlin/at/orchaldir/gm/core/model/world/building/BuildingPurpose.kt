package at.orchaldir.gm.core.model.world.building

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BuildingPurpose

@Serializable
@SerialName("SingleFamilyHouse")
data class SingleFamilyHouse(val home: Home = Home()) : BuildingPurpose()

@Serializable
@SerialName("ApartmentHouse")
data class ApartmentHouse(val apartments: List<Home>) : BuildingPurpose()



