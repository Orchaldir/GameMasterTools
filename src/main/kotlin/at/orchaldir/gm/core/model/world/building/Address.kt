package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.world.street.StreetId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AddressType {
    None,
    Settlement,
    Street,
    Crossing,
}

@Serializable
sealed class Address {

    fun getType() = when (this) {
        is CrossingAddress -> AddressType.Crossing
        NoAddress -> AddressType.None
        is SettlementAddress -> AddressType.Settlement
        is StreetAddress -> AddressType.Street
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
@SerialName("Settlement")
data class SettlementAddress(val houseNumber: Int) : Address()

@Serializable
@SerialName("Street")
data class StreetAddress(val street: StreetId, val houseNumber: Int) : Address()

@Serializable
@SerialName("Crossing")
data class CrossingAddress(val streets: List<StreetId>) : Address()



