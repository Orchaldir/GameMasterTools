package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.event.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasStartAndEndDate
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
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
        handleStartAndEnd(events, default, calendar, style, style.id)
    }

    getBuildingStorage().getAll().forEach { building ->
        addPossibleEvent(events, default, calendar, building.constructionDate) {
            StartEvent(it, building.id)
        }

        addHistoricEvents(events, default, calendar, building.id, building.ownership, HistoryEventType.Ownership)
    }

    getBusinessStorage().getAll().forEach { business ->
        addPossibleEvent(events, default, calendar, business.startDate()) {
            StartEvent(it, business.id)
        }

        addHistoricEvents(events, default, calendar, business.id, business.ownership, HistoryEventType.Ownership)
    }

    getCatastropheStorage().getAll().forEach { catastrophe ->
        handleStartAndEnd(events, default, calendar, catastrophe, catastrophe.id)
    }

    getCharacterStorage().getAll().forEach { character ->
        handleStartAndEnd(events, default, calendar, character, character.id)

        addHistoricEvents(
            events,
            default,
            calendar,
            character.id,
            character.employmentStatus,
            HistoryEventType.Employment
        )
    }

    getCurrencyStorage().getAll().forEach { currency ->
        handleStartAndEnd(events, default, calendar, currency, currency.id)
    }

    getFontStorage().getAll().forEach { font ->
        addPossibleEvent(events, default, calendar, font.startDate()) {
            StartEvent(it, font.id)
        }
    }

    getLegalCodeStorage().getAll().forEach { code ->
        addPossibleEvent(events, default, calendar, code.startDate()) {
            StartEvent(it, code.id)
        }
    }

    getMagicTraditionStorage().getAll().forEach { tradition ->
        addPossibleEvent(events, default, calendar, tradition.startDate()) {
            StartEvent(it, tradition.id)
        }
    }

    getOrganizationStorage().getAll().forEach { organization ->
        addPossibleEvent(events, default, calendar, organization.startDate()) {
            StartEvent(it, organization.id)
        }
    }

    getPeriodicalStorage().getAll().forEach { periodical ->
        val periodicalCalendar = getCalendarStorage().getOrThrow(periodical.calendar)

        addPossibleEvent(events, periodicalCalendar, calendar, periodical.date) {
            StartEvent(it, periodical.id)
        }

        addHistoricEvents(events, default, calendar, periodical.id, periodical.ownership, HistoryEventType.Ownership)
    }

    getRaceStorage().getAll().forEach { race ->
        addPossibleEvent(events, default, calendar, race.startDate()) {
            StartEvent(it, race.id)
        }
    }

    getRealmStorage().getAll().forEach { realm ->
        addPossibleEvent(events, default, calendar, realm.startDate()) {
            StartEvent(it, realm.id)
        }

        addHistoricEvents(events, default, calendar, realm.id, realm.capital, HistoryEventType.Capital)
        addHistoricEvents(events, default, calendar, realm.id, realm.currency, HistoryEventType.Currency)
        addHistoricEvents(events, default, calendar, realm.id, realm.legalCode, HistoryEventType.LegalCode)
        addHistoricEvents(events, default, calendar, realm.id, realm.owner, HistoryEventType.OwnerRealm)
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
        addPossibleEvent(events, default, calendar, town.foundingDate) {
            StartEvent(it, town.id)
        }
    }

    getTreatyStorage().getAll().forEach { treaty ->
        addPossibleEvent(events, default, calendar, treaty.startDate()) {
            StartEvent(it, treaty.id)
        }
    }

    getWarStorage().getAll().forEach { war ->
        handleStartAndEnd(events, default, calendar, war, war.id)
    }

    return events
}

private fun <ID : Id<ID>, T : HasStartAndEndDate> handleStartAndEnd(
    events: MutableList<Event<*>>,
    from: Calendar,
    to: Calendar,
    element: T,
    id: ID,
) {
    val startDate = element.startDate()
    val endDate = element.endDate()

    if (startDate == endDate) {
        addPossibleEvent(events, from, to, startDate) {
            SameStartAndEndEvent(it, id)
        }
    } else {
        addPossibleEvent(events, from, to, startDate) {
            StartEvent(it, id)
        }

        addPossibleEvent(events, from, to, endDate) {
            EndEvent(it, id)
        }
    }
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

private fun <ID : Id<ID>, T> addHistoricEvents(
    events: MutableList<Event<*>>,
    from: Calendar,
    to: Calendar,
    id: ID,
    history: History<T>,
    type: HistoryEventType,
) {
    var lastPrevious: HistoryEntry<T>? = null

    for (previous in history.previousEntries) {
        addHistoricEvent(events, from, to, id, lastPrevious, previous.entry, type)

        lastPrevious = previous
    }

    addHistoricEvent(events, from, to, id, lastPrevious, history.current, type)
}

private fun <ID : Id<ID>, T> addHistoricEvent(
    events: MutableList<Event<*>>,
    from: Calendar,
    to: Calendar,
    id: ID,
    entry: HistoryEntry<T>?,
    owner: T,
    type: HistoryEventType,
) {
    if (entry != null) {
        addEvent(events, from, to, entry.until) {
            HistoryEvent(
                it,
                id,
                type,
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

