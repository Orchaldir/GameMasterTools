package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.WarId

fun State.canDeleteWar(war: WarId) = false
