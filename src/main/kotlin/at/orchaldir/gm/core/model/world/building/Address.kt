package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.world.street.StreetId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Address

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



