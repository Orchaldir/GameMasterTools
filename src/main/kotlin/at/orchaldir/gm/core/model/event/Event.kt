package at.orchaldir.gm.core.model.event

import at.orchaldir.gm.core.model.character.CauseOfDeath
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CharacterOrigin
import at.orchaldir.gm.core.model.time.Day
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Event {

    abstract fun getEventDay(): Day

}

@Serializable
@SerialName("CharacterOrigin")
data class CharacterOriginEvent(
    val day: Day,
    val characterId: CharacterId,
    val origin: CharacterOrigin,
) : Event() {

    override fun getEventDay() = day

}

@Serializable
@SerialName("CharacterDeath")
data class CharacterDeathEvent(
    val day: Day,
    val characterId: CharacterId,
    val causeOfDeath: CauseOfDeath,
) : Event() {

    override fun getEventDay() = day

}
