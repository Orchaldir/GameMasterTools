package at.orchaldir.gm.core.model.world.terrain

import at.orchaldir.gm.core.model.realm.CatastropheId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RegionDataType {
    Battlefield,
    Continent,
    Mountain,
    Undefined,
    Wasteland,
}

@Serializable
sealed class RegionData {

    fun getType() = when (this) {
        Battlefield -> RegionDataType.Battlefield
        Continent -> RegionDataType.Continent
        Mountain -> RegionDataType.Mountain
        UndefinedRegionData -> RegionDataType.Undefined
        is Wasteland -> RegionDataType.Wasteland
    }
}

@Serializable
@SerialName("Battlefield")
data object Battlefield : RegionData()

@Serializable
@SerialName("Continent")
data object Continent : RegionData()

@Serializable
@SerialName("Mountain")
data object Mountain : RegionData()

@Serializable
@SerialName("Undefined")
data object UndefinedRegionData : RegionData()

@Serializable
@SerialName("Wasteland")
data class Wasteland(
    val catastrophe: CatastropheId? = null,
) : RegionData()


