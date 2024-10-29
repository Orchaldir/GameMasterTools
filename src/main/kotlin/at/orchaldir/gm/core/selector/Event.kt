package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.Dead
import at.orchaldir.gm.core.model.event.*
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.model.util.Ownership
import at.orchaldir.gm.core.model.util.PreviousOwner
import at.orchaldir.gm.core.model.world.building.BuildingId

fun State.getEvents(): List<Event> {
    val events = mutableListOf<Event>()

    getArchitecturalStyleStorage().getAll().forEach { style ->
        events.add(ArchitecturalStyleStartEvent(style.start, style.id))

        if (style.end != null) {
            events.add(ArchitecturalStyleEndEvent(style.end, style.id))
        }
    }

    getBuildingStorage().getAll().forEach { building ->
        events.add(BuildingConstructedEvent(building.constructionDate, building.id))

        handleOwnership(events, building.id, building.ownership, ::createOwnershipChanged)
    }

    getCharacterStorage().getAll().forEach { character ->
        events.add(CharacterOriginEvent(character.birthDate, character.id, character.origin))

        if (character.vitalStatus is Dead) {
            events.add(CharacterDeathEvent(character.vitalStatus.deathDay, character.id, character.vitalStatus.cause))
        }
    }

    getTownStorage().getAll().forEach { town ->
        events.add(TownFoundingEvent(town.foundingDate, town.id))
    }

    return events
}

private fun handleOwnership(
    events: MutableList<Event>,
    id: BuildingId,
    ownership: Ownership,
    create: (BuildingId, PreviousOwner, Owner) -> BuildingOwnershipChangedEvent,
) {
    var lastPrevious: PreviousOwner? = null

    for (previous in ownership.previousOwners) {
        if (lastPrevious != null) {
            events.add(create(id, lastPrevious, previous.owner))
        }

        lastPrevious = previous
    }

    if (lastPrevious != null) {
        events.add(create(id, lastPrevious, ownership.owner))
    }
}

private fun createOwnershipChanged(
    id: BuildingId,
    previous: PreviousOwner,
    to: Owner,
) = BuildingOwnershipChangedEvent(
    previous.until,
    id,
    previous.owner,
    to,
)

fun State.getEventsOfMonth(calendarId: CalendarId, day: Day): List<Event> {
    val calendar = getCalendarStorage().getOrThrow(calendarId)
    val start = calendar.getStartOfMonth(day)
    val end = calendar.getEndOfMonth(day)

    return getEvents().filter { it.getDate().isBetween(calendar, start, end) }
}

fun State.getEventsOfYear(calendarId: CalendarId, year: Year): List<Event> {
    val calendar = getCalendarStorage().getOrThrow(calendarId)
    val start = calendar.getStartOfYear(year)
    val end = calendar.getEndOfYear(year)

    return getEvents().filter {
        it.getDate().isBetween(calendar, start, end)
    }
}

fun List<Event>.sort(calendar: Calendar): List<Event> {
    val daysPerYear = calendar.getDaysPerYear()

    return sortedBy {
        when (val date = it.getDate()) {
            is Day -> date.day
            is Year -> {
                date.year * daysPerYear
            }
        }
    }
}

