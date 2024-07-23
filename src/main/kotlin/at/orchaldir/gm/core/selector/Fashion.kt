package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.fashion.FashionId

fun State.canDelete(fashion: FashionId) = getCultures(fashion).isEmpty()

fun State.getCultures(fashion: FashionId) = cultures.getAll()
    .filter { it.clothingStyles.contains(fashion) }
