package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.utils.Id
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

    fun <ID : Id<ID>> isDestroyedBy(id: ID) = if (this is Dead) {
        when (cause) {
            Abandoned -> false
            Accident -> false
            is DeathByCatastrophe -> cause.catastrophe == id
            is DeathByIllness -> cause.illness == id
            is DeathInBattle -> cause.battle == id
            is DeathInWar -> cause.war == id
            is Murder -> cause.killer == id
            OldAge -> false
            UndefinedCauseOfDeath -> false
        }
    } else {
        false
    }
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
