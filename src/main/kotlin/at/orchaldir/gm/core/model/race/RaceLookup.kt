package at.orchaldir.gm.core.model.race

import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.RarityMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RaceLookupType {
    Race,
    Rarity,
}

@Serializable
sealed class RaceLookup {

    fun getType() = when (this) {
        is UseRace -> RaceLookupType.Race
        is UseRaceRarityMap -> RaceLookupType.Rarity
    }

    fun contains(id: RaceId) = when(this) {
        is UseRace -> race == id
        is UseRaceRarityMap -> map.contains(id)
    }

    fun defaultRace() = when(this) {
        is UseRace -> race
        is UseRaceRarityMap -> map.getMostCommon()
    }

    fun races() = when(this) {
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
