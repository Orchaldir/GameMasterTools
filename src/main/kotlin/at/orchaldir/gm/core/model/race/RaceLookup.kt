package at.orchaldir.gm.core.model.race

import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.RarityMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RaceLookupType {
    Race,
    Rarity,
    Undefined,
}

@Serializable
sealed class RaceLookup {

    fun getType() = when (this) {
        UndefinedRaceLookup -> RaceLookupType.Undefined
        is UseRace -> RaceLookupType.Race
        is UseRaceRarityMap -> RaceLookupType.Race
    }

    fun races() = when(this) {
        UndefinedRaceLookup -> emptySet()
        is UseRace -> setOf(race)
        is UseRaceRarityMap -> map.getValidValues()
    }
}

@Serializable
@SerialName("Race")
data class UseRace(
    val race: RaceId,
) : RaceLookup()

@Serializable
@SerialName("Rarity")
data class UseRaceRarityMap(
    val map: OneOf<RaceId>,
) : RaceLookup()

@Serializable
@SerialName("Undefined")
data object UndefinedRaceLookup : RaceLookup()