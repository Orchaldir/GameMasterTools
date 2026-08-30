package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.ColorSchemeGroupId


fun State.canDeleteColorSchemeGroup(id: ColorSchemeGroupId) = DeleteResult(id)



