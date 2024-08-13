package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.time.Day
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CauseOfDeath {
    open fun getDeathDate(): Day? = null

    fun getType() = when (this) {
        is Accident -> CauseOfDeathType.Accident
        Alive -> CauseOfDeathType.Alive
        is Murder -> CauseOfDeathType.Murder
        is OldAge -> CauseOfDeathType.OldAge
    }
}

@Serializable
@SerialName("Alive")
data object Alive : CauseOfDeath()

@Serializable
@SerialName("Accident")
data class Accident(val deathDay: Day) : CauseOfDeath() {
    override fun getDeathDate() = deathDay
}

@Serializable
@SerialName("Murder")
data class Murder(
    val deathDay: Day,
    val killer: CharacterId,
) : CauseOfDeath() {
    override fun getDeathDate() = deathDay
}

@Serializable
@SerialName("OldAge")
data class OldAge(val deathDay: Day) : CauseOfDeath() {
    override fun getDeathDate() = deathDay
}


