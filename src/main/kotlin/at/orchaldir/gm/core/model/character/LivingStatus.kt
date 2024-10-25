package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.world.building.BuildingId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class LivingStatus

@Serializable
@SerialName("InHouse")
data class InHouse(val building: BuildingId) : LivingStatus()

@Serializable
@SerialName("InApartment")
data class InApartment(
    val building: BuildingId,
    val apartment: Int,
) : LivingStatus()

@Serializable
@SerialName("Homeless")
data object Homeless : LivingStatus()
