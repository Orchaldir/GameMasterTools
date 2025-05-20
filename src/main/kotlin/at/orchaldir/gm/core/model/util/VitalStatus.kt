package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.model.time.date.Date
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class VitalStatusType {
    Alive,
    Dead,
}

@Serializable
sealed class VitalStatus {
    fun getType() = when (this) {
        is Alive -> VitalStatusType.Alive
        is Dead -> VitalStatusType.Dead
    }

    fun getCauseOfDeath() = when (this) {
        is Alive -> null
        is Dead -> cause
    }

    fun getDeathDate() = when (this) {
        is Alive -> null
        is Dead -> deathDay
    }

    fun isCausedBy(battle: BattleId) =
        this is Dead && cause is DeathInBattle && cause.battle == battle

    fun isCausedBy(catastrophe: CatastropheId) =
        this is Dead && cause is DeathByCatastrophe && cause.catastrophe == catastrophe

    fun isCausedBy(war: WarId) = this is Dead && cause is DeathByWar && cause.war == war
}

@Serializable
@SerialName("Alive")
data object Alive : VitalStatus()

@Serializable
@SerialName("Dead")
data class Dead(
    val deathDay: Date,
    val cause: CauseOfDeath,
) : VitalStatus()
