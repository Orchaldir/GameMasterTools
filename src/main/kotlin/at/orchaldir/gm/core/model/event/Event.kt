package at.orchaldir.gm.core.model.event

import at.orchaldir.gm.core.model.character.CauseOfDeath
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CharacterOrigin
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.building.Owner
import at.orchaldir.gm.core.model.world.town.TownId

sealed class Event {

    abstract fun getDate(): Date

}

// architectural style

data class ArchitecturalStyleStartEvent(
    val startDate: Date,
    val style: ArchitecturalStyleId,
) : Event() {

    override fun getDate() = startDate

}

data class ArchitecturalStyleEndEvent(
    val endDate: Date,
    val style: ArchitecturalStyleId,
) : Event() {

    override fun getDate() = endDate

}

// building

data class BuildingConstructedEvent(
    val constructionDate: Date,
    val buildingId: BuildingId,
) : Event() {

    override fun getDate() = constructionDate

}

data class BuildingOwnershipChangedEvent(
    val changeDate: Date,
    val buildingId: BuildingId,
    val from: Owner,
    val to: Owner,
) : Event() {

    override fun getDate() = changeDate

}

// character

data class CharacterOriginEvent(
    val day: Day,
    val characterId: CharacterId,
    val origin: CharacterOrigin,
) : Event() {

    override fun getDate() = day

}

data class CharacterDeathEvent(
    val day: Day,
    val characterId: CharacterId,
    val causeOfDeath: CauseOfDeath,
) : Event() {

    override fun getDate() = day

}

// town

data class TownFoundingEvent(
    val foundingDate: Date,
    val townId: TownId,
) : Event() {

    override fun getDate() = foundingDate

}