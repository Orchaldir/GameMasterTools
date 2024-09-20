package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.world.street.StreetId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Construction {

    fun getStreet() = when (this) {
        is StreetTile -> street
        else -> null
    }

}

@Serializable
@SerialName("None")
data object NoConstruction : Construction()

@Serializable
@SerialName("Street")
data class StreetTile(val street: StreetId) : Construction()


