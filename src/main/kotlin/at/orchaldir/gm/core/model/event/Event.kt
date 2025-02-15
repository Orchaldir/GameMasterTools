package at.orchaldir.gm.core.model.event

import at.orchaldir.gm.core.model.character.CauseOfDeath
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CharacterOrigin
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.race.RaceId
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
    val date: Date,
    val style: ArchitecturalStyleId,
) : Event() {

    override fun date() = date

}

data class ArchitecturalStyleEndEvent(
    val date: Date,
    val style: ArchitecturalStyleId,
) : Event() {

    override fun date() = date

}

// building

data class BuildingConstructedEvent(
    val date: Date,
    val buildingId: BuildingId,
) : Event() {

    override fun date() = date

}

// business

data class BusinessStartedEvent(
    val date: Date,
    val businessId: BusinessId,
) : Event() {

    override fun date() = date

}

// character

data class CharacterOriginEvent(
    val date: Date,
    val characterId: CharacterId,
    val origin: CharacterOrigin,
) : Event() {

    override fun date() = date

}

data class CharacterDeathEvent(
    val date: Date,
    val characterId: CharacterId,
    val causeOfDeath: CauseOfDeath,
) : Event() {

    override fun date() = date

}

// font

data class FontCreatedEvent(
    val date: Date,
    val fontId: FontId,
) : Event() {

    override fun date() = date

}

// organization

data class OrganizationFoundingEvent(
    val date: Date,
    val organizationId: OrganizationId,
) : Event() {

    override fun date() = date

}

// ownership

open class OwnershipChangedEvent<ID : Id<ID>>(
    val date: Date,
    val id: ID,
    val from: Owner,
    val to: Owner,
) : Event() {

    override fun date() = date

}

class BuildingOwnershipChangedEvent(
    date: Date,
    id: BuildingId,
    from: Owner,
    to: Owner,
) : OwnershipChangedEvent<BuildingId>(date, id, from, to)

class BusinessOwnershipChangedEvent(
    date: Date,
    id: BusinessId,
    from: Owner,
    to: Owner,
) : OwnershipChangedEvent<BusinessId>(date, id, from, to)

// race

data class RaceCreatedEvent(
    val date: Date,
    val race: RaceId,
) : Event() {

    override fun date() = date

}

// text

data class SpellCreatedEvent(
    val date: Date,
    val spellId: SpellId,
) : Event() {

    override fun date() = date

}

// text

data class TextPublishedEvent(
    val date: Date,
    val textId: TextId,
) : Event() {

    override fun date() = date

}

// town

data class TownFoundingEvent(
    val date: Date,
    val townId: TownId,
) : Event() {

    override fun date() = date

}