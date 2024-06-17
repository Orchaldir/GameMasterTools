package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.name.*

fun State.canDelete(nameList: NameListId) = getCultures(nameList).isEmpty()

fun State.getCultures(nameList: NameListId) = cultures.getAll()
    .filter { it.namingConvention.contains(nameList) }
