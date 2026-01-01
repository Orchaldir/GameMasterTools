package at.orchaldir.gm.core.model.character

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
