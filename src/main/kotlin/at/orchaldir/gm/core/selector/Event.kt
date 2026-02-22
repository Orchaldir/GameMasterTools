package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasStartAndEndDate
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.model.util.event.*
import at.orchaldir.gm.core.selector.time.date.convertDate
import at.orchaldir.gm.core.selector.time.date.createSorter
import at.orchaldir.gm.core.selector.time.date.getEndDay
import at.orchaldir.gm.core.selector.time.date.getStartDay
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id

fun State.getEvents(calendar: Calendar): List<Event<*>> {
    val events = mutableListOf<Event<*>>()
    val default = getDefaultCalendar()

    getArchitecturalStyleStorage().getAll().forEach { style ->
        handleStartAndEnd(events, default, calendar, style)
    }

    getBattleStorage().getAll().forEach { battle ->
        addPossibleEvent(events, default, calendar, battle.date) {
            StartEvent(it, battle.id)
        }
    }

    getBuildingStorage().getAll().forEach { building ->
        addPossibleEvent(events, default, calendar, building.constructionDate) {
            StartEvent(it, building.id)
        }

        addHistoricEvents(events, default, calendar, building.id, building.ownership, HistoryEventType.Ownership)
    }

    getBusinessStorage().getAll().forEach { business ->
        handleStartAndEnd(events, default, calendar, business)

        addHistoricEvents(events, default, calendar, business.id, business.ownership, HistoryEventType.Ownership)
    }

    getCatastropheStorage().getAll().forEach { catastrophe ->
        handleStartAndEnd(events, default, calendar, catastrophe)
    }

    getCharacterStorage().getAll().forEach { character ->
        handleStartAndEnd(events, default, calendar, character)

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
        handleStartAndEnd(events, default, calendar, currency)
    }

    getFontStorage().getAll().forEach { font ->
        addPossibleEvent(events, default, calendar, font.startDate(this)) {
            StartEvent(it, font.id)
        }
    }

    getGodStorage().getAll().forEach { god ->
        handleStartAndEnd(events, default, calendar, god)
    }

    getLegalCodeStorage().getAll().forEach { code ->
        addPossibleEvent(events, default, calendar, code.startDate(this)) {
            StartEvent(it, code.id)
        }
    }

    getMagicTraditionStorage().getAll().forEach { tradition ->
        addPossibleEvent(events, default, calendar, tradition.startDate(this)) {
            StartEvent(it, tradition.id)
        }
    }

    getMoonStorage().getAll().forEach { god ->
        handleStartAndEnd(events, default, calendar, god)
    }

    getOrganizationStorage().getAll().forEach { organization ->
        handleStartAndEnd(events, default, calendar, organization)
    }

    getPeriodicalStorage().getAll().forEach { periodical ->
        val periodicalCalendar = getCalendarStorage().getOrThrow(periodical.calendar)

        addPossibleEvent(events, periodicalCalendar, calendar, periodical.date) {
            StartEvent(it, periodical.id)
        }

        addHistoricEvents(events, default, calendar, periodical.id, periodical.ownership, HistoryEventType.Ownership)
    }

    getRaceStorage().getAll().forEach { race ->
        addPossibleEvent(events, default, calendar, race.startDate(this)) {
            StartEvent(it, race.id)
        }
    }

    getRealmStorage().getAll().forEach { realm ->
        handleStartAndEnd(events, default, calendar, realm)

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

    getSettlementStorage().getAll().forEach { town ->
        handleStartAndEnd(events, default, calendar, town)
    }

    getTreatyStorage().getAll().forEach { treaty ->
        addPossibleEvent(events, default, calendar, treaty.startDate(this)) {
            StartEvent(it, treaty.id)
        }
    }

    getWarStorage().getAll().forEach { war ->
        handleStartAndEnd(events, default, calendar, war)
    }

    return events
}

private fun <ID : Id<ID>, T> State.handleStartAndEnd(
    events: MutableList<Event<*>>,
    from: Calendar,
    to: Calendar,
    element: T,
) where
        T : HasStartAndEndDate,
        T : Element<ID> {
    val id = element.id()
    val startDate = element.startDate(this)
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

