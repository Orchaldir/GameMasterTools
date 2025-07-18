package at.orchaldir.gm.core.selector.item.periodical

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.periodical.PublicationFrequency
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId

fun State.canDeletePeriodical(periodical: PeriodicalId) = countPeriodicalIssues(periodical) == 0

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
): List<PublicationFrequency> {
    val calendar = getCalendarStorage().getOrThrow(calendarId)

    return getValidPublicationFrequencies(calendar)
}

fun getValidPublicationFrequencies(
    calendar: Calendar,
) = if (calendar.days.hasWeeks()) {
    PublicationFrequency.entries
} else {
    PublicationFrequency.entries - PublicationFrequency.Weekly
}