package at.orchaldir.gm.core.model.event

import at.orchaldir.gm.core.model.character.CauseOfDeath
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CharacterOrigin
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.utils.Id

sealed class Event {

    abstract fun date(): Date

}

// architectural style

data class ArchitecturalStyleStartEvent(
    val startDate: Date,
    val style: ArchitecturalStyleId,
) : Event() {

    override fun date() = startDate

}

data class ArchitecturalStyleEndEvent(
    val endDate: Date,
    val style: ArchitecturalStyleId,
) : Event() {

    override fun date() = endDate

}

// building

data class BuildingConstructedEvent(
    val constructionDate: Date,
    val buildingId: BuildingId,
) : Event() {

    override fun date() = constructionDate

}

// business

data class BusinessStartedEvent(
    val startDate: Date,
    val businessId: BusinessId,
) : Event() {

    override fun date() = startDate

}

// character

data class CharacterOriginEvent(
    val day: Date,
    val characterId: CharacterId,
    val origin: CharacterOrigin,
) : Event() {

    override fun date() = day

}

data class CharacterDeathEvent(
    val day: Date,
    val characterId: CharacterId,
    val causeOfDeath: CauseOfDeath,
) : Event() {

    override fun date() = day

}

// font

data class FontCreatedEvent(
    val creationDate: Date,
    val fontId: FontId,
) : Event() {

    override fun date() = creationDate

}

// organization

data class OrganizationFoundingEvent(
    val creationDate: Date,
    val organizationId: OrganizationId,
) : Event() {

    override fun date() = creationDate

}

// ownership

open class OwnershipChangedEvent<ID : Id<ID>>(
    val changeDate: Date,
    val id: ID,
    val from: Owner,
    val to: Owner,
) : Event() {

    override fun date() = changeDate

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

// text

data class SpellCreatedEvent(
    val date: Date,
    val spellId: SpellId,
) : Event() {

    override fun date() = date

}

// text

data class TextPublishedEvent(
    val publishingDate: Date,
    val textId: TextId,
) : Event() {

    override fun date() = publishingDate

}

// town

data class TownFoundingEvent(
    val foundingDate: Date,
    val townId: TownId,
) : Event() {

    override fun date() = foundingDate

}