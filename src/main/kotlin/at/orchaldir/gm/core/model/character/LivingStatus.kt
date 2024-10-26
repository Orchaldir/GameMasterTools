package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.world.building.BuildingId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class LivingStatusType {
    Homeless,
    InApartment,
    InHouse,
}

@Serializable
sealed class LivingStatus {

    fun getType() = when (this) {
        Homeless -> LivingStatusType.Homeless
        is InApartment -> LivingStatusType.InApartment
        is InHouse -> LivingStatusType.InHouse
    }

    open fun isLivingInApartment(building: BuildingId, apartment: Int) = false
    open fun isLivingInHouse(building: BuildingId) = false

}

@Serializable
@SerialName("InHouse")
data class InHouse(val building: BuildingId) : LivingStatus() {

    override fun isLivingInHouse(building: BuildingId) = this.building == building

}

@Serializable
@SerialName("InApartment")
data class InApartment(
    val building: BuildingId,
    val apartmentIndex: Int,
) : LivingStatus() {

    init {
        require(apartmentIndex >= 0) { "Apartment index must be greater 0!" }
    }

    override fun isLivingInApartment(building: BuildingId, apartmentIndex: Int) = this.building == building &&
            this.apartmentIndex == apartmentIndex

}

@Serializable
@SerialName("Homeless")
data object Homeless : LivingStatus()
