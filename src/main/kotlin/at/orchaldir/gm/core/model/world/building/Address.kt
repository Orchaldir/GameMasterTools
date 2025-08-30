package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.world.street.StreetId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AddressType {
    None,
    Town,
    Street,
    Crossing,
}

@Serializable
sealed class Address {

    fun getType() = when (this) {
        is CrossingAddress -> AddressType.Crossing
        NoAddress -> AddressType.None
        is StreetAddress -> AddressType.Street
        is TownAddress -> AddressType.Town
    }

    fun getHouseNumber(other: StreetId) = when (this) {
        is CrossingAddress -> streets.contains(other)
        is StreetAddress -> street == other
        else -> false
    }

    fun contains(other: StreetId) = when (this) {
        is CrossingAddress -> streets.contains(other)
        is StreetAddress -> street == other
        else -> false
    }

}

@Serializable
@SerialName("None")
data object NoAddress : Address()

@Serializable
@SerialName("Town")
data class TownAddress(val houseNumber: Int) : Address()

@Serializable
@SerialName("Street")
data class StreetAddress(val street: StreetId, val houseNumber: Int) : Address()

@Serializable
@SerialName("Crossing")
data class CrossingAddress(val streets: List<StreetId>) : Address()



