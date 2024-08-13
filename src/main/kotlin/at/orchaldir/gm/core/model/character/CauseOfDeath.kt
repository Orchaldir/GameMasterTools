package at.orchaldir.gm.core.model.character

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CauseOfDeath

@Serializable
@SerialName("Alive")
data object Alive : CauseOfDeath()

@Serializable
@SerialName("Accident")
data object Accident : CauseOfDeath()

@Serializable
@SerialName("Murder")
data class Murder(val killer: CharacterId) : CauseOfDeath()

@Serializable
@SerialName("OldAge")
data object OldAge : CauseOfDeath()

