package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.LifeStageId
import at.orchaldir.gm.core.model.time.date.Date
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CharacterAgeType {
    Birthdate,
    LifeStage,
    DefaultLifeStage,
}

@Serializable
sealed class CharacterAge {

    fun getType() = when (this) {
        is AgeViaBirthdate -> CharacterAgeType.Birthdate
        AgeViaDefaultLifeStage -> CharacterAgeType.DefaultLifeStage
        is AgeViaLifeStage -> CharacterAgeType.LifeStage
    }

    fun date() = when (this) {
        is AgeViaBirthdate -> date
        AgeViaDefaultLifeStage -> null
        is AgeViaLifeStage -> null
    }

    fun approximateBirthday(state: State, raceId: RaceId): Date = when (this) {
        is AgeViaBirthdate -> date
        AgeViaDefaultLifeStage -> {
            val race = state.getRaceStorage().getOrThrow(raceId)
            val lifeStageId = race.lifeStages.getDefaultLifeStageId()
                ?: error("ImmutableLifeStage is not supported by AgeViaDefaultLifeStage!")
            approximateBirthday(state, raceId, lifeStageId)
        }
        is AgeViaLifeStage -> approximateBirthday(state, raceId, lifeStage)
    }

    private fun approximateBirthday(state: State, raceId: RaceId, lifeStage: LifeStageId) = state
        .getRaceStorage()
        .getOrThrow(raceId)
        .lifeStages.approximateBirthDate(state, lifeStage)
}

@Serializable
@SerialName("Birthdate")
data class AgeViaBirthdate(
    val date: Date,
) : CharacterAge()

@Serializable
@SerialName("DefaultLifeStage")
data object AgeViaDefaultLifeStage : CharacterAge()

@Serializable
@SerialName("LifeStage")
data class AgeViaLifeStage(
    val lifeStage: LifeStageId,
) : CharacterAge()
