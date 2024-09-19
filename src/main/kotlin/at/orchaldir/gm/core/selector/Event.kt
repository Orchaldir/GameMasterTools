package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.Dead
import at.orchaldir.gm.core.model.event.CharacterDeathEvent
import at.orchaldir.gm.core.model.event.CharacterOriginEvent
import at.orchaldir.gm.core.model.event.Event
import at.orchaldir.gm.core.model.event.TownFoundingEvent
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Year

fun State.getEvents(): List<Event> {
    val events = mutableListOf<Event>()

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

fun State.getEventsOfMonth(calendarId: CalendarId, day: Day): List<Event> {
    val calendar = getCalendarStorage().getOrThrow(calendarId)
    val start = calendar.getStartOfMonth(day)
    val end = calendar.getEndOfMonth(day)

    return getEvents().filter { it.getDate().isBetween(start, end) }
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

