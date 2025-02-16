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

sealed class Event(
    val date: Date,
)

// architectural style

class ArchitecturalStyleStartEvent(
    date: Date,
    val style: ArchitecturalStyleId,
) : Event(date)

class ArchitecturalStyleEndEvent(
    date: Date,
    val style: ArchitecturalStyleId,
) : Event(date)

// building

class BuildingConstructedEvent(
    date: Date,
    val building: BuildingId,
) : Event(date)

// business

class BusinessStartedEvent(
    date: Date,
    val business: BusinessId,
) : Event(date)

// character

class CharacterOriginEvent(
    date: Date,
    val character: CharacterId,
    val origin: CharacterOrigin,
) : Event(date)

class CharacterDeathEvent(
    date: Date,
    val character: CharacterId,
    val causeOfDeath: CauseOfDeath,
) : Event(date)

// font

class FontCreatedEvent(
    date: Date,
    val font: FontId,
) : Event(date)

// organization

class OrganizationFoundingEvent(
    date: Date,
    val organization: OrganizationId,
) : Event(date)

// ownership

open class OwnershipChangedEvent<ID : Id<ID>>(
    date: Date,
    val id: ID,
    val from: Owner,
    val to: Owner,
) : Event(date)

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

class RaceCreatedEvent(
    date: Date,
    val race: RaceId,
) : Event(date)

// text

class SpellCreatedEvent(
    date: Date,
    val spell: SpellId,
) : Event(date)

// text

class TextPublishedEvent(
    date: Date,
    val text: TextId,
) : Event(date)

// town

class TownFoundingEvent(
    date: Date,
    val town: TownId,
) : Event(date)