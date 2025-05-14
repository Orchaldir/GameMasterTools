package at.orchaldir.gm.core.selector.source

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.source.DataSourceId

fun State.canDeleteDataSource(source: DataSourceId) = false
