package at.orchaldir.gm.core.selector.culture

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.selector.character.getCharacters

fun State.canDelete(culture: CultureId) = getCharacters(culture).isEmpty()

fun State.countCultures(language: LanguageId) = getCultureStorage()
    .getAll()
    .count { it.languages.isAvailable(language) }

fun State.getCultures(calendar: CalendarId) = getCultureStorage().getAll()
    .filter { it.calendar == calendar }

fun State.getCultures(fashion: FashionId) = getCultureStorage().getAll()
    .filter { it.fashion.contains(fashion) }

fun State.getCultures(holiday: HolidayId) = getCultureStorage().getAll()
    .filter { it.holidays.contains(holiday) }

fun State.getCultures(language: LanguageId) = getCultureStorage().getAll()
    .filter { it.languages.isAvailable(language) }

fun State.getCultures(nameList: NameListId) = getCultureStorage().getAll()
    .filter { it.namingConvention.contains(nameList) }
