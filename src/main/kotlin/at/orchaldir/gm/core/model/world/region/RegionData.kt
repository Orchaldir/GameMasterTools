package at.orchaldir.gm.core.model.world.region

import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.CatastropheId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RegionDataType {
    Battlefield,
    Continent,
    Forrest,
    Mountain,
    Undefined,
    Wasteland,
}

@Serializable
sealed class RegionData {

    fun getType() = when (this) {
        is Battlefield -> RegionDataType.Battlefield
        Continent -> RegionDataType.Continent
        Forrest -> RegionDataType.Forrest
        Mountain -> RegionDataType.Mountain
        UndefinedRegionData -> RegionDataType.Undefined
        is Wasteland -> RegionDataType.Wasteland
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
@SerialName("Forrest")
data object Forrest : RegionData()

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


