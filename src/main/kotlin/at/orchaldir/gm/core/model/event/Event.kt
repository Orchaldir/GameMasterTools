package at.orchaldir.gm.core.model.event

import at.orchaldir.gm.core.model.character.CauseOfDeath
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CharacterOrigin
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.town.TownId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Event {

    abstract fun getDate(): Date

}

// building

@Serializable
@SerialName("BuildingConstructed")
data class BuildingConstructedEvent(
    val constructionDate: Date,
    val buildingId: BuildingId,
) : Event() {

    override fun getDate() = constructionDate

}

// character

@Serializable
@SerialName("CharacterOrigin")
data class CharacterOriginEvent(
    val day: Day,
    val characterId: CharacterId,
    val origin: CharacterOrigin,
) : Event() {

    override fun getDate() = day

}

@Serializable
@SerialName("CharacterDeath")
data class CharacterDeathEvent(
    val day: Day,
    val characterId: CharacterId,
    val causeOfDeath: CauseOfDeath,
) : Event() {

    override fun getDate() = day

}

// town

@Serializable
@SerialName("TownFounding")
data class TownFoundingEvent(
    val foundingDate: Date,
    val townId: TownId,
) : Event() {

    override fun getDate() = foundingDate

}