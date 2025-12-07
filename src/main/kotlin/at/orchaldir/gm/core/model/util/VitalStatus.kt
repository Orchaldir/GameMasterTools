package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

    fun <ID : Id<ID>> isDestroyedBy(id: ID) = when (this) {
        is Abandoned -> cause.isDestroyedBy(id)
        Alive -> false
        is Dead -> cause.isDestroyedBy(id)
        is Destroyed -> cause.isDestroyedBy(id)
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
