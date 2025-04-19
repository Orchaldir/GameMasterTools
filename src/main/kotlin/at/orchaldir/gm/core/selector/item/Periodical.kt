package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.periodical.PublicationFrequencyType
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId

fun State.canDeletePeriodical(text: PeriodicalId) = true

fun State.countPeriodicals(calendar: CalendarId) = getPeriodicalStorage()
    .getAll()
    .count { b -> b.calendar == calendar }

fun State.countPeriodicals(language: LanguageId) = getPeriodicalStorage()
    .getAll()
    .count { b -> b.language == language }

fun countPublicationFrequencies(collection: Collection<Periodical>) = collection
    .groupingBy { it.frequency }
    .eachCount()

fun State.getPeriodicals(calendar: CalendarId) = getPeriodicalStorage()
    .getAll()
    .filter { b -> b.calendar == calendar }

fun State.getPeriodicals(language: LanguageId) = getPeriodicalStorage()
    .getAll()
    .filter { b -> b.language == language }

fun State.getValidPublicationFrequencies(
    calendarId: CalendarId,
): List<PublicationFrequencyType> {
    val calendar = getCalendarStorage().getOrThrow(calendarId)

    return getValidPublicationFrequencies(calendar)
}

fun getValidPublicationFrequencies(
    calendar: Calendar,
) = if (calendar.days.hasWeeks()) {
    PublicationFrequencyType.entries
} else {
    PublicationFrequencyType.entries - PublicationFrequencyType.Weekly
}