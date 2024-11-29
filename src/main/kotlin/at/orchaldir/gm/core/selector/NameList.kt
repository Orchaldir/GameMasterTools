package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.name.NameListId

fun State.canDelete(nameList: NameListId) = getCultures(nameList).isEmpty()

fun State.getCultures(nameList: NameListId) = getCultureStorage().getAll()
    .filter { it.namingConvention.contains(nameList) }
