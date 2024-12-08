package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.time.Date
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class VitalStatusType {
    Alive,
    Dead,
}

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
    val deathDay: Date,
    val cause: CauseOfDeath,
) : VitalStatus()
