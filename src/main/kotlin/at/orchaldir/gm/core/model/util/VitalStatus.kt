package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val VALID_VITAL_STATUS_FOR_CHARACTERS = setOf(VitalStatusType.Alive, VitalStatusType.Dead)
val VALID_VITAL_STATUS_FOR_REALM = VitalStatusType.entries -
        VitalStatusType.Dead
val VALID_VITAL_STATUS_FOR_TOWN = VALID_VITAL_STATUS_FOR_REALM

enum class VitalStatusType {
    Abandoned,
    Alive,
    Dead,
    Destroyed,
}

@Serializable
sealed class VitalStatus {
    fun getType() = when (this) {
        is Abandoned -> VitalStatusType.Abandoned
        is Alive -> VitalStatusType.Alive
        is Dead -> VitalStatusType.Dead
        is Destroyed -> VitalStatusType.Destroyed
    }

    fun getCauseOfDeath() = when (this) {
        is Abandoned -> cause
        is Alive -> null
        is Dead -> cause
        is Destroyed -> cause
    }

    fun getDeathDate() = when (this) {
        is Abandoned -> date
        is Alive -> null
        is Dead -> date
        is Destroyed -> date
    }

    fun <ID : Id<ID>> isDestroyedBy(id: ID) = if (this is Dead) {
        when (cause) {
            Accident -> false
            is DeathByCatastrophe -> cause.catastrophe == id
            is DeathByDisease -> cause.disease == id
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
@SerialName("Abandoned")
data class Abandoned(
    val date: Date,
    val cause: CauseOfDeath,
) : VitalStatus()

@Serializable
@SerialName("Alive")
data object Alive : VitalStatus()

@Serializable
@SerialName("Dead")
data class Dead(
    val date: Date,
    val cause: CauseOfDeath,
) : VitalStatus()

@Serializable
@SerialName("Destroyed")
data class Destroyed(
    val date: Date,
    val cause: CauseOfDeath,
) : VitalStatus()
