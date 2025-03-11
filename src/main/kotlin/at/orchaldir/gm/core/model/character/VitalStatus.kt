package at.orchaldir.gm.core.model.character

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
