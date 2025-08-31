package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PositionType {
    Undefined,
    Apartment,
    District,
    Homeless,
    Building,
    Plane,
    Realm,
    Town,
    TownMap,
}

@Serializable
sealed class Position {

    fun getType() = when (this) {
        Homeless -> PositionType.Homeless
        is InApartment -> PositionType.Apartment
        is InBuilding -> PositionType.Building
        is InDistrict -> PositionType.District
        is InPlane -> PositionType.Plane
        is InRealm -> PositionType.Realm
        is InTown -> PositionType.Town
        is InTownMap -> PositionType.TownMap
        UndefinedPosition -> PositionType.Undefined
    }

    fun getId() = when (this) {
        Homeless -> null
        is InApartment -> building
        is InDistrict -> district
        is InBuilding -> building
        is InPlane -> plane
        is InRealm -> realm
        is InTown -> town
        is InTownMap -> townMap
        UndefinedPosition -> null
    }

    open fun getApartmentIndex(): Int? = when (this) {
        is InApartment -> apartmentIndex
        else -> null
    }

    fun getBuilding() = when (this) {
        is InApartment -> building
        is InBuilding -> building
        else -> null
    }

    fun <ID : Id<ID>> isIn(id: ID) = when (this) {
        Homeless -> false
        is InApartment -> building == id
        is InDistrict -> district == id
        is InBuilding -> building == id
        is InPlane -> plane == id
        is InRealm -> realm == id
        is InTown -> town == id
        is InTownMap -> townMap == id
        UndefinedPosition -> false
    }

    open fun isInApartment(building: BuildingId, apartmentIndex: Int) = false
    open fun isInBuilding(building: BuildingId) = false
    open fun isIn(building: BuildingId) = false
    open fun isIn(district: DistrictId) = false
    open fun isIn(plane: PlaneId) = false
    open fun isIn(realm: RealmId) = false
    open fun isIn(town: TownId) = false
    open fun isIn(townMap: TownMapId) = false

}

@Serializable
@SerialName("Homeless")
data object Homeless : Position()

@Serializable
@SerialName("Apartment")
data class InApartment(
    val building: BuildingId,
    val apartmentIndex: Int,
) : Position() {

    init {
        require(apartmentIndex >= 0) { "Apartment index must be greater 0!" }
    }

    override fun isIn(building: BuildingId) = this.building == building
    override fun isInApartment(building: BuildingId, apartmentIndex: Int) = isIn(building) &&
            this.apartmentIndex == apartmentIndex

}

@Serializable
@SerialName("Building")
data class InBuilding(val building: BuildingId) : Position() {

    override fun isIn(building: BuildingId) = isInBuilding(building)
    override fun isInBuilding(building: BuildingId) = this.building == building

}

@Serializable
@SerialName("District")
data class InDistrict(val district: DistrictId) : Position() {

    override fun isIn(district: DistrictId) = this.district == district

}

@Serializable
@SerialName("Plane")
data class InPlane(val plane: PlaneId) : Position() {

    override fun isIn(plane: PlaneId) = this.plane == plane

}

@Serializable
@SerialName("Realm")
data class InRealm(val realm: RealmId) : Position() {

    override fun isIn(realm: RealmId) = this.realm == realm

}

@Serializable
@SerialName("Town")
data class InTown(val town: TownId) : Position() {

    override fun isIn(town: TownId) = this.town == town

}

@Serializable
@SerialName("TownMap")
data class InTownMap(
    val townMap: TownMapId,
    val tileIndex: Int,
) : Position() {

    override fun isIn(townMap: TownMapId) = this.townMap == townMap

}

@Serializable
@SerialName("Undefined")
data object UndefinedPosition : Position()
