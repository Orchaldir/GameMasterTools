package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Dead
import at.orchaldir.gm.core.model.event.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.convertDate
import at.orchaldir.gm.core.selector.time.date.createSorter
import at.orchaldir.gm.core.selector.time.date.getEndDay
import at.orchaldir.gm.core.selector.time.date.getStartDay
import at.orchaldir.gm.utils.Id

fun State.getEvents(calendar: Calendar): List<Event<*>> {
    val events = mutableListOf<Event<*>>()
    val default = getDefaultCalendar()

    getArchitecturalStyleStorage().getAll().forEach { style ->
        addPossibleEvent(events, default, calendar, style.start) {
            StartEvent(it, style.id)
        }

        addPossibleEvent(events, default, calendar, style.end) {
            EndEvent(it, style.id)
        }
    }

    getBuildingStorage().getAll().forEach { building ->
        addPossibleEvent(events, default, calendar, building.constructionDate) {
            StartEvent(it, building.id)
        }

        addOwnershipEvents(events, default, calendar, building.id, building.ownership)
    }

    getBusinessStorage().getAll().forEach { business ->
        addPossibleEvent(events, default, calendar, business.startDate()) {
            StartEvent(it, business.id)
        }

        addOwnershipEvents(events, default, calendar, business.id, business.ownership)
    }

    getCharacterStorage().getAll().forEach { character ->
        addEvent(events, default, calendar, character.birthDate) {
            StartEvent(it, character.id)
        }

        if (character.vitalStatus is Dead) {
            addEvent(events, default, calendar, character.birthDate) {
                EndEvent(it, character.id)
            }
        }
    }

    getCurrencyStorage().getAll().forEach { currency ->
        addPossibleEvent(events, default, calendar, currency.startDate) {
            StartEvent(it, currency.id)
        }

        addPossibleEvent(events, default, calendar, currency.endDate) {
            EndEvent(it, currency.id)
        }
    }

    getFontStorage().getAll().forEach { font ->
        addPossibleEvent(events, default, calendar, font.startDate()) {
            StartEvent(it, font.id)
        }
    }

    getPeriodicalStorage().getAll().forEach { periodical ->
        val periodicalCalendar = getCalendarStorage().getOrThrow(periodical.calendar)

        addPossibleEvent(events, periodicalCalendar, calendar, periodical.date) {
            StartEvent(it, periodical.id)
        }

        addOwnershipEvents(events, default, calendar, periodical.id, periodical.ownership)
    }

    getOrganizationStorage().getAll().forEach { organization ->
        addPossibleEvent(events, default, calendar, organization.startDate()) {
            StartEvent(it, organization.id)
        }
    }

    getRaceStorage().getAll().forEach { race ->
        addPossibleEvent(events, default, calendar, race.startDate()) {
            StartEvent(it, race.id)
        }
    }

    getSpellStorage().getAll().forEach { spell ->
        addPossibleEvent(events, default, calendar, spell.date) {
            StartEvent(it, spell.id)
        }
    }

    getTextStorage().getAll().forEach { text ->
        addPossibleEvent(events, default, calendar, text.date) {
            StartEvent(it, text.id)
        }
    }

    getTownStorage().getAll().forEach { town ->
        addEvent(events, default, calendar, town.foundingDate) {
            StartEvent(it, town.id)
        }
    }

    return events
}

private fun addPossibleEvent(
    events: MutableList<Event<*>>,
    from: Calendar,
    to: Calendar,
    date: Date?,
    create: (Date) -> Event<*>,
) {
    if (date != null) {
        val convertedDate = convertDate(from, to, date)
        events.add(create(convertedDate))
    }
}

private fun addEvent(
    events: MutableList<Event<*>>,
    from: Calendar,
    to: Calendar,
    date: Date,
    create: (Date) -> Event<*>,
) {
    val convertedDate = convertDate(from, to, date)
    events.add(create(convertedDate))
}

private fun <ID : Id<ID>> addOwnershipEvents(
    events: MutableList<Event<*>>,
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
    events: MutableList<Event<*>>,
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

fun State.getEvents(calendar: Calendar, date: Date): List<Event<*>> {
    val start = calendar.getStartDay(date)
    val end = calendar.getEndDay(date)

    return getEvents(calendar).filter {
        it.date.isOverlapping(calendar, start, end)
    }
}

fun List<Event<*>>.sort(calendar: Calendar): List<Event<*>> {
    return sortedBy {
        calendar.createSorter()(it.date)
    }
}

