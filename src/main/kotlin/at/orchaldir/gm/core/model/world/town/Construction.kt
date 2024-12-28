package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Construction {

    fun contains(id: StreetId) = when (this) {
        is StreetTile -> streetId == id
        else -> false
    }

    fun contains(id: StreetTemplateId) = when (this) {
        is StreetTile -> templateId == id
        else -> false
    }

    fun getOptionalStreet() = when (this) {
        is StreetTile -> streetId
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
    val templateId: StreetTemplateId = StreetTemplateId(0),
    val streetId: StreetId? = null,
) : Construction()


