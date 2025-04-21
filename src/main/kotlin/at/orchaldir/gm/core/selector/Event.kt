package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Dead
import at.orchaldir.gm.core.model.event.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.convertDate
import at.orchaldir.gm.core.selector.time.date.createSorter
import at.orchaldir.gm.core.selector.time.date.getEndDay
import at.orchaldir.gm.core.selector.time.date.getStartDay
import at.orchaldir.gm.utils.Id

fun State.getEvents(calendar: Calendar): List<Event> {
    val events = mutableListOf<Event>()
    val default = getDefaultCalendar()

    getArchitecturalStyleStorage().getAll().forEach { style ->
        addPossibleEvent(events, default, calendar, style.start) {
            ArchitecturalStyleStartEvent(it, style.id)
        }

        addPossibleEvent(events, default, calendar, style.end) {
            ArchitecturalStyleEndEvent(it, style.id)
        }
    }

    getBuildingStorage().getAll().forEach { building ->
        addPossibleEvent(events, default, calendar, building.constructionDate) {
            BuildingConstructedEvent(it, building.id)
        }

        addOwnershipEvents(events, default, calendar, building.id, building.ownership)
    }

    getBusinessStorage().getAll().forEach { business ->
        addPossibleEvent(events, default, calendar, business.startDate()) {
            BusinessStartedEvent(it, business.id)
        }

        addOwnershipEvents(events, default, calendar, business.id, business.ownership)
    }

    getCharacterStorage().getAll().forEach { character ->
        addEvent(events, default, calendar, character.birthDate) {
            CharacterOriginEvent(it, character.id, character.origin)
        }

        if (character.vitalStatus is Dead) {
            addEvent(events, default, calendar, character.birthDate) {
                CharacterDeathEvent(it, character.id, character.vitalStatus.cause)
            }
        }
    }

    getFontStorage().getAll().forEach { font ->
        addPossibleEvent(events, default, calendar, font.startDate()) {
            FontCreatedEvent(it, font.id)
        }
    }

    getPeriodicalStorage().getAll().forEach { periodical ->
        val periodicalCalendar = getCalendarStorage().getOrThrow(periodical.calendar)

        addEvent(events, periodicalCalendar, calendar, periodical.frequency.getStartDate()) {
            PeriodicalCreatedEvent(it, periodical.id)
        }

        addOwnershipEvents(events, default, calendar, periodical.id, periodical.ownership)
    }

    getOrganizationStorage().getAll().forEach { organization ->
        addPossibleEvent(events, default, calendar, organization.startDate()) {
            OrganizationFoundingEvent(it, organization.id)
        }
    }

    getRaceStorage().getAll().forEach { race ->
        addPossibleEvent(events, default, calendar, race.startDate()) {
            RaceCreatedEvent(it, race.id)
        }
    }

    getSpellStorage().getAll().forEach { spell ->
        addPossibleEvent(events, default, calendar, spell.date) {
            SpellCreatedEvent(it, spell.id)
        }
    }

    getTextStorage().getAll().forEach { text ->
        addPossibleEvent(events, default, calendar, text.date) {
            TextPublishedEvent(it, text.id)
        }
    }

    getTownStorage().getAll().forEach { town ->
        addEvent(events, default, calendar, town.foundingDate) {
            TownFoundingEvent(it, town.id)
        }
    }

    return events
}

private fun addPossibleEvent(
    events: MutableList<Event>,
    from: Calendar,
    to: Calendar,
    date: Date?,
    create: (Date) -> Event,
) {
    if (date != null) {
        val convertedDate = convertDate(from, to, date)
        events.add(create(convertedDate))
    }
}

private fun addEvent(
    events: MutableList<Event>,
    from: Calendar,
    to: Calendar,
    date: Date,
    create: (Date) -> Event,
) {
    val convertedDate = convertDate(from, to, date)
    events.add(create(convertedDate))
}

private fun <ID : Id<ID>> addOwnershipEvents(
    events: MutableList<Event>,
    from: Calendar,
    to: Calendar,
    id: ID,
    ownership: History<Owner>,
) {
    var lastPrevious: HistoryEntry<Owner>? = null

    for (previous in ownership.previousEntries) {
        addOwnershipEvent(events, from, to, id, lastPrevious, previous.entry)

        lastPrevious = previous
    }

    addOwnershipEvent(events, from, to, id, lastPrevious, ownership.current)
}

private fun <ID : Id<ID>> addOwnershipEvent(
    events: MutableList<Event>,
    from: Calendar,
    to: Calendar,
    id: ID,
    entry: HistoryEntry<Owner>?,
    owner: Owner,
) {
    if (entry != null) {
        addEvent(events, from, to, entry.until) {
            OwnershipChangedEvent(
                it,
                id,
                entry.entry,
                owner,
            )
        }
    }
}

fun State.getEvents(calendar: Calendar, date: Date): List<Event> {
    val start = calendar.getStartDay(date)
    val end = calendar.getEndDay(date)

    return getEvents(calendar).filter {
        it.date.isBetween(calendar, start, end)
    }
}

fun List<Event>.sort(calendar: Calendar): List<Event> {
    return sortedBy {
        calendar.createSorter()(it.date)
    }
}

