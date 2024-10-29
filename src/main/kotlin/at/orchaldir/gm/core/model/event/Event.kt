package at.orchaldir.gm.core.model.event

import at.orchaldir.gm.core.model.character.CauseOfDeath
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CharacterOrigin
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.utils.Id

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

// business

data class BusinessStartedEvent(
    val startDate: Date,
    val businessId: BusinessId,
) : Event() {

    override fun getDate() = startDate

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

// ownership

open class OwnershipChangedEvent<ID : Id<ID>>(
    val changeDate: Date,
    val id: ID,
    val from: Owner,
    val to: Owner,
) : Event() {

    override fun getDate() = changeDate

}

class BuildingOwnershipChangedEvent(
    changeDate: Date,
    id: BuildingId,
    from: Owner,
    to: Owner,
) : OwnershipChangedEvent<BuildingId>(changeDate, id, from, to)

class BusinessOwnershipChangedEvent(
    changeDate: Date,
    id: BusinessId,
    from: Owner,
    to: Owner,
) : OwnershipChangedEvent<BusinessId>(changeDate, id, from, to)

// town

data class TownFoundingEvent(
    val foundingDate: Date,
    val townId: TownId,
) : Event() {

    override fun getDate() = foundingDate

}