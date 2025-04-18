package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.world.building.BuildingId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HousingStatusType {
    Undefined,
    Homeless,
    InApartment,
    InHouse,
}

@Serializable
sealed class HousingStatus {

    open fun getApartmentIndex(): Int? = when (this) {
        is InApartment -> apartmentIndex
        else -> null
    }

    fun getBuilding() = when (this) {
        is InApartment -> building
        is InHouse -> building
        else -> null
    }

    fun getType() = when (this) {
        Homeless -> HousingStatusType.Homeless
        is InApartment -> HousingStatusType.InApartment
        is InHouse -> HousingStatusType.InHouse
        UndefinedHousingStatus -> HousingStatusType.Undefined
    }

    open fun isLivingIn(building: BuildingId) = false
    open fun isLivingInApartment(building: BuildingId, apartmentIndex: Int) = false
    open fun isLivingInHouse(building: BuildingId) = false

}

@Serializable
@SerialName("InHouse")
data class InHouse(val building: BuildingId) : HousingStatus() {

    override fun isLivingIn(building: BuildingId) = isLivingInHouse(building)
    override fun isLivingInHouse(building: BuildingId) = this.building == building

}

@Serializable
@SerialName("InApartment")
data class InApartment(
    val building: BuildingId,
    val apartmentIndex: Int,
) : HousingStatus() {

    init {
        require(apartmentIndex >= 0) { "Apartment index must be greater 0!" }
    }

    override fun isLivingIn(building: BuildingId) = this.building == building
    override fun isLivingInApartment(building: BuildingId, apartmentIndex: Int) = isLivingIn(building) &&
            this.apartmentIndex == apartmentIndex

}

@Serializable
@SerialName("Homeless")
data object Homeless : HousingStatus()

@Serializable
@SerialName("Undefined")
data object UndefinedHousingStatus : HousingStatus()
