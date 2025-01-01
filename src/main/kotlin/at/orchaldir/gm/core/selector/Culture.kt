package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.language.LanguageId

fun State.canDelete(culture: CultureId) = getCharacters(culture).isEmpty()

fun State.countCultures(language: LanguageId) = getCultureStorage()
    .getAll()
    .count { it.languages.isAvailable(language) }

fun State.getCultures(fashion: FashionId) = getCultureStorage().getAll()
    .filter { it.clothingStyles.contains(fashion) }

fun State.getCultures(calendar: CalendarId) = getCultureStorage().getAll()
    .filter { it.calendar == calendar }

fun State.getCultures(holiday: HolidayId) = getCultureStorage().getAll()
    .filter { it.holidays.contains(holiday) }

fun State.getCultures(language: LanguageId) = getCultureStorage().getAll()
    .filter { it.languages.isAvailable(language) }
