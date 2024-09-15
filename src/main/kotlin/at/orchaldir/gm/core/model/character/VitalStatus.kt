package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.time.Day
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class VitalStatus {
    fun getType() = when (this) {
        Alive -> VitalStatusType.Alive
        is Dead -> VitalStatusType.Dead
    }
}

@Serializable
@SerialName("Alive")
data object Alive : VitalStatus()

@Serializable
@SerialName("Dead")
data class Dead(
    val deathDay: Day,
    val cause: CauseOfDeath,
) : VitalStatus()
