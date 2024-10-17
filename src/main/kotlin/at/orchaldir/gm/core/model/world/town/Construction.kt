package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.railway.RailwayTypeId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Construction {

    fun <ID : Id<ID>> contains(id: ID): Boolean = when (this) {
        is BuildingTile -> building == id
        is RailwayTile -> railwayType == id
        is StreetTile -> street == id
        is CrossingTile -> if (id is RailwayTypeId) {
            railways.any { it.first == id }
        } else {
            false
        }
        NoConstruction -> false
    }

    fun getStreet() = when (this) {
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
    val street: StreetId,
    val connection: TileConnection = TileConnection.Horizontal,
) : Construction()

@Serializable
@SerialName("Railway")
data class RailwayTile(
    val railwayType: RailwayTypeId,
    val connection: TileConnection = TileConnection.Horizontal,
) : Construction()

@Serializable
@SerialName("Crossing")
data class CrossingTile(
    val railways: Set<Pair<RailwayTypeId, TileConnection>>,
    val streets: Set<Pair<StreetId, TileConnection>>,
) : Construction()

