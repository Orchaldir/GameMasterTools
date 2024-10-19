package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.Dead
import at.orchaldir.gm.core.model.event.*
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.Owner
import at.orchaldir.gm.core.model.world.building.PreviousOwner

fun State.getEvents(): List<Event> {
    val events = mutableListOf<Event>()

    getArchitecturalStyleStorage().getAll().forEach { style ->
        events.add(ArchitecturalStyleStartEvent(style.startDate, style.id))

        if (style.endDate != null) {
            events.add(ArchitecturalStyleEndEvent(style.endDate, style.id))
        }
    }

    getBuildingStorage().getAll().forEach { building ->
        events.add(BuildingConstructedEvent(building.constructionDate, building.id))

        var lastPrevious: PreviousOwner? = null

        for (previous in building.ownership.previousOwners) {
            if (lastPrevious != null) {
                events.add(createOwnershipChanged(building, lastPrevious, previous.owner))
            }

            lastPrevious = previous
        }

        if (lastPrevious != null) {
            events.add(createOwnershipChanged(building, lastPrevious, building.ownership.owner))
        }
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

private fun createOwnershipChanged(
    building: Building,
    lastPrevious: PreviousOwner,
    to: Owner,
) = BuildingOwnershipChangedEvent(
    lastPrevious.until,
    building.id,
    lastPrevious.owner,
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

