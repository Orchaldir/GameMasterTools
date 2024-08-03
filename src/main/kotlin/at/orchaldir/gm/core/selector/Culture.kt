package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.language.LanguageId

fun State.canDelete(culture: CultureId) = getCharacters(culture).isEmpty()

fun State.getCultures(fashion: FashionId) = cultures.getAll()
    .filter { it.clothingStyles.contains(fashion) }

fun State.getCultures(calendar: CalendarId) = cultures.getAll()
    .filter { it.calendar == calendar }

fun State.getCultures(language: LanguageId) = cultures.getAll()
    .filter { it.languages.isAvailable(language) }
