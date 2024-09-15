package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.Dead
import at.orchaldir.gm.core.model.event.CharacterDeathEvent
import at.orchaldir.gm.core.model.event.CharacterOriginEvent
import at.orchaldir.gm.core.model.event.Event
import at.orchaldir.gm.core.model.time.Day

fun State.getEvents(): List<Event> {
    val events = mutableListOf<Event>()

    getCharacterStorage().getAll().forEach { character ->
        events.add(CharacterOriginEvent(character.birthDate, character.id, character.origin))

        if (character.vitalStatus is Dead) {
            events.add(CharacterDeathEvent(character.vitalStatus.deathDay, character.id, character.vitalStatus.cause))
        }
    }

    return events
}

fun State.getEventsOfMonth(calendarId: CalendarId, day: Day): List<Event> {
    val calendar = getCalendarStorage().getOrThrow(calendarId)
    val start = calendar.getStartOfMonth(day)
    val end = calendar.getEndOfMonth(day)

    return getEvents().filter { it.getEventDay().isBetween(start, end) }
}

fun List<Event>.sort() = sortedBy { it.getEventDay().day }

