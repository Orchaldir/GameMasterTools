package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.selector.culture.getCultures

fun State.canDelete(nameList: NameListId) = getCultures(nameList).isEmpty()

