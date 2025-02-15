package at.orchaldir.gm.core.model.race

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.HasStartDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RaceOriginType {
    Cosmic,
    Created,
    Evolved,
    Hybrid,
    Modified,
    Original,
}

@Serializable
sealed class RaceOrigin : HasStartDate {

    fun getType() = when (this) {
        is CosmicRace -> RaceOriginType.Cosmic
        is CreatedRace -> RaceOriginType.Created
        is EvolvedRace -> RaceOriginType.Evolved
        is HybridRace -> RaceOriginType.Hybrid
        is ModifiedRace -> RaceOriginType.Modified
        OriginalRace -> RaceOriginType.Original
    }

    fun isChildOf(race: RaceId) = when (this) {
        is HybridRace -> first == race || second == race
        is EvolvedRace -> parent == race
        is ModifiedRace -> parent == race
        else -> false
    }

    override fun startDate() = when (this) {
        is CreatedRace -> date
        is ModifiedRace -> date
        else -> null
    }

}

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
@SerialName("Created")
data class CreatedRace(
    val creator: Creator,
    val date: Date,
) : RaceOrigin()

/**
 * A race evolved naturally from another race.
 */
@Serializable
@SerialName("Evolved")
data class EvolvedRace(val parent: RaceId) : RaceOrigin()

/**
 * The natural hybrid of 2 parent races. e.g. a half-elf
 */
@Serializable
@SerialName("Hybrid")
data class HybridRace(val first: RaceId, val second: RaceId) : RaceOrigin()

/**
 * A race modified by someone. e.g. the dolgrim in Eberron
 */
@Serializable
@SerialName("Modified")
data class ModifiedRace(
    val parent: RaceId,
    val modifier: Creator,
    val date: Date,
) : RaceOrigin()

/**
 * A race without predecessor or creator.
 */
@Serializable
@SerialName("Original")
data object OriginalRace : RaceOrigin()
