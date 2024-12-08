package at.orchaldir.gm.core.model.character

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CauseOfDeathType {
    Accident,
    Illness,
    Murder,
    OldAge,
}

@Serializable
sealed class CauseOfDeath {
    fun getType() = when (this) {
        is Accident -> CauseOfDeathType.Accident
        is DeathByIllness -> CauseOfDeathType.Illness
        is Murder -> CauseOfDeathType.Murder
        is OldAge -> CauseOfDeathType.OldAge
    }
}

@Serializable
@SerialName("Accident")
data object Accident : CauseOfDeath()

@Serializable
@SerialName("Illness")
data object DeathByIllness : CauseOfDeath()

@Serializable
@SerialName("Murder")
data class Murder(
    val killer: CharacterId,
) : CauseOfDeath()

@Serializable
@SerialName("OldAge")
data object OldAge : CauseOfDeath()


