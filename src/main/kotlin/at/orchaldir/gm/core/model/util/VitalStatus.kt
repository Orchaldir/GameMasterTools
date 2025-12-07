package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class VitalStatusType {
    Abandoned,
    Alive,
    Closed,
    Dead,
    Destroyed,
    Vanished,
}

@Serializable
sealed class VitalStatus {
    fun getType() = when (this) {
        is Abandoned -> VitalStatusType.Abandoned
        is Alive -> VitalStatusType.Alive
        is Closed -> VitalStatusType.Closed
        is Dead -> VitalStatusType.Dead
        is Destroyed -> VitalStatusType.Destroyed
        is Vanished -> VitalStatusType.Vanished
    }

    fun getCauseOfDeath() = when (this) {
        is Abandoned -> cause
        is Alive -> null
        is Closed -> null
        is Dead -> cause
        is Destroyed -> cause
        is Vanished -> null
    }

    fun getDeathDate() = when (this) {
        is Abandoned -> date
        is Alive -> null
        is Closed -> date
        is Dead -> date
        is Destroyed -> date
        is Vanished -> date
    }

    fun <ID : Id<ID>> isDestroyedBy(id: ID) = when (this) {
        is Abandoned -> cause.isDestroyedBy(id)
        Alive -> false
        is Closed -> false
        is Dead -> cause.isDestroyedBy(id)
        is Destroyed -> cause.isDestroyedBy(id)
        is Vanished -> false
    }
}

@Serializable
@SerialName("Abandoned")
data class Abandoned(
    val date: Date,
    val cause: CauseOfDeath = UndefinedCauseOfDeath,
) : VitalStatus()

@Serializable
@SerialName("Alive")
data object Alive : VitalStatus()

@Serializable
@SerialName("Closed")
data class Closed(
    val date: Date,
) : VitalStatus()

@Serializable
@SerialName("Dead")
data class Dead(
    val date: Date,
    val cause: CauseOfDeath = UndefinedCauseOfDeath,
) : VitalStatus()

@Serializable
@SerialName("Destroyed")
data class Destroyed(
    val date: Date,
    val cause: CauseOfDeath = UndefinedCauseOfDeath,
) : VitalStatus()

@Serializable
@SerialName("Vanished")
data class Vanished(
    val date: Date,
) : VitalStatus()
