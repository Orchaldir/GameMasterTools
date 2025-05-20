package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.realm.WarId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CauseOfDeathType {
    Accident,
    Battle,
    Catastrophe,
    Illness,
    Murder,
    OldAge,
    War,
}

@Serializable
sealed class CauseOfDeath {
    fun getType() = when (this) {
        is Accident -> CauseOfDeathType.Accident
        is DeathByCatastrophe -> CauseOfDeathType.Catastrophe
        is DeathByIllness -> CauseOfDeathType.Illness
        is DeathInBattle -> CauseOfDeathType.Battle
        is DeathInWar -> CauseOfDeathType.War
        is Murder -> CauseOfDeathType.Murder
        is OldAge -> CauseOfDeathType.OldAge
    }
}

@Serializable
@SerialName("Accident")
data object Accident : CauseOfDeath()

@Serializable
@SerialName("Catastrophe")
data class DeathByCatastrophe(
    val catastrophe: CatastropheId,
) : CauseOfDeath()

@Serializable
@SerialName("Illness")
data object DeathByIllness : CauseOfDeath()

@Serializable
@SerialName("Battle")
data class DeathInBattle(
    val battle: BattleId,
) : CauseOfDeath()

@Serializable
@SerialName("War")
data class DeathInWar(
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
