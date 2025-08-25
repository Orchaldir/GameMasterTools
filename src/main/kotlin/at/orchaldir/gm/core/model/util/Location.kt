package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.plane.PlaneId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class LocationType {
    Undefined,
    Homeless,
    Apartment,
    District,
    House,
    Plane,
    Realm,
    Town,
}

@Serializable
sealed class Location {

    fun getType() = when (this) {
        Homeless -> LocationType.Homeless
        is InApartment -> LocationType.Apartment
        is InDistrict -> LocationType.District
        is InHouse -> LocationType.House
        is InPlane -> LocationType.Plane
        is InRealm -> LocationType.Realm
        is InTown -> LocationType.Town
        UndefinedLocation -> LocationType.Undefined
    }

    open fun getApartmentIndex(): Int? = when (this) {
        is InApartment -> apartmentIndex
        else -> null
    }

    fun getBuilding() = when (this) {
        is InApartment -> building
        is InHouse -> building
        else -> null
    }

    open fun isInApartment(building: BuildingId, apartmentIndex: Int) = false
    open fun isInHouse(building: BuildingId) = false
    open fun isIn(building: BuildingId) = false
    open fun isIn(district: DistrictId) = false
    open fun isIn(plane: PlaneId) = false
    open fun isIn(realm: RealmId) = false
    open fun isIn(town: TownId) = false

}

@Serializable
@SerialName("Homeless")
data object Homeless : Location()

@Serializable
@SerialName("Apartment")
data class InApartment(
    val building: BuildingId,
    val apartmentIndex: Int,
) : Location() {

    init {
        require(apartmentIndex >= 0) { "Apartment index must be greater 0!" }
    }

    override fun isIn(building: BuildingId) = this.building == building
    override fun isInApartment(building: BuildingId, apartmentIndex: Int) = isIn(building) &&
            this.apartmentIndex == apartmentIndex

}

@Serializable
@SerialName("District")
data class InDistrict(val district: DistrictId) : Location() {

    override fun isIn(district: DistrictId) = this.district == district

}

@Serializable
@SerialName("House")
data class InHouse(val building: BuildingId) : Location() {

    override fun isIn(building: BuildingId) = isInHouse(building)
    override fun isInHouse(building: BuildingId) = this.building == building

}

@Serializable
@SerialName("Plane")
data class InPlane(val plane: PlaneId) : Location() {

    override fun isIn(plane: PlaneId) = this.plane == plane

}

@Serializable
@SerialName("Realm")
data class InRealm(val realm: RealmId) : Location() {

    override fun isIn(realm: RealmId) = this.realm == realm

}

@Serializable
@SerialName("Town")
data class InTown(val town: TownId) : Location() {

    override fun isIn(town: TownId) = this.town == town

}

@Serializable
@SerialName("Undefined")
data object UndefinedLocation : Location()
