package at.orchaldir.gm.core.model.world.terrain

import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.CatastropheId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RegionDataType {
    Battlefield,
    Continent,
    Desert,
    Forrest,
    Lake,
    Mountain,
    Plains,
    Sea,
    Undefined,
    Wasteland,
}

@Serializable
sealed class RegionData {

    fun getType() = when (this) {
        is Battlefield -> RegionDataType.Battlefield
        Continent -> RegionDataType.Continent
        Desert -> RegionDataType.Desert
        Forrest -> RegionDataType.Forrest
        Lake -> RegionDataType.Lake
        Plains -> RegionDataType.Plains
        Mountain -> RegionDataType.Mountain
        Sea -> RegionDataType.Sea
        UndefinedRegionData -> RegionDataType.Undefined
        is Wasteland -> RegionDataType.Wasteland
    }

    fun getAllowedRegionTypes() = if (this is Continent) {
        ALLOWED_CONTINENT_POSITIONS
    } else {
        ALLOWED_REGION_POSITIONS
    }

    fun isCreatedBy(battle: BattleId) = when (this) {
        is Battlefield -> this.battle == battle
        else -> false
    }

    fun isCreatedBy(catastrophe: CatastropheId) = when (this) {
        is Wasteland -> this.catastrophe == catastrophe
        else -> false
    }
}

@Serializable
@SerialName("Battlefield")
data class Battlefield(
    val battle: BattleId? = null,
) : RegionData()

@Serializable
@SerialName("Continent")
data object Continent : RegionData()

@Serializable
@SerialName("Desert")
data object Desert : RegionData()

@Serializable
@SerialName("Forrest")
data object Forrest : RegionData()

@Serializable
@SerialName("Lake")
data object Lake : RegionData()

@Serializable
@SerialName("Plains")
data object Plains : RegionData()

@Serializable
@SerialName("Mountain")
data object Mountain : RegionData()

@Serializable
@SerialName("Sea")
data object Sea : RegionData()

@Serializable
@SerialName("Undefined")
data object UndefinedRegionData : RegionData()

@Serializable
@SerialName("Wasteland")
data class Wasteland(
    val catastrophe: CatastropheId? = null,
) : RegionData()


