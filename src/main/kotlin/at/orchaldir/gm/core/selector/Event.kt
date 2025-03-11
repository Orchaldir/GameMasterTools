package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Dead
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.event.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.Century
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.Decade
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.selector.time.date.*
import at.orchaldir.gm.utils.Id

fun State.getEvents(): List<Event> {
    val events = mutableListOf<Event>()

    getArchitecturalStyleStorage().getAll().forEach { style ->
        if (style.start != null) {
            events.add(ArchitecturalStyleStartEvent(style.start, style.id))
        }

        if (style.end != null) {
            events.add(ArchitecturalStyleEndEvent(style.end, style.id))
        }
    }

    getBuildingStorage().getAll().forEach { building ->
        if (building.constructionDate != null) {
            events.add(BuildingConstructedEvent(building.constructionDate, building.id))
        }

        handleOwnership(events, building.id, building.ownership, ::createOwnershipChanged)
    }

    getBusinessStorage().getAll().forEach { business ->
        business.startDate()?.let {
            events.add(BusinessStartedEvent(it, business.id))
        }

        handleOwnership(events, business.id, business.ownership, ::createOwnershipChanged)
    }

    getCharacterStorage().getAll().forEach { character ->
        events.add(CharacterOriginEvent(character.birthDate, character.id, character.origin))

        if (character.vitalStatus is Dead) {
            events.add(CharacterDeathEvent(character.vitalStatus.deathDay, character.id, character.vitalStatus.cause))
        }
    }

    getFontStorage().getAll().forEach { text ->
        if (text.date != null) {
            events.add(FontCreatedEvent(text.date, text.id))
        }
    }

    getOrganizationStorage().getAll().forEach { organization ->
        organization.startDate()?.let {
            events.add(OrganizationFoundingEvent(it, organization.id))
        }
    }

    getRaceStorage().getAll().forEach { race ->
        race.startDate()?.let {
            events.add(RaceCreatedEvent(it, race.id))
        }
    }

    getSpellStorage().getAll().forEach { spell ->
        if (spell.date != null) {
            events.add(SpellCreatedEvent(spell.date, spell.id))
        }
    }

    getTextStorage().getAll().forEach { text ->
        if (text.date != null) {
            events.add(TextPublishedEvent(text.date, text.id))
        }
    }

    getTownStorage().getAll().forEach { town ->
        events.add(TownFoundingEvent(town.foundingDate, town.id))
    }

    return events
}

private fun <ID : Id<ID>> handleOwnership(
    events: MutableList<Event>,
    id: ID,
    ownership: History<Owner>,
    create: (ID, HistoryEntry<Owner>, Owner) -> OwnershipChangedEvent<ID>,
) {
    var lastPrevious: HistoryEntry<Owner>? = null

    for (previous in ownership.previousEntries) {
        if (lastPrevious != null) {
            events.add(create(id, lastPrevious, previous.entry))
        }

        lastPrevious = previous
    }

    if (lastPrevious != null) {
        events.add(create(id, lastPrevious, ownership.current))
    }
}

private fun createOwnershipChanged(
    id: BuildingId,
    previous: HistoryEntry<Owner>,
    to: Owner,
) = BuildingOwnershipChangedEvent(
    previous.until,
    id,
    previous.entry,
    to,
)

private fun createOwnershipChanged(
    id: BusinessId,
    previous: HistoryEntry<Owner>,
    to: Owner,
) = BusinessOwnershipChangedEvent(
    previous.until,
    id,
    previous.entry,
    to,
)

fun State.getEventsOfMonth(calendarId: CalendarId, day: Day): List<Event> {
    val calendar = getCalendarStorage().getOrThrow(calendarId)
    val start = calendar.getStartOfMonth(day)
    val end = calendar.getEndOfMonth(day)

    return getEvents().filter { it.date.isBetween(calendar, start, end) }
}

fun State.getEventsOfYear(calendarId: CalendarId, year: Year): List<Event> {
    val calendar = getCalendarStorage().getOrThrow(calendarId)
    val start = calendar.getStartDayOfYear(year)
    val end = calendar.getEndDayOfYear(year)

    return getEvents().filter {
        it.date.isBetween(calendar, start, end)
    }
}

fun State.getEventsOfDecade(calendarId: CalendarId, decade: Decade): List<Event> {
    val calendar = getCalendarStorage().getOrThrow(calendarId)
    val start = calendar.getStartDayOfDecade(decade)
    val end = calendar.getEndDayOfDecade(decade)

    return getEvents().filter {
        it.date.isBetween(calendar, start, end)
    }
}

fun State.getEventsOfCentury(calendarId: CalendarId, century: Century): List<Event> {
    val calendar = getCalendarStorage().getOrThrow(calendarId)
    val start = calendar.getStartDayOfCentury(century)
    val end = calendar.getEndDayOfCentury(century)

    return getEvents().filter {
        it.date.isBetween(calendar, start, end)
    }
}

fun List<Event>.sort(calendar: Calendar): List<Event> {
    val daysPerYear = calendar.getDaysPerYear()

    return sortedBy {
        when (val date = it.date) {
            is Day -> date.day
            is Year -> {
                date.year * daysPerYear
            }

            is Decade -> {
                date.decade * daysPerYear * 10
            }

            is Century -> {
                date.century * daysPerYear * 100
            }
        }
    }
}

