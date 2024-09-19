package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.world.street.StreetId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Construction

@Serializable
@SerialName("None")
data object NoConstruction : Construction()

@Serializable
@SerialName("River")
data class StreetTile(val street: StreetId) : Construction()


