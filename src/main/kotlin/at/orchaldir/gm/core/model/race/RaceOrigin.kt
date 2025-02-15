package at.orchaldir.gm.core.model.race

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.Creator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RaceOriginType {
    Cosmic,
    Created,
    Evolved,
    Hybrid,
    Original,
}

@Serializable
sealed class RaceOrigin {

    fun getType() = when (this) {
        is CosmicRace -> RaceOriginType.Cosmic
        is CreatedRace -> RaceOriginType.Created
        is EvolvedRace -> RaceOriginType.Evolved
        is HybridRace -> RaceOriginType.Hybrid
        OriginalRace -> RaceOriginType.Original
    }

    fun isChildOf(race: RaceId) = when (this) {
        is HybridRace -> first == race || second == race
        is EvolvedRace -> parent == race
        else -> false
    }

}

/**
 * The natural hybrid of 2 parent races. e.g. a half-elf
 */
@Serializable
@SerialName("Hybrid")
data class HybridRace(val first: RaceId, val second: RaceId) : RaceOrigin()

/**
 * A race that is an integral part of creation. e.g. the different outsiders in Eberron
 */
@Serializable
@SerialName("Cosmic")
data object CosmicRace : RaceOrigin()

/**
 * A race created by someone. e.g. the warforged in Eberron
 */
@Serializable
@SerialName("Invented")
data class CreatedRace(
    val inventor: Creator,
    val date: Date,
) : RaceOrigin()

/**
 * A race evolved naturally from another race.
 */
@Serializable
@SerialName("Evolved")
data class EvolvedRace(val parent: RaceId) : RaceOrigin()

/**
 * A race without predecessor or creator.
 */
@Serializable
@SerialName("Original")
data object OriginalRace : RaceOrigin()
