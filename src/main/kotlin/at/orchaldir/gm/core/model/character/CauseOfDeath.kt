package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.realm.WarId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CauseOfDeathType {
    Accident,
    Illness,
    Murder,
    OldAge,
    War,
}

@Serializable
sealed class CauseOfDeath {
    fun getType() = when (this) {
        is Accident -> CauseOfDeathType.Accident
        is DeathByIllness -> CauseOfDeathType.Illness
        is DeathByWar -> CauseOfDeathType.War
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
@SerialName("War")
data class DeathByWar(
    val war: WarId,
) : CauseOfDeath()

@Serializable
@SerialName("Murder")
data class Murder(
    val killer: CharacterId,
) : CauseOfDeath()

@Serializable
@SerialName("OldAge")
data object OldAge : CauseOfDeath()
