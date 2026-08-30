package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.ColorSchemeGroupId
import at.orchaldir.gm.core.model.util.render.ColorSchemeId


fun State.canDeleteColorSchemeGroup(id: ColorSchemeGroupId) = DeleteResult(id)

fun State.getColorSchemeGroups(id: ColorSchemeId) = getColorSchemeGroupStorage()
    .getAll()
    .filter { it.schemes.contains(id) }



