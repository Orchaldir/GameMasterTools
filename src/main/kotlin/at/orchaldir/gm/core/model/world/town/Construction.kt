package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTypeId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Construction {

    fun contains(id: StreetId) = when (this) {
        is StreetTile -> street == id
        else -> false
    }

    fun contains(id: StreetTypeId) = when (this) {
        is StreetTile -> type == id
        else -> false
    }

    fun getOptionalStreet() = when (this) {
        is StreetTile -> street
        else -> null
    }

}

@Serializable
@SerialName("None")
data object NoConstruction : Construction()

@Serializable
@SerialName("Building")
data class BuildingTile(val building: BuildingId) : Construction()

@Serializable
@SerialName("Street")
data class StreetTile(
    val type: StreetTypeId = StreetTypeId(0),
    val street: StreetId? = null,
) : Construction()


