package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.health.DiseaseId
import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ALLOWED_KILLERS = listOf(
    ReferenceType.Undefined,
    ReferenceType.Character,
    ReferenceType.Culture,
    ReferenceType.God,
    ReferenceType.Organization,
    ReferenceType.Realm,
)

enum class CauseOfDeathType {
    Accident,
    Battle,
    Catastrophe,
    Disease,
    Killed,
    OldAge,
    War,
    Undefined,
}

@Serializable
sealed class CauseOfDeath {
    fun getType() = when (this) {
        is Accident -> CauseOfDeathType.Accident
        is DeathByCatastrophe -> CauseOfDeathType.Catastrophe
        is DeathByDisease -> CauseOfDeathType.Disease
        is DeathInBattle -> CauseOfDeathType.Battle
        is DeathInWar -> CauseOfDeathType.War
        is KilledBy -> CauseOfDeathType.Killed
        is OldAge -> CauseOfDeathType.OldAge
        is UndefinedCauseOfDeath -> CauseOfDeathType.Undefined
    }

    fun <ID : Id<ID>> isDestroyedBy(id: ID) = when (this) {
        Accident, OldAge, UndefinedCauseOfDeath -> false
        is DeathByCatastrophe -> catastrophe == id
        is DeathByDisease -> disease == id
        is DeathInBattle -> battle == id
        is DeathInWar -> war == id
        is KilledBy -> killer.isId(id)
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
@SerialName("Disease")
data class DeathByDisease(
    val disease: DiseaseId,
) : CauseOfDeath()

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
@SerialName("Killed")
data class KilledBy(
    val killer: Reference,
) : CauseOfDeath()

@Serializable
@SerialName("OldAge")
data object OldAge : CauseOfDeath()

@Serializable
@SerialName("Undefined")
data object UndefinedCauseOfDeath : CauseOfDeath()
